package com.pumpkin.controller.ControllerImp;

import com.alibaba.fastjson2.JSON;
import com.pumpkin.Service.ServiceImp.UserServiceImp;
import com.pumpkin.controller.UserController;
import com.pumpkin.entity.User;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.pumpkin.tool.entry.entry.hamcsha256;

@WebServlet("/User/*")
public class UserControllerImp extends HttpServlet implements UserController {
    UserServiceImp userServiceImp = new UserServiceImp();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorHeader(request, response);

    }
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     * @param request 前端发送的请求
     * @param response 后台发送的响应
     * @throws ServletException
     * @throws IOException
     */
    private void setCorHeader(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        response.addHeader("Access-Control-Allow-Origin", origin);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    /**
     * 解析前端发送的json字符串
     * @param request 前端传入的请求
     * @return
     */
    private User parseToUser(HttpServletRequest request) {
        //从请求头中获取user对象
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = request.getReader();
            String line;
            while((line = bf.readLine())!=null){
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //返回解析的User对象
        return JSON.parseObject(sb.toString(), User.class);
    }












    @Override
    public void Login(User user) {
        User LoginUser = userServiceImp.Login(user);
        if(LoginUser != null){
           user = LoginUser;
           //将User数据存放到token并传回
            hamcsha256(user.getId()+user.getName(), user.getPassword().getBytes());
        }
    }

    @Override
    public void Register(User user) {

    }

    @Override
    public void ModifyPasswordBeforeLogin(User user, String newPassword) {

    }
}
