package com.pumpkin.controller.ControllerImp;

import com.alibaba.fastjson2.JSON;
import com.pumpkin.Service.ServiceImp.GamerServiceImp;
import com.pumpkin.Service.ServiceImp.UserServiceImp;
import com.pumpkin.controller.GamerController;
import com.pumpkin.entity.Gamer;
import com.pumpkin.entity.User;
import com.pumpkin.entity.entryData;
import com.pumpkin.tool.entry.ControllerUtils;
import com.pumpkin.tool.entry.entry;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Gamer/*")
public class GamerControllerImp extends HttpServlet implements GamerController {
    private final GamerServiceImp gamerServiceImp = new GamerServiceImp();

    @Override
    public void init(){

    };


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ControllerUtils.setCorHeader(request,response);
        Gamer gamer = new Gamer();
        //对传入的请求进行分拣
        System.out.println("Major接收到请求");
        if("/Major/getGamer".equals(request.getPathInfo())) {
            //获取gamer对象
            getGamer(request, response);
        }
//        }else if(""){
//            //个人信息中还有更改信息的功能，接下来进行更改个人信息的模块
//        }
    }

    private void getGamer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gamer gamer;
        //通过id获取gamer的所有信息
        System.out.println("1------"+request.getHeader("Authorization"));
        try {
            int id =Integer.parseInt(new String(Hex.decodeHex(request.getHeader("Authorization").split("\\.")[0].toCharArray())).split("&")[2]);
            System.out.println(id);
            gamer = gamerServiceImp.selectGamerById(id);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        Map<String,Object> map = new HashMap<>();
        if(gamer!=null){
            //如果成功获取到gamer对象，则将gamer对象传递回前端进行数据展示
            map.put("gamer",gamer);
            map.put("success",true);
            String json = JSON.toJSONString(map);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(json);
        }else{
            //如果无法获取到gamer对象，则把success设置为true并传递回前端进行控制台输出
            map.put("success",false);
            String json = JSON.toJSONString(map);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(json);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ControllerUtils.setCorHeader(request,response);
        response.setStatus(200);
    }



    @Override
    public void showGamerInfo(HttpServletRequest request, HttpServletResponse response) {
//        StringBuilder sb = new StringBuilder();
//        //从request中获取password
//        String password=null;
//        try {
//            BufferedReader bf = request.getReader();
//            String line;
//            while((line = bf.readLine())!=null){
//                sb.append(line);
//            }
//            password = JSON.parseObject(sb.toString(),String.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //从传入的请求中将Authorization头里取出加密后的字符串并对加密后的字符串进行解密，在&符号间获取id对象
        entryData entrydata = getEntryData(request);
        //接下来从头中取出id并生成密钥对加密数据和签名进行验证，如果为真则直接使用
        int id = Integer.parseInt(entrydata.getHeader().split("\\.")[2]);
//        byte[] salt = new UserServiceImp().getUserSalt(id);
//        //获取新的密钥并比对是否相同
//        String judgeSign;
//        try {
//            judgeSign = entry.getSignature(password,entry.deriveKeyFromPassword(password,salt),entrydata.getHeader());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        } catch (InvalidKeySpecException e) {
//            throw new RuntimeException(e);
//        }
//        if (judgeSign.equals(tokenPart[2])){
//            //密码相同
//        }
//        gamerServiceImp.insertGamer(id);
        Map<String,Object> map = new HashMap<>();
        Gamer gamer = gamerServiceImp.getGamer(id);
        if(gamer != null){
            map.put("gamer",gamer);
            map.put("success",true);
        }else {
            map.put("success",false);
        }
        String json = JSON.toJSONString(map);
        response.setContentType("application/json;charset=utf-8");
        try {
            PrintWriter out = response.getWriter();
            out.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static entryData getEntryData(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String[] tokenPart = token.split("&");
        entryData entrydata = new entryData(tokenPart[0],tokenPart[1],tokenPart[2]);
        return entrydata;
    }

    @Override
    public void updateMatchData(HttpServletRequest request, HttpServletResponse response) {
        Gamer gamer = new Gamer();
        //每个对局后进行更新,因此只需要取出前端发回的是否胜利的数据再进行计算即可
        try {
            BufferedReader bf = request.getReader();
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            boolean IsVictory = JSON.parseObject(sb.toString(),boolean.class);
            System.out.println(IsVictory);
            if(IsVictory){
                //获取gamer的数据
                int id = Integer.parseInt(getEntryData(request).getHeader().split("\\.")[2]);
                gamer = gamerServiceImp.selectGamerById(id);
                //将gamer的数据更新
                gamer.setId(id);
                gamer.setSumPlayNumb(gamer.getSumPlayNumb()+1);
                gamer.setSeasonPlayNumb(gamer.getSeasonPlayNumb()+1);
                gamer.setSeasonWinNumb(gamer.getSeasonWinNumb()+1);
                gamer.setSumWinNumb(gamer.getSumWinNumb()+1);
                gamer.setWinRate((double) gamer.getSeasonWinNumb() /gamer.getSeasonPlayNumb());
                gamer.setPoints(gamer.getPoints()+30);
                //修改gamer的数据
                gamerServiceImp.updateGamerMatchData(gamer);
            }else{
                //获取gamer的数据
                int id = Integer.parseInt(getEntryData(request).getHeader().split("\\.")[2]);
                gamer = gamerServiceImp.selectGamerById(id);
                //将gamer的数据更新
                gamer.setId(id);
                gamer.setSumPlayNumb(gamer.getSumPlayNumb()-1);
                gamer.setSeasonPlayNumb(gamer.getSeasonPlayNumb()-1);
                gamer.setSeasonWinNumb(gamer.getSeasonWinNumb()-1);
                gamer.setSumWinNumb(gamer.getSumWinNumb()-1);
                gamer.setWinRate((double) gamer.getSeasonWinNumb() /gamer.getSeasonPlayNumb());
                gamer.setPoints(gamer.getPoints()-25);
                //修改gamer的数据
                gamerServiceImp.updateGamerMatchData(gamer);
            }
            Map<String,Object> map = new HashMap<>();
            map.put("success",true);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(JSON.toJSONString(map));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean updateDanInfo(HttpServletResponse response,Gamer gamer) {
        if(gamer.getPoints()>gamer.getDan()*3*100){
            gamer.setDan(gamer.getDan()+1);
        }
        return gamerServiceImp.updateGamerDan(gamer);
    }

    @Override
    public void clearSeasonData(HttpServletRequest request, HttpServletResponse response) {

    }
}
