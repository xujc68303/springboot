package com.xjc.mysql.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xjc.mysql.mapper.dao.TestUserMapper;
import com.xjc.mysql.mapper.dataobject.TestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName PageServiceImpl
 * @Author jiachenXu
 * @Date 2020/3/10
 * @Description
 */
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private TestUserMapper testUserMapper;

    @Override
    public PageInfo<TestUser> selectAll(int pageNum, int size) {
        PageHelper.startPage(pageNum, size);
        List<TestUser> testUserList = testUserMapper.selectAll();
        return new PageInfo<>(testUserList);
    }

    @Override
    public TestUser selectById(long id) {
        return testUserMapper.selectByPrimaryKey(id);
    }
}
