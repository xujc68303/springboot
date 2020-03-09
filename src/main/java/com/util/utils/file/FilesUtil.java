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
     * @param cs   编码
     * @return 执行结果
     */
    Boolean createFile(String path, String cs);

    /**
     * 创建路径
     *
     * @param path 路径
     * @param cs   编码
     * @return 执行结果
     */
    Boolean createDirectories(String path, String cs);

    /**
     * 写入文件
     *
     * @param path  路径
     * @param count 文件内容
     * @return 执行结果
     */
    Boolean write(String path, String count, String cs);

    /**
     * 读取文件
     *
     * @param path 路径
     * @param cs   编码
     * @return 文件内容
     */
    String read(String path, String cs);

    /**
     * 删除文件
     *
     * @param path 路径
     * @param cs 编码
     * @return 执行结果
     */
    Boolean delete(String path, String cs);

}