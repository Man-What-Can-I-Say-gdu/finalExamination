package com.pumpkin.controller.ControllerImp;

import com.alibaba.fastjson2.JSON;
import com.mysql.cj.log.Log;
import com.pumpkin.Service.ServiceImp.GamerServiceImp;
import com.pumpkin.Service.ServiceImp.UserServiceImp;
import com.pumpkin.controller.UserController;
import com.pumpkin.entity.User;
import com.pumpkin.entity.entryData;
import com.pumpkin.tool.entry.ControllerUtils;
import com.pumpkin.tool.entry.entry;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
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
        System.out.println(request.getPathInfo());
        if("/Login".equals(request.getPathInfo())){
            Login(user, response);
        } else if ("/Register".equals(request.getPathInfo())) {
            Register(user,response);
        }else if("/ModifyEmail".equals(request.getPathInfo())) {
            //获取id,需要获取存放在头中的id并进行解码获得原字符串
            String header = request.getHeader("Authorization").split("\\.")[0];
            try {
                //将header解码获取
                header = Arrays.toString(Hex.decodeHex(header.toCharArray()));
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
            //获取id值
            int id = Integer.parseInt(header.split("&")[2]);
            user.setId(id);
            StringBuffer sb = new StringBuffer();
            BufferedReader bf = request.getReader();
            String line = bf.readLine();
            sb.append(line);
            //获取user对象
            User newEmailVector = JSON.parseObject(sb.toString(),User.class);
            Map<String, Object> returnData = new HashMap<>();
            if(userServiceImp.ModifyEmail(user,newEmailVector.getEmail())){
                returnData.put("success",true);
                response.setStatus(200);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(JSON.toJSONString(returnData));
            }else{
                returnData.put("success",false);
                returnData.put("message","用户不存在");
                response.setStatus(200);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(JSON.toJSONString(returnData));
            }
        }else if("/ModifyPhoneNumb".equals(request.getPathInfo())) {
            //获取id,需要获取存放在头中的id并进行解码获得原字符串
            String header = request.getHeader("Authorization").split("\\.")[0];
            try {
                //将header解码获取
                header = Arrays.toString(Hex.decodeHex(header.toCharArray()));
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
            //获取id值
            int id = Integer.parseInt(header.split("&")[2]);
            user.setId(id);
            StringBuffer sb = new StringBuffer();
            BufferedReader bf = request.getReader();
            String line = bf.readLine();
            sb.append(line);
            //获取user对象
            User newEmailVector = JSON.parseObject(sb.toString(),User.class);
            Map<String, Object> returnData = new HashMap<>();
            if(userServiceImp.ModifyPhoneNumber(user,newEmailVector.getEmail())){
                returnData.put("success",true);
                response.setStatus(200);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(JSON.toJSONString(returnData));
            }else{
                returnData.put("success",false);
                returnData.put("message","用户不存在");
                response.setStatus(200);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(JSON.toJSONString(returnData));
            }
        }else if("/ModifyPassword".equals(request.getPathInfo())) {
            BufferedReader bf = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line = bf.readLine();
            sb.append(line);
            //用于存储旧密码的容器
            User PasswordVector = JSON.parseObject(sb.toString(),User.class);
            //先验证传入的令牌和原先的是否一致，再验证传入的密码和原先的密码是否相同
            String Authorization = request.getHeader("Authorization");
            String[] AuthorizationPart = Authorization.split("&");
            entryData entryData = new entryData(AuthorizationPart[0],AuthorizationPart[1],AuthorizationPart[2]);
            //获取注册时生成的盐
            //获取id,需要获取存放在头中的id并进行解码获得原字符串
            String header = request.getHeader("Authorization").split("\\.")[0];
            try {
                //将header解码获取
                header = Arrays.toString(Hex.decodeHex(header.toCharArray()));
            } catch (DecoderException e) {
                throw new RuntimeException(e);
            }
            //获取id值
            int id = Integer.parseInt(header.split("&")[2]);
            byte[] salt = userServiceImp.getUserSalt(id);
            Map<String,Object> returnData = new HashMap<>();
            try {
                //获取key
                byte[] key = entry.deriveKeyFromPassword(PasswordVector.getPassword(),salt);
                //获取密文并生成签名，对签名进行比对
                if(AuthorizationPart[2].equals(entry.getSignature(entryData.getPayload(),key,AuthorizationPart[0]))){
                    //签名相同,说明密码未更改，进一步判断旧密码与新密码是否一致
                    user=userServiceImp.selectUserById(id);
                    if(user.getPassword().equals(BCrypt.hashpw(PasswordVector.getPassword(),BCrypt.gensalt(12)))){
                        //密码相同，进行修改
                        //获取新密码
                        line=bf.readLine();
                        sb.append(line);
                        PasswordVector = JSON.parseObject(sb.toString(),User.class);
                        //进行更改
                        userServiceImp.ModifyPasswordAfterLogin(user,PasswordVector.getPassword());
                        returnData.put("success",true);
                    }else{
                        returnData.put("success",false);
                    }
                    //返回处理结果
                    response.setContentType("application/json");
                    response.setStatus(200);
                    //更新令牌
                    Authorization = buildAuthorization(PasswordVector.getPassword(),entry.deriveKeyFromPassword(user.getPassword(),user.getSalt()), user.getId());
                    response.setHeader("Authorization",Authorization);
                    PrintWriter out = response.getWriter();
                    out.print(JSON.toJSONString(returnData));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入options");
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
                //传入的时Map数组
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
            user.setId(userServiceImp.selectUserByName(user.getName()).getId());
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
    private static String buildAuthorization(String info,byte[] key,int id) throws Exception {
        String token;
        //获取token头
        String tokenHeader = new String(entry.getTokenHeader(id));
        //获取payload
        String payload = new String(entry.encryUserInfo(String.valueOf(info),false));
        //获取签名
        String signature = new String(entry.getSignature(payload,key, tokenHeader));
        //填充token
        token = tokenHeader+"."+payload+"."+signature;
        return token;
    }

}
