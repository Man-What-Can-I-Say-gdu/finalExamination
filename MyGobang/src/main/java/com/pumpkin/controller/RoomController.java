package com.pumpkin.controller;

import com.pumpkin.entity.Room;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RoomController {
    /**
     * 处理前端传入的room创建请求
     */
    public void buildRoom(HttpServletRequest request, HttpServletResponse response);

    /**
     *处理前端传入的退出房间请求
     */
    public void exitRoom(HttpServletRequest request, HttpServletResponse response);

    /**
     * 处理前端传入的加入房间请求
     */
    public void joinRoom(HttpServletRequest request, HttpServletResponse response);

    /**
     * 处理前端发送的修改房间信息请求
     */
    public void updateRoom(HttpServletRequest request, HttpServletResponse response);

    /**
     *
     */
}
