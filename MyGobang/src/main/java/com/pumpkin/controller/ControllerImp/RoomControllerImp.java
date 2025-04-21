package com.pumpkin.controller.ControllerImp;

import com.alibaba.fastjson2.JSON;
import com.pumpkin.controller.RoomController;
import com.pumpkin.entity.Room;
import com.pumpkin.entity.RoomInstruction;
import com.pumpkin.entity.entryData;
import com.pumpkin.tool.entry.ControllerUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //设置状态码和响应头
        resp.setStatus(200);
        ControllerUtils.setCorHeader(req, resp);

        //先获取传入的room对象
        room = parseRoom(req);
        //房间的操作都在服务器中进行，因此可以直接创建连接
        Socket socket = new Socket(Address,8081);
        //获取字符输出流,写出写出传入的房间信息的JSON字符串
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //获取字符输入流，获取服务器发送的创建成功的数据
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //根据不同网址选择不同指令
        RoomInstruction instruction = null;
        if("/buildRoom".equals(req.getPathInfo())){
            instruction = RoomInstruction.BUILD_ROOM;
        }else if("/joinRoom".equals(req.getPathInfo())){
            instruction = RoomInstruction.JOIN_ROOM;
        }else if("/updateRoom".equals(req.getPathInfo())){
            instruction = RoomInstruction.UPDATE_ROOM;
        }else if("/leaveRoom".equals(req.getPathInfo())){
            instruction = RoomInstruction.LEAVE_ROOM;
        }else if("/destoryRoom".equals(req.getPathInfo())){
            instruction = RoomInstruction.DESTROY_ROOM;
        }else if("/startGame".equals(req.getPathInfo())){
            instruction = RoomInstruction.START_GAME;
        }else{

        }

        //进行连接
        if(connectToServer(req,out,in) && instruction!=null){
            //调用isSuccess方法向服务器传递数据
            map.put("success",isSuccess(out,instruction,in));

            //响应数据
            resp.setStatus(200);
            PrintWriter responseOut = resp.getWriter();
            responseOut.print(JSON.toJSONString(map));
            responseOut.close();
            //关闭流
            in.close();
            out.close();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(200);
        ControllerUtils.setCorHeader(req, resp);
    }


    /**
     * 解析token
     * @param request
     * @return
     * @throws DecoderException
     */
    private static entryData getEntryData(HttpServletRequest request) throws DecoderException {
        String[] authorization = request.getHeader("Authorization").split("\\.");
        entryData entrydata = new entryData(authorization[0],authorization[1],authorization[2]);
        entrydata.setHeader(new String(Hex.decodeHex(entrydata.getHeader().toCharArray())));
        return entrydata;
    }



    /**
     * 向服务器传递指令并进行执行并返回执行是否成功
     * @param out 输出流
     * @param instruction 执行指令
     * @param in 输入流
     * @return 执行情况
     * @throws IOException
     */
    private boolean isSuccess(PrintWriter out, RoomInstruction instruction, BufferedReader in) throws IOException {
        //连接成功，输出指令到服务器
        out.print(instruction);
        //将传入的guest转化为JSON字符串并传入
        out.print(JSON.toJSONString(room));
        //等待服务器响应加入信息
        String receive;
        //循环接受服务器传入的信息
        while ((receive = in.readLine()) == null) ;
        //接收到服务器传入的信息后进行转化
        return Boolean.parseBoolean(receive);
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

    /**
     * 连接到服务器
     * @param request
     * @param out
     * @param in
     * @return
     */
    private boolean connectToServer(HttpServletRequest request,PrintWriter out,BufferedReader in) {
        //创建socket
        Socket socket = null;
        try {
            //先尝试建立连接，传入房主id，在此之前需要先从令牌获取房主id
            entryData entrydata = getEntryData(request);
            int ownerId = Integer.parseInt(entrydata.getHeader().split("&")[2]);
            //发送ownerId给服务器，建立连接
            out.print(ownerId);
            String received;
            //死循环进行监听是否返回创建成功的消息
            while((received = in.readLine())==null);
            return Boolean.parseBoolean(received);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
