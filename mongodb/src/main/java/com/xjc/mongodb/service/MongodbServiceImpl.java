package com.xjc.mongodb.service;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import com.sun.istack.NotNull;
import com.xjc.mongodb.api.MongodbService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName MongodbServiceImpl
 * @Author jiachenXu
 * @Date 2020/8/26 22:12
 * @Description
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class MongodbServiceImpl implements MongodbService {

    private static final String ID = "_id";

    private static final String UTF8 = "UTF-8";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    /**
     * 基础查询条件构建
     *
     * @param query     query
     * @param key       查询条件字段
     * @param condition 查询条件
     * @param limit     分页数
     */
    private void builderQuery(@NotNull Query query, String key, Object condition, int limit) {
        query.addCriteria(Criteria.where(key).in(condition));
        if (limit != 0) {
            query.limit(limit);
        }
    }

    /**
     * 按照字段排序
     *
     * @param query query
     * @param desc  是否倒序
     * @param field 排序字段
     */
    private void orderByField(@NotNull Query query, boolean desc, String field) {
        if (field == null) {
            field = ID;
        }
        Sort.Order order;
        if (desc) {
            order = Sort.Order.desc(field);
        } else {
            order = Sort.Order.asc(field);
        }
        query.with(Sort.by(order));
    }


    @Override
    public boolean exists(String collectionName) {
        return mongoTemplate.collectionExists(collectionName);
    }

    @Override
    public String createCollection(String collectionNam) {
        if (exists(collectionNam)) {
            return collectionNam;
        }
        return mongoTemplate.createCollection(collectionNam).getNamespace( ).getCollectionName( );
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return mongoTemplate.getCollection(collectionName);
    }

    @Override
    public boolean dropCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
        return Boolean.TRUE;
    }

    @Override
    public boolean insert(Object entity, String collectionName) {
        return mongoTemplate.insert(entity, collectionName) != null;
    }

    @Override
    public boolean insertAll(List<Object> entitys, String collectionName) {
        entitys.forEach(x -> this.insert(x, collectionName));
        return true;
    }

    @Override
    public boolean remove(String key, Object condition, int limit, Object entity, String collectionName) {
        Query query = new Query( );
        this.builderQuery(query, key, condition, limit);
        return mongoTemplate.remove(query, entity.getClass( ), collectionName).wasAcknowledged( );
    }

    @Override
    public boolean removeAll(Object entity, String collectionName) {
        return mongoTemplate.remove(entity, collectionName).wasAcknowledged( );
    }

    @Override
    public boolean update(String key, Object condition, String field, Object newData, boolean multi,
                          Object entity, String collectionName) {
        Query query = new Query( );
        this.builderQuery(query, key, condition, 0);
        Update update = Update.update(field, condition);
        UpdateResult updateResult = null;
        if (multi) {
            updateResult = mongoTemplate.updateMulti(query, update, entity.getClass( ), collectionName);
        } else {
            updateResult = mongoTemplate.updateFirst(query, update, entity.getClass( ), collectionName);
        }
        return updateResult.wasAcknowledged( );
    }

    @Override
    public List<Object> findFilter(String key, Object condition, Object entity, String collectionName) {
        FindIterable<Document> findIterable =
                this.getCollection(collectionName).find( ).filter(Filters.eq(key, condition));
        MongoCursor<Document> mongoCursor = findIterable.iterator( );
        List<Object> result = Lists.newLinkedList( );
        while (mongoCursor.hasNext( )) {
            result.add(mongoCursor.tryNext( ));
        }
        return result;
    }

    @Override
    public Object findFirst(String collectionName) {
        return this.getCollection(collectionName).find( ).first( );
    }

    @Override
    public List<Object> findPagination(String key, Object condition, int pageNum, int pageSize,
                                       Object entity, boolean desc, String field, String collectionName) {
        Query query = new Query( );
        this.builderQuery(query, key, condition, pageSize);
        this.orderByField(query, desc, field);
        query.skip((pageNum - 1) * pageSize);
        return mongoTemplate.find(query, (Class<Object>) entity.getClass( ), collectionName);
    }

    @Override
    public Object findById(String id, Object entity, String collectionName) {
        return mongoTemplate.findById(id, entity.getClass( ), collectionName);
    }

    @Override
    public List<Object> findFieldOrder(boolean desc, String field, Object entity, String collectionName) {
        Query query = new Query( );
        this.orderByField(query, desc, field);
        return mongoTemplate.find(query, (Class<Object>) entity.getClass( ), collectionName);
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream( );
        String fileName = new String(file.getOriginalFilename( ).getBytes(UTF8));
        ObjectId objectId = gridFsTemplate.store(inputStream, fileName, file.getContentType( ));
        return objectId + "-" + fileName;
    }

    @Override
    public void downloadFile(String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        GridFSFile gridFSFile = this.findFileById(id);
        if (gridFSFile != null) {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb( ));
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId( ));
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //防止文件名称乱码
            String fileName = new String(gridFsResource.getFilename( ).getBytes(UTF8));
            response.setContentType(gridFsResource.getContentType( ));
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setCharacterEncoding(UTF8);

            IOUtils.copy(gridFsResource.getInputStream( ), response.getOutputStream( ));
            response.flushBuffer( );
        }
    }

    @Override
    public GridFSFile findFileById(String id) {
        Query query = new Query( );
        this.builderQuery(query, ID, id, 0);
        return gridFsTemplate.findOne(query);
    }

    @Override
    public boolean deleteFile(String id) {
        Query query = new Query( );
        this.builderQuery(query, ID, id, 0);
        gridFsTemplate.delete(query);
        return Boolean.TRUE;
    }


}
