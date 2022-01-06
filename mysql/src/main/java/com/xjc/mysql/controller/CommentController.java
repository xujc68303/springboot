package com.xjc.mysql.controller;

import com.xjc.mysql.model.AppraiseVO;
import com.xjc.mysql.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author jiachenxu
 * @Date 2022/1/6
 * @Descripetion
 */
@RestController
@RequestMapping(value = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/queryComment")
    public List<AppraiseVO> queryComment() {
        return commentService.queryAppraise();
    }

}
