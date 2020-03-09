package com.util.utils.file;

/**
 * @Version 1.0
 * @ClassName FilesUtil
 * @Author jiachenXu
 * @Date 2020/3/8 21:57
 * @Description 更快捷文件操作 JDK7
 */
public interface FilesUtil {

    /**
     * 创建文件
     *
     * @param path 路径
     * @return 执行结果
     */
    Boolean createFile(String path);

    /**
     * 创建路径
     *
     * @param path 路径
     * @return 执行结果
     */
    Boolean createDirectories(String path);

    /**
     * 写入文件
     *
     * @param path  路径
     * @param count 文件内容
     * @return 执行结果
     */
    Boolean write(String path, String count);

    /**
     * 读取文件
     *
     * @param path 路径
     * @return 文件内容
     */
    String read(String path);

    /**
     * 删除文件、目录
     *
     * @param path 路径
     * @return 执行结果
     */
    Boolean delete(String path);

    /**
     * 复制文件
     * @param oldPath 原有路径
     * @param newPath 新路径
     * @return 执行结果
     */
    Boolean copy(String oldPath, String newPath);

}