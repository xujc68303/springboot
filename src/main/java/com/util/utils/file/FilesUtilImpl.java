package com.util.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

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

    @Override
    public Boolean createFile(String path, String cs) {
        try {
            Path dirPath = Paths.get(path);
            checkSpaceSize(dirPath);
            if(Files.notExists(dirPath)){
                Files.newBufferedWriter(dirPath, CREATE);
                log.info("FileUtil-createFile success");
                return true;
            }

        } catch (Exception e) {
            log.error("FileUtil-createFile error", e);
        }
        return false;
    }

    @Override
    public Boolean createDirectories(String path, String cs) {
        try {
            Path dirPath = Paths.get(path);
            Files.createDirectories(dirPath);
            log.info("FileUtil-createDirectories success");
            return true;
        } catch (Exception e) {
            log.error("FileUtil-createDirectories error", e);
        }
        return false;
    }

    @Override
    public Boolean write(String path, String count, String cs) {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            Path dirPath = Paths.get(path);
            if (StringUtils.isEmpty(count)) {

                writer = Files.newBufferedWriter(dirPath, APPEND);
                reader = Files.newBufferedReader(dirPath);
                if (StringUtils.isEmpty(reader.readLine( ))) {
                    writer.write(count);
                }

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
                if (reader != null) {
                    reader.close( );
                }
            } catch (IOException e) {
                log.error("FileUtil-write close error", e);
            }
        }

        return false;
    }


    @Override
    public String read(String path, String cs) {
        BufferedReader reader = null;
        try {
            Path dirPath = Paths.get(path);
            reader = Files.newBufferedReader(dirPath);
            String count = reader.readLine( );

            if (StringUtils.isEmpty(count)) {
                log.info("FileUtil-read success");
                return count;
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
    public Boolean delete(String path, String cs) {
        try {
            Path dirPath = Paths.get(path);
            if (dirPath.toFile().exists()) {
                return Files.deleteIfExists(dirPath);
            }
        } catch (Exception e) {
            log.error("FileUtil-delete error", e);
        }

        return false;
    }

    /**
     * 空间小于50不创建
     * @param dirPath
     * @return
     * @throws IOException
     */
    private void checkSpaceSize(Path dirPath) throws IOException {
        FileStore fileStore = Files.getFileStore(dirPath);
        if(fileStore.getTotalSpace() < 50){
            throw new IllegalArgumentException("最大空间不足");
        }
    }
}
