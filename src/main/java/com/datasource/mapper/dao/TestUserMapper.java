package com.datasource.mapper.dao;

import com.datasource.mapper.dataobject.TestUser;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface TestUserMapper {
    int updateBatch(@Param("list") List<TestUser> list);

    int updateBatchSelective(@Param("list") List<TestUser> list);

    int batchInsert(@Param("list") List<TestUser> list);

    int insertOrUpdate(TestUser record);

    int insertOrUpdateSelective(TestUser record);

    List<TestUser> selectByAll(TestUser testUser);

    List<TestUser> selectAll();

    TestUser selectById(@Param("id") Long id);
}