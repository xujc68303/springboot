package com.xjc.mysql.mapper.dao;

import com.xjc.mysql.mapper.dataobject.TestUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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