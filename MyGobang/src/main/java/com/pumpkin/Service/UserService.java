package com.pumpkin.Service;

import com.pumpkin.entity.User;

import java.util.ArrayList;

public interface UserService {
    /**
     * 实现注册功能
     * @param user 注册的用户
     */
    boolean Register(User user);

    /**
     * 实现登录功能
     * @param user 登录的用户
     */
    User Login(User user);

    /**
     * 实现登陆前修改密码功能
     * @param user 修改密码的用户
     * @param newPassword 新密码
     */
    boolean ModifyPasswordBeforeLogin(User user,String newPassword);

    /**
     * 实现登陆后修改密码
     * @param user 修改密码的用户
     * @param newPassword 新密码
     */
    boolean ModifyPasswordAfterLogin(User user,String newPassword);

    /**
     * 实现修改手机号码
     * @param newPhoneNumber 新手机号
     * @param user 修改的账号
     */
    boolean ModifyPhoneNumber(User user,String newPhoneNumber);

    /**
     * 实现修改邮箱功能
     * @param user 修改邮箱的用户
     * @param newEmail 新邮箱号
     */
    boolean ModifyEmail(User user,String newEmail);


    /**
     * 实现获取所有用户信息
     */
    ArrayList<User> selectAllUsers();

    /**
     * 实现通过名字查找用户信息
     * @param name 查找信息的用户名
     */
    User selectUserByName(String name);
}
