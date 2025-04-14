package com.pumpkin.Service.ServiceImp;

import com.pumpkin.Dao.DaoImp.UserDaoImp;
import com.pumpkin.Service.UserService;
import com.pumpkin.entity.User;

import java.util.ArrayList;

public class UserServiceImp implements UserService {
    UserDaoImp userDaoImp;
    public UserServiceImp() {}
    @Override
    public boolean Register(User user) {
        return userDaoImp.insertUser(user);
    }

    @Override
    public User Login(User user) {
        return userDaoImp.selectUserByNameAndPassword(user);
    }

    @Override
    public boolean ModifyPasswordBeforeLogin(User user, String newPassword) {
        return userDaoImp.updatePasswordBeforeLogin(user, newPassword);
    }

    @Override
    public boolean ModifyPasswordAfterLogin(User user, String newPassword) {
        return userDaoImp.updateUserAfterLogin(user, newPassword);
    }

    @Override
    public boolean ModifyPhoneNumber(User user, String newPhoneNumber) {
        return userDaoImp.updatePhone(user, newPhoneNumber);
    }

    @Override
    public boolean ModifyEmail(User user, String newEmail) {
        return userDaoImp.updateEmail(user,newEmail);
    }

    @Override
    public ArrayList<User> selectAllUsers() {
        return userDaoImp.selectAllUsers();
    }

    @Override
    public User selectUserByName(String name) {
        return userDaoImp.selectUserByName(name);
    }
}
