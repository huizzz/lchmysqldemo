package com.lch.Entity;

import lombok.Data;

/**
 *
 * 用于传递注解上的信息
 * @author dly_lee
 */
@Data
public class RepositoryInfo {

    private String url;

    private String dbTable;

    private String username;

    private String password;
}
