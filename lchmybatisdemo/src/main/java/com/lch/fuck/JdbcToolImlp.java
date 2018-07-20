package com.lch.fuck;

import com.lch.Entity.RepositoryInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 操作数据库的主逻辑
 * conn没能复用 每次连接都要去new一个新的conn
 * 如果只是一个数据库的话 可以改成将conn做成单例 然后交由spring管理
 *
 * 只是一个简单的demo 远不能在日常的工作中使用
 * @author dly_lee
 */

@Slf4j
public class JdbcToolImlp implements JdbcTool {

    @Override
    public Object find(RepositoryInfo info, String sql, Object[] args,Type type) throws Exception {


        var conn = getConn(info);

        log.info("sql -> {}",sql);

        // 无参数的情况
        if (args == null){
            var pstmt = conn.prepareStatement(sql);
            var rs = pstmt.executeQuery();
            return populate(rs , type);
        }

        // 有参数的情况
        var pstmt = conn.prepareStatement(sql);
        for (int i = 1; i <= args.length ; i++) {
            pstmt.setString(i, String.valueOf(args[i-1]));
        }
        var rs = pstmt.executeQuery();

        return populate(rs , type);
    }

    @Override
    public Integer insertAndUpdate(RepositoryInfo info, String sql, Object[] args, Type type) throws Exception {
        var conn = getConn(info);
        log.info("sql -> {}",sql);

        // 无参数的情况
        if (args == null){
            var pstmt = conn.prepareStatement(sql);
            return  pstmt.executeUpdate();
        }

        // 有参数的情况
        var pstmt = conn.prepareStatement(sql);
        for (int i = 1; i <= args.length ; i++) {
            pstmt.setString(i, String.valueOf(args[i-1]));
        }
        return pstmt.executeUpdate();
    }


    private Connection getConn(RepositoryInfo info){
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://"+info.getUrl()+"/"+info.getDbTable();

        log.info("DBurl -> {}",url);

        Connection conn = null;
        try {
            Class.forName(driver);
            if (info.getUsername() != null && info.getPassword() != null){
                var username = info.getUsername();
                var password = info.getPassword();
                conn = DriverManager.getConnection(url,username,password);
            }else {
                conn = DriverManager.getConnection(url);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private Object populate(ResultSet rs , Type type) throws Exception{

        Class<?> clazz;
        if (type instanceof ParameterizedType){
            clazz = (Class<?>)((ParameterizedType) type).getActualTypeArguments()[0];
            log.info(clazz.getName());

            List<Object> list = new ArrayList<>();

            while(rs.next()){
                var obj = resultSetToList(rs, clazz);
                list.add(obj);
            }
            return list;
        }else {

            clazz = (Class<?>)type;
            if (rs.next()) {
                return resultSetToList(rs, clazz);
            }else {
                return null;
            }

        }
    }

    private Object resultSetToList(ResultSet rs,Class cls) throws Exception {

        Object obj ;

        var fields = cls.getDeclaredFields();

        ResultSetMetaData meta=rs.getMetaData();

        obj = Class.forName(cls.getName()).newInstance();

        for(int i=1;i<=meta.getColumnCount();i++) {

            String colName=meta.getColumnName(i);

            for (var f : fields){
                if (f.getName().equalsIgnoreCase(colName)){
                    f.setAccessible(true);
                    if (f.getType().getName().equals(String.class.getName())){
                        var value = rs.getString(i);
                        f.set(obj,value);
                    }else if(f.getType().getName().equals(int.class.getName())|
                            f.getType().getName().equals(Integer.class.getName())){
                        var value = rs.getInt(i);
                        f.set(obj,value);
                    }else {
                        var value = rs.getObject(i);
                        f.set(obj,value);
                    }

                }
            }

        }
        return obj;

    }
}
