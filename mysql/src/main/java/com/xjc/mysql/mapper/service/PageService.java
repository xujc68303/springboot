package com.xjc.mysql.mapper.service;

import com.github.pagehelper.PageInfo;
import com.xjc.mysql.mapper.dataobject.TestUser;

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
