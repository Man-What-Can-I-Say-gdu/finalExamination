package com.pumpkin.Dao;

import com.pumpkin.entity.User;

import java.util.ArrayList;

public interface UserMapper {
    /**
     * 实现注册功能
     * @param user 添加数据
     */
    boolean insertUser(User user);

    /**
     * 实现修改手机号功能
     * @param user 修改的账号
     */
    boolean updatePhone(User user);

    /**
     * 实现修改邮箱
     * @param user 修改的账号
     */
    boolean updateEmail(User user);

    /**
     * 实现登录功能
     * @param user 登录的账号
     */
    User selectUserByNameAndPassword(User user);

    /**
     * 实现登陆前修改密码功能
     * @param newPassword 新密码
     * @param user 修改密码的账号
     */
    boolean updatePasswordBeforeLogin(User user,String newPassword);

    /**
     * 查找所有用户的信息
     */
    ArrayList<User> selectAllUsers();

    /**
     * 通过名字查找用户信息
     * @param name
     */
    User selectUserByName(String name);

    /**
     * 实现登录后修改密码
     * @param user 登录的用户
     */
    boolean updateUserAfterLogin(User user);
}
