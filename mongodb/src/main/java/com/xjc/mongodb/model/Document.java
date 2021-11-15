package com.xjc.mongodb.model;


import java.io.Serializable;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName Document
 * @Author jiachenXu
 * @Date 2020/9/1
 * @Description mongodb请求模型
 */

public class Document implements Serializable {

    private static final long serialVersionUID = 8705881005182365835L;

    private String id;

    private String title;

    private String description;

    private String group;

    private List<String> tags;

    private Integer likes;

    public Document() {
    }

    public Document(String id, String title, String description, String group, List<String> tags, Integer likes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.group = group;
        this.tags = tags;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
