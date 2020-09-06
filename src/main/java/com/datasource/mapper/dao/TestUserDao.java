package com.datasource.mapper.dao;

import com.datasource.mapper.dataobject.TestUser;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName TestUserDao
 * @Author jiachenXu
 * @Date 2020/5/23
 * @Description
 */
public interface TestUserDao {

    int deleteByPrimaryKey(Long id);

    int insert(TestUser record);

    int insertSelective(TestUser record);

    TestUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TestUser record);

    int updateByPrimaryKey(TestUser record);

    /**
     * 分页插件
     *
     * @return
     */
    List<TestUser> selectAll();
}