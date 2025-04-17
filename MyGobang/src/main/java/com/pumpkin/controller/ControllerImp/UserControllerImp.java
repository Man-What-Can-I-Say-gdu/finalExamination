package com.pumpkin.controller.ControllerImp;

import com.alibaba.fastjson2.JSON;
import com.mysql.cj.log.Log;
import com.pumpkin.Service.ServiceImp.GamerServiceImp;
import com.pumpkin.Service.ServiceImp.UserServiceImp;
import com.pumpkin.controller.UserController;
import com.pumpkin.entity.User;
import com.pumpkin.tool.entry.entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.pumpkin.tool.entry.ControllerUtils.setCorHeader;


@WebServlet("/User/*")
public class UserControllerImp extends HttpServlet implements UserController {
    UserServiceImp userServiceImp = new UserServiceImp();

    @Override
    public void init(){

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorHeader(request,response);
        User user = parseToUser(request);
        if("/Login".equals(request.getPathInfo())){
            Login(user, response);
        } else if ("/Register".equals(request.getPathInfo())) {
            Register(user,response);
        }
    }
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorHeader(request,response);
        response.setStatus(200);

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
    public void Login(User user, HttpServletResponse response) {
        User LoginUser = userServiceImp.Login(user);
        //用于存放返回的数据
        Map<String,Object> map = new HashMap<>();
        if(LoginUser != null){
            map.put("success",true);
            map.put("gamerId",LoginUser.getId());
            response.setStatus(200);
           user = LoginUser;
                //获取Authorization并转化为json字符串
            try {
                byte[] key = entry.deriveKeyFromPassword(user.getPassword(),user.getSalt());
                map.put("Authorization",buildAuthorization(user.getPassword(), key,user.getId()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            map.put("success",false);
            map.put("message","该用户不存在");
        }
        String responseString = JSON.toJSONString(map);
        //返回Json字符串
        response.setContentType("application/json");
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.print(responseString);
        printWriter.close();
    }

    @Override
    public void Register(User user,HttpServletResponse response) {
        //map为返回的结果
        Map<String,Object> map = new HashMap<>();
        if(userServiceImp.Register(user)){
            //生成随机盐值
            userServiceImp.updateUserSalt(user.getId());
            //同步注册gamer账号
            new GamerServiceImp().insertGamer(user.getId());
            map.put("success",true);
        }else{
            map.put("success",false);
            map.put("message","用户已存在");
        }
        String responseJson = JSON.toJSONString(map);
        response.setContentType("application/json");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(responseJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void ModifyPasswordBeforeLogin(User user, String newPassword,HttpServletResponse response) {
        if(userServiceImp.ModifyPasswordBeforeLogin(user, newPassword)){
            Map<String ,Object> map = new HashMap<>();
            map.put("success",true);
            String responseJson = JSON.toJSONString(map);
            response.setContentType("application/json");
            try {
                PrintWriter printWriter = response.getWriter();
                printWriter.print(responseJson);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * 创建Authorization
     * @return Authorization的map集合
     */
    private static Map<String,String> buildAuthorization(String info,byte[] key,int id) throws Exception {
        Map<String,String> token = new HashMap<>();
        //获取token头
        Map<String, Object> tokenHeader = entry.getTokenHeader(id);
        //获取payload
        String Userinfo = entry.encryUserInfo(String.valueOf(info),key,"AES",false);
        //获取签名
        String signature = entry.getSignature(Userinfo,key,tokenHeader.toString());
        //填充token
        token.put("tokenHeader",tokenHeader.toString());
        token.put("signature",signature);
        token.put("Userinfo",Userinfo);
        return token;
    }

}
