package com.pumpkin.controller;

import com.pumpkin.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserController {
    /**
     * 实现注册功能交互
     */


    void Login(User user, HttpServletResponse response);

    /**
     *
     * 实现登录功能交互
     */
    void Register(User user,HttpServletResponse response);

    /**
     * 实现登录前修改密码
     */
    void ModifyPasswordBeforeLogin(User user,String newPassword,HttpServletResponse response);
}
