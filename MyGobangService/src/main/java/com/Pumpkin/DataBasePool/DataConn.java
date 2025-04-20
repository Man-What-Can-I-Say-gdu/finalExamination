package com.Pumpkin.DataBasePool;

import java.sql.Connection;

public class DataConn {
    /*
     *@author 用于封装连接池的对象和开始使用的时间，只能由ConnectionPool调用
     */
    public Connection conn;
    //受保护的方法，防止使用的时间被更改导致长时间占用连接
    protected Long StartTime;

    public DataConn(Connection conn, Long startTime) {
        this.conn = conn;
        StartTime = startTime;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Long getStartTime() {
        return StartTime;
    }

    public void setStartTime(Long startTime) {
        StartTime = startTime;
    }


}
