package com.xjc.zookeeper.service;

import com.xjc.zookeeper.api.ZookeeperService;
import com.xjc.zookeeper.config.ZookeeperConfig;
import com.xjc.zookeeper.object.InstanceDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.TransactionOp;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.queue.DistributedIdQueue;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @Version 1.0
 * @ClassName ZookeeperServiceImpl
 * @Author jiachenXu
 * @Date 2020/7/26 15:01
 * @Description
 */
@Slf4j
@Service
public class ZookeeperServiceImpl implements ZookeeperService {

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    private int nThreads = 10;

    private static volatile int countAdd;

    private TransactionOp transactionOp;

    private Stream<Object> leaderId;

    private volatile InterProcessMutex interProcessMutex;


    private volatile DistributedAtomicInteger distributedAtomicInteger;

    private volatile LeaderLatch leaderLatch;

    private List<LeaderLatch> leaderLatchList;

    private ExecutorService executorService = Executors.newFixedThreadPool(this.nThreads);

    private ServiceDiscovery<Object> serviceDiscovery;

    private DistributedQueue distributedQueue;

    private DistributedIdQueue distributedIdQueue;

    private List<Object> messageList = new ArrayList<>( );

    private volatile InterProcessReadWriteLock readWriteLock;

    @Override
    public boolean exists(String path) throws Exception {
        path = checkPath(path);
        zookeeperConfig.addListener(curatorFramework, path);
        return curatorFramework.checkExists( ).forPath(path) != null;
    }

    @Override
    public boolean asyncExists(String path) throws Exception {
        return curatorFramework.checkExists( ).inBackground((client, event) -> {
            log.warn("asyncExists, path={}, data={}", event.getPath( ), event.getData( ));
        }, executorService).forPath(path) != null;
    }

    @Override
    public TransactionOp transaction() {
        if (this.transactionOp == null) {
            this.transactionOp = curatorFramework.transactionOp( );
        }
        return transactionOp;
    }

    @Override
    public String createNode(String path) throws Exception {
        if (this.exists(path)) {
            return null;
        }
        return curatorFramework.create( )
                .creatingParentsIfNeeded( )
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }

    @Override
    public String createNode(String path, String data) throws Exception {
        if (this.exists(path)) {
            return null;
        }
        return curatorFramework.create( )
                .creatingParentsIfNeeded( )
                .withMode(CreateMode.PERSISTENT)
                .forPath(path, data.getBytes( ));
    }

    @Override
    public String getData(String path) throws Exception {
        if (this.exists(path)) {
            return null;
        }
        return new String(curatorFramework.getData( ).forPath(path));
    }

    @Override
    public String synGetData(String path) throws Exception {
        curatorFramework.sync( );
        return this.getData(path);
    }

    @Override
    public String getStat(String path) throws Exception {
        if (this.exists(path)) {
            return null;
        }
        return new String(curatorFramework.getData( ).storingStatIn(new Stat( )).forPath(path));
    }

    @Override
    public boolean updateNode(String path, String newData) throws Exception {
        return this.updateNode(path, newData, -1);
    }

    @Override
    public boolean updateNode(String path, String data, int version) throws Exception {
        if (!this.exists(path)) {
            return false;
        }
        return curatorFramework.setData( ).withVersion(version).forPath(path, data.getBytes( )) != null;
    }

    @Override
    public boolean deleteNode(String path, Boolean deleteChildren) throws Exception {
        return this.deleteNode(path, deleteChildren, -1);
    }

    @Override
    public boolean deleteNode(String path, Boolean deleteChildren, int version) throws Exception {
        Objects.requireNonNull(path);
        if (deleteChildren) {
            curatorFramework.delete( )
                    .guaranteed( )
                    .deletingChildrenIfNeeded( )
                    .withVersion(version)
                    .forPath(path);
        } else {
            curatorFramework.delete( )
                    .guaranteed( )
                    .withVersion(version)
                    .forPath(path);
        }
        return true;
    }

    @Override
    public boolean asyncDeleteNode(String path, Boolean deleteChildren) throws Exception {
        return this.asyncDeleteNode(path, deleteChildren, -1);
    }

    @Override
    public boolean asyncDeleteNode(String path, Boolean deleteChildren, int version) throws Exception {
        if (this.exists(path)) {
            return false;
        }
        if (deleteChildren) {
            curatorFramework.delete( )
                    .guaranteed( )
                    .deletingChildrenIfNeeded( )
                    .withVersion(version)
                    .inBackground((client, event) -> {
                        log.warn("asyncDeleteNode, path={}, data={}", event.getPath( ), event.getData( ));
                    }, executorService).forPath(path);
        } else {
            curatorFramework.delete( )
                    .guaranteed( )
                    .withVersion(version)
                    .inBackground((client, event) -> {
                        log.warn("asyncDeleteNode, path={}, data={}", event.getPath( ), event.getData( ));
                    }, executorService).forPath(path);
        }
        return true;
    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        if (!this.exists(path)) {
            return null;
        }
        return curatorFramework.getChildren( ).forPath(path);
    }

    @Override
    public boolean distributed(String path, int count) throws Exception {
        return this.distributed(path, 10, -1, TimeUnit.MINUTES);
    }

    @Override
    public boolean distributed(String path, int count, long time, TimeUnit unit) throws Exception {
        if (this.exists(path)) {
            return false;
        }
        this.interProcessMutex = new InterProcessMutex(this.curatorFramework, path);
        if (count != 0) {
            while (true) {
                if (countAdd == 10) {
                    break;
                }
                if (this.interProcessMutex.acquire(time, unit)) {
                    return true;
                } else {
                    countAdd++;
                }
            }
        } else {
            return this.interProcessMutex.acquire(time, unit);
        }
        return false;
    }

    @Override
    public boolean releaseDistributed(String path) throws Exception {
        if (!this.exists(path)) {
            return false;
        }
        if (this.interProcessMutex != null) {
            this.interProcessMutex.release( );
            this.asyncDeleteNode(path, true);
        }
        return true;
    }

    @Override
    public AtomicValue<Integer> distributedCountAdd(String path, int delta, int retryTime, int sleepMsBetweenRetries) throws Exception {
        if (!this.exists(path)) {
            return null;
        }
        RetryNTimes retryNTimes = new RetryNTimes(retryTime, sleepMsBetweenRetries);
        this.distributedAtomicInteger = new DistributedAtomicInteger(this.curatorFramework, path, retryNTimes);
        return this.distributedAtomicInteger.add(delta);
    }

    @Override
    public AtomicValue<Integer> distributedCountSubtract(String path, int delta) throws Exception {
        if (!this.exists(path)) {
            return null;
        }
        if (distributedAtomicInteger != null) {
            return distributedAtomicInteger.subtract(delta);
        }
        return null;
    }

    @Override
    public String leaderLatch(int clientCount, String path) throws Exception {
        if (!this.exists(path)) {
            return null;
        }
        for (int i = 0; i < clientCount; i++) {
            this.leaderLatch = new LeaderLatch(curatorFramework, path, String.valueOf(i));
            leaderLatchList.add(leaderLatch);
            this.leaderLatch.start( );
        }

        do {
            checkLeader(leaderLatchList);
        } while (this.leaderId == null);

        this.leaderLatch.addListener(new LeaderLatchListener( ) {
            @Override
            public void isLeader() {
                log.warn(leaderId + "抢主成功，现在晋升为master");
            }

            @Override
            public void notLeader() {
                log.warn(leaderId + "抢主失败，现在继续选举master");
            }
        });
        return "path=" + this.leaderLatch.getOurPath( ) + "leaderId" + this.leaderId;
    }

    @Override
    public InterProcessReadWriteLock getReadWriteLock(String path) throws Exception {
        if (!this.exists(path)) {
            return null;
        }
        if (readWriteLock == null) {
            readWriteLock = new InterProcessReadWriteLock(this.curatorFramework, path);
        }
        return readWriteLock;
    }

    @Override
    public ServiceInstance<Object> registryService(String path, String serviceName, Object data) throws Exception {
        ServiceInstance<Object> serviceInstance = this.builderServiceInstance(serviceName, data);
        if (serviceDiscovery == null) {
            serviceDiscovery = this.builderServiceDiscovery(path, data);
        }
        serviceDiscovery.registerService(serviceInstance);
        serviceDiscovery.start( );
        return serviceInstance;
    }

    @Override
    public void unregisterService(String path, String serviceName, Object data, ServiceInstance<Object> serviceInstance) throws Exception {
        if (serviceDiscovery == null) {
            serviceDiscovery = this.builderServiceDiscovery(path, data);
        }
        serviceDiscovery.unregisterService(serviceInstance);
        serviceDiscovery.start( );
    }

    @Override
    public List<Object> getServices(String path, String serviceName, Object data) throws Exception {
        ServiceDiscovery<Object> serviceDiscovery = this.builderServiceDiscovery(path, data);
        serviceDiscovery.start( );
        return this.queryForInstances(serviceDiscovery, serviceName);
    }

    /**
     * 不停的获取服务列表
     *
     * @param serviceDiscovery
     * @param servieName
     * @return
     * @throws Exception
     */
    private List<Object> queryForInstances(ServiceDiscovery<Object> serviceDiscovery, String servieName) throws Exception {
        List<Object> result = new ArrayList<>( );
        Collection<ServiceInstance<Object>> services;
        services = serviceDiscovery.queryForInstances(servieName);
        if (services == null) {
            while (true) {
                services = serviceDiscovery.queryForInstances(servieName);
                if (services != null) {
                    break;
                }
            }
        } else {
            services.forEach(x -> {
                Object p = x.getPayload( );
                InstanceDetails payload = null;
                if (p instanceof InstanceDetails) {
                    payload = (InstanceDetails) x.getPayload( );
                }
                // 业务描述
                String serviceDesc = payload.getServiceDesc( );
                // 接口列表
                Map<String, Object> maps = payload.getMaps( );

                InstanceDetails instanceDetails = new InstanceDetails(serviceDesc, maps);
                result.add(instanceDetails);
            });
        }
        return result;
    }

    private void checkLeader(List<LeaderLatch> leaderLatchList) throws InterruptedException {
        Thread.sleep(1000);
        if (leaderLatchList.stream( ).allMatch(LeaderLatch::hasLeadership)) {
            this.leaderId = leaderLatchList.stream( ).filter(LeaderLatch::hasLeadership).map(LeaderLatch::getId);
        }
    }

    private ServiceDiscovery<Object> builderServiceDiscovery(String path, Object payloadClass) {
        return ServiceDiscoveryBuilder.builder(Object.class)
                .client(this.curatorFramework)
                .serializer(new JsonInstanceSerializer<>(Object.class))
                .basePath(path)
                .thisInstance((ServiceInstance<Object>) payloadClass)
                .build( );
    }

    /**
     * 将服务添加到 ServiceInstance
     *
     * @param serviceName
     * @param payload
     * @return
     * @throws Exception
     */
    private ServiceInstance<Object> builderServiceInstance(String serviceName, Object payload) throws Exception {
        ServiceInstanceBuilder<Object> serviceInstanceBuilder = ServiceInstance.builder( );
        return serviceInstanceBuilder
                .address("127.0.0.1")
                .port(9090)
                .name(serviceName)
                .payload(payload)
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build( );
    }

    private String checkPath(String path) {
        Objects.requireNonNull(path);
        if (path.indexOf("/") == 0) {
            return path;
        }
        return "/" + path;
    }

}
