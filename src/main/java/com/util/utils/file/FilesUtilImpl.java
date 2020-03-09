package com.util.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * @Version 1.0
 * @ClassName FilesUtil
 * @Author jiachenXu
 * @Date 2020/3/8 21:53
 * @Description JDK7新特性，更快捷创建、读取文件
 */
@Slf4j
@Service
public class FilesUtilImpl implements FilesUtil {

    private static final Long SPACE_MAX = 100L;

    @Autowired
    private static StopWatch stopWatch;

    @Override
    public Boolean createFile(String path) {
        StopWatch stopWatch = null;
        try {
            stopWatch = getWatch("FilesUtilImpl-createFile");

            checkSpace(path);
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                Files.createFile(dirPath);
                log.info("FileUtil-createFile success, runTime=", stopWatch.getTotalTimeMillis( ));
                return true;
            }
        } catch (Exception e) {
            log.error("FileUtil-createFile error, runTime=", stopWatch.getTotalTimeMillis( ), e);
        } finally {
            if(stopWatch != null){
                stopWatch.stop();
            }
        }
        return false;
    }

    @Override
    public Boolean createDirectories(String path) {
        try {
            checkSpace(path);
            Path dirPath = Paths.get(path);
            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("FileUtil-createDirectories success, runTime=", stopWatch.getTotalTimeMillis( ));
                return true;
            }
        } catch (Exception e) {
            log.error("FileUtil-createDirectories error, runTime=", stopWatch.getTotalTimeMillis( ), e);
        }
        return false;
    }

    @Override
    public Boolean write(String path, String count, String cs) {
        BufferedWriter writer = null;
        try {
            checkSpace(path);

            Path dirPath = Paths.get(path);

            if (!StringUtils.isEmpty(count.trim( ))) {
                writer = Files.newBufferedWriter(dirPath, APPEND);
                writer.write(count);
                writer.flush( );
                log.info("FileUtil-write success");
                return true;
            }
        } catch (Exception e) {
            log.error("FileUtil-write error", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close( );
                }
            } catch (IOException e) {
                log.error("FileUtil-write close error", e);
            }
        }

        return false;
    }


    @Override
    public String read(String path) {
        BufferedReader reader = null;
        String line;
        try {
            Path dirPath = Paths.get(path);
            reader = Files.newBufferedReader(dirPath, StandardCharsets.UTF_8);
            while ((line = reader.readLine( )) != null) {
                log.info("FileUtil-read success");
                return line;
            }
        } catch (Exception e) {
            log.error("FileUtil-read error", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close( );
                }
            } catch (IOException e) {
                log.error("FileUtil-write read error", e);
            }
        }
        return null;
    }

    @Override
    public Boolean delete(String path) {
        try {
            Path dirPath = Paths.get(path);
            if (Files.exists(dirPath)) {
                return Files.deleteIfExists(dirPath);
            }
        } catch (Exception e) {
            log.error("FileUtil-delete error", e);
        }
        return false;
    }

    @Override
    public Boolean copy(String oldPath, String newPath) {
        try {
            checkSpace(newPath);
            Files.copy(Paths.get(oldPath), Paths.get(newPath));
            return true;
        } catch (Exception e) {
            log.error("FileUtil-copy error", e);
        }
        return false;
    }

    /**
     * 获取空间，预留参数后期可以改造
     */
    private static void checkSpace(String dir) throws IOException {
        // 分区的总空间
        long totalSpace = 0;
        // 分区的已用空间
        long usableSpace = 0;
        // 分区的剩余空间
        long unallocatedspace = 0;

        dir = dir.substring(0, 2);

        Path path = Paths.get(dir);

        FileStore fileStore = Files.getFileStore(path);

        if (fileStore.isReadOnly( )) {
            unallocatedspace = fileStore.getUnallocatedSpace( );
            if (unallocatedspace <= SPACE_MAX) {
                throw new IllegalArgumentException("可使用空间不足");
            }
        }
    }

    /**
     * 计数器初始化
     *
     * @param functionName 函数
     * @return StopWatch
     */
    private static StopWatch getWatch(String functionName) {
        stopWatch = new StopWatch(functionName);
        stopWatch.start( );
        return stopWatch;
    }

}
