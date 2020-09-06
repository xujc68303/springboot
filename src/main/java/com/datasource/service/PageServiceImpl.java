package com.datasource.service;

import com.datasource.mapper.dao.TestUserDao;
import com.datasource.mapper.dataobject.TestUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class PageServiceImpl implements PageService{

    @Autowired
    private TestUserDao testUserDao;

    @Override
    public PageInfo<TestUser> selectAll(int pageNum, int size) {
        PageHelper.startPage(pageNum, size);
        List<TestUser> testUserList = testUserDao.selectAll();
        return new PageInfo<>(testUserList);
    }

    @Override
    public TestUser selectById(long id) {
        return testUserDao.selectByPrimaryKey(id);
    }
}
