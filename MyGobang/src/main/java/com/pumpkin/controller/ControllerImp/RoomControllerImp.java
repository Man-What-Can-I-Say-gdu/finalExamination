package com.pumpkin.controller.ControllerImp;

import com.alibaba.fastjson2.JSON;
import com.pumpkin.controller.GamerController;
import com.pumpkin.controller.RoomController;
import com.pumpkin.entity.Gamer;
import com.pumpkin.entity.Room;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Room/*")
public class RoomControllerImp extends HttpServlet implements RoomController {
    Room room = new Room();
    private static final String Address = "101.37.135.39";
    private static final String Port = "8081";
    private Map<String,Object> map = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(200);
        ControllerUtils.setCorHeader(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }


    @Override
    public void buildRoom(HttpServletRequest request, HttpServletResponse response) {
        //先获取传入的room对象
        room = parseRoom(request);
        try {
            //创建socket
            Socket socket = new Socket(Address,8081);
            //获取字符输出流,写出写出传入的房间信息的JSON字符串
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //获取字符输入流，获取服务器发送的创建成功的数据
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //先尝试建立连接，传入房主id，在此之前需要先从令牌获取房主id
            entryData entrydata = getEntryData(request);
            int ownerId = Integer.parseInt(entrydata.getHeader().split("&")[2]);
            //发送ownerId给服务器
            out.print(ownerId);
            String received;
            //死循环进行监听是否返回创建成功的消息
            while((received = in.readLine())==null);
            //退出死循环说明已经获取信息,获取成功消息并返回给前端
            boolean success = Boolean.parseBoolean(received);
            map.put("success",success);
            response.setStatus(200);
            PrintWriter responseOut = response.getWriter();
            responseOut.print(JSON.toJSONString(map));
            responseOut.close();
            in.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private static entryData getEntryData(HttpServletRequest request) throws DecoderException {
        String[] authorization = request.getHeader("Authorization").split("\\.");
        entryData entrydata = new entryData(authorization[0],authorization[1],authorization[2]);
        entrydata.setHeader(new String(Hex.decodeHex(entrydata.getHeader().toCharArray())));
        return entrydata;
    }

    @Override
    public void exitRoom(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void joinRoom(HttpServletRequest request, HttpServletResponse response){

    }

    @Override
    public void updateRoom(HttpServletRequest request, HttpServletResponse response) {

    }
    private Room parseRoom(HttpServletRequest request) {
        try {
            BufferedReader bf = request.getReader();
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = bf.readLine()) != null) {
                content.append(line);
            }
            return JSON.parseObject(content.toString(), Room.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
