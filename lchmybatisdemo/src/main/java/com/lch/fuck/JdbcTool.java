package com.lch.fuck;

import com.lch.Entity.RepositoryInfo;

import java.lang.reflect.Type;

/**
 *
 * 数据库的操作接口
 * 如有需要可以再改
 * @author dly_lee
 */
public interface JdbcTool {


    Object find(RepositoryInfo info, String sql, Object[] args, Type cls) throws Exception;

    Integer insertAndUpdate(RepositoryInfo info,String sql,Object[] args,Type cls) throws Exception;
}
