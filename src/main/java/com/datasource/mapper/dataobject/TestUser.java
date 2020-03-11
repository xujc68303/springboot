package com.datasource.mapper.dataobject;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestUser implements Serializable {
    private static final long serialVersionUID = 3580865635839853856L;
    private Long id;

    private String userName;

    private Integer userAge;

    private String userBase;
}