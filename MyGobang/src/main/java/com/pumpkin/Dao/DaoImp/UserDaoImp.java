package com.pumpkin.Dao.DaoImp;

import DataBasePool.ConnectionPool;
import com.pumpkin.Dao.UserMapper;
import com.pumpkin.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserDaoImp implements UserMapper {
    User user;

    public UserDaoImp(User user) {
        this.user = user;
    }


    @Override
    public boolean insertUser(User user) {
        //操纵数据库添加数据
        String insertSQL = "insert into user (name,password,phonenumber,email) value (?,?,?,?)";
        try {
            //从数据库中初始化连接池并获得对象
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, user.getName());
            //对密码进行加密
            String password = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(12));
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, user.getPhoneNumber());
            preparedStatement.setString(4, user.getEmail());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean updatePhone(User user) {
        //修改手机号的功能在登录后才可以使用，此时已经获得了用户的所有数据
        String updateSQL = "update user set phonenumber=? where id=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, user.getPhoneNumber());
            preparedStatement.setInt(2, user.getId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateEmail(User user) {
        String updateSQL = "update user set email=? where id=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setInt(2, user.getId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User selectUserByNameAndPassword(User user) {
        String selectSQL = "select * from user where name=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, user.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            if(resultSet.next()) {
                //对获得的password进行比对，如果正确则对user进行赋值，如果错误则返回null
                String password = resultSet.getString("password");
                if(BCrypt.checkpw(user.getPassword(),password)){
                    user.setId(resultSet.getInt("id"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPhoneNumber(resultSet.getString("phonenumber"));
                }
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updatePasswordBeforeLogin(User user, String newPassword) {
        //这里是在登录前进行更改，只知道登录的账号名
        String updateSQL = "update user set password=? where name=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, user.getName());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<User> selectAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String selectSQL = "select * from user";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setPhoneNumber(resultSet.getString("phonenumber"));
                user.setEmail(resultSet.getString("email"));
                users.add(user);
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User selectUserByName(String name) {
        String selectSQL = "select * from user where name=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            if(resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setEmail(resultSet.getString("email"));
                user.setPhoneNumber(resultSet.getString("phonenumber"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateUserAfterLogin(User user) {
        String SQL = "update user set password=? where id=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            //进行数据加密
            String password = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(15));
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, user.getId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
