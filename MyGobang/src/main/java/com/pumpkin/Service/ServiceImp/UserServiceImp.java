package com.pumpkin.Service.ServiceImp;

import com.pumpkin.Dao.DaoImp.UserDaoImp;
import com.pumpkin.Service.UserService;
import com.pumpkin.entity.User;

import java.util.ArrayList;

public class UserServiceImp implements UserService {
    private UserDaoImp userDaoImp = new UserDaoImp();

    public UserServiceImp(UserDaoImp userDaoImp) {
        this.userDaoImp = userDaoImp;
    }

    public UserServiceImp() {}

    @Override
    public boolean Register(User user) {
        if(userDaoImp.selectUserByName(user.getName())==null) {
            boolean result = userDaoImp.insertUser(user);
            if (result) {
                userDaoImp.updateUserSalt(user.getId());
                return true;
            }
        }
        return false;
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

    public byte[] getUserSalt(int id){
        return userDaoImp.getSalt(id);
    }
    public boolean updateUserSalt(int id) {
        return userDaoImp.updateUserSalt(id);
    }
}
