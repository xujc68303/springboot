package com.datasource.mapper.dao;

import com.datasource.mapper.dataobject.TestUser;

import java.util.List;

/**
 * @author Administrator
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