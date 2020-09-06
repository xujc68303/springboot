package com.datasource.service;

import com.datasource.mapper.dataobject.TestUser;
import com.github.pagehelper.PageInfo;

/**
 * @Version 1.0
 * @ClassName PageService
 * @Author jiachenXu
 * @Date 2020/3/10
 * @Description
 */
public interface PageService {

    PageInfo selectAll(int page, int size);

    TestUser selectById(long id);
}
