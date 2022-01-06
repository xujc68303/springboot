package com.xjc.mysql.service;

import com.google.common.collect.Lists;
import com.xjc.mysql.mapper.dao.CommentMapper;
import com.xjc.mysql.mapper.dao.ReplyMapper;
import com.xjc.mysql.mapper.dataobject.Comment;
import com.xjc.mysql.mapper.dataobject.Reply;
import com.xjc.mysql.model.AppraiseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author jiachenxu
 * @Date 2022/1/6
 * @Descripetion
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ReplyMapper replyMapper;

    public List<AppraiseVO> queryAppraise() {
        List<AppraiseVO> result = Lists.newArrayList();
        Comment comment = new Comment();
        comment.setBrandId(1L);
        comment.setStoreId(1L);
        comment.setGoodsId(1L);
        List<Comment> commentList = commentMapper.queryComment(comment);
        if (CollectionUtils.isEmpty(commentList)) {
            return result;
        }
        Reply reply = new Reply();
        reply.setBrandId(1L);
        reply.setStoreId(1L);
        reply.setGoodsId(1L);
        List<Reply> replyList = replyMapper.queryReply(reply);
        if (1 == commentList.size() && CollectionUtils.isEmpty(replyList)) {
            result.addAll(commentList.stream().map(x -> {
                AppraiseVO appraiseVO = new AppraiseVO();
                appraiseVO.setCommentContent(x.getContent());
                return appraiseVO;
            }).collect(Collectors.toList()));
            return result;
        }
        Map<Long, Reply> replyMap = replyList.stream()
                .collect(Collectors.toMap(Reply::getCommentId, a -> a, (k, v) -> k));
        commentList.forEach(x -> {
            AppraiseVO appraiseVO = new AppraiseVO();
            appraiseVO.setCommentContent(x.getContent());
            Reply reply1 = replyMap.get(x.getId());
            if (Objects.nonNull(reply1)) {
                appraiseVO.setReplyContent(reply1.getContent());
            }
            result.add(appraiseVO);
        });
        return result;
    }
}
