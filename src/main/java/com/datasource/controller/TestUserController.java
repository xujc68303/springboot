package com.datasource.controller;

import com.datasource.service.PageService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Version 1.0
 * @ClassName TestUserController
 * @Author jiachenXu
 * @Date 2020/3/10 14:58
 * @Description
 */
@RestController
@RequestMapping("/test")
public class TestUserController {

    @Autowired
    private PageService pageService;

    @RequestMapping(value = "/selectAll")
    public PageInfo selectAll(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size) {
        return pageService.selectAll(page, size);
    }

}
