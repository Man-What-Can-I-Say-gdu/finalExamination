package com.Pumpkin.Controller;

import com.Pumpkin.Service.ServiceImp.RoomServiceImp;
import com.Pumpkin.entity.Room;
import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomControllerImp{
    private static final int PORT = 8081;
    private static Map<Integer,Socket> clients = new ConcurrentHashMap<Integer,Socket>();
    private static RoomServiceImp roomServiceImp = new RoomServiceImp();
    public static void main(String[] args) {
        //创建ServerSocket进行监听
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("服务端开始监听"+PORT);

            //死循环接收请求
            while(true){
                //接收到新的socket对象
                Socket socket = serverSocket.accept();
                //开启新线程处理传入的请求
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientHandler extends Thread{
        /**
         * 客户端的socket
         */
        private Socket socket;
        /**
         * 客户端的唯一id
         */
        private int clientId;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }
        //重写run函数实现线程操作
        @Override
        public void run() {
            //创建输入流和输出流
            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {
                //注册客户端
                clientId = Integer.parseInt(in.readLine());
                clients.put(clientId,socket);
                System.out.println(clientId+"已连接");
                out.print(clients.get(clientId)!=null);
                //接收传入的请求
                int requestType = -1;
                while(true){
                    //先获取请求类型
                    requestType = Integer.parseInt(in.readLine());
                    //获取room数据
                    Room room = parseRoom(in);
                    //根据不同的请求执行不同的功能
                    switch(requestType){
                        case 1:{
                            out.print(buildRoom(room));
                            break;
                        }case 2:{
                            //加入房间请求
                            out.print(joinRoom(room.getGuestId(),room));
                            break;
                        }case 3:{
                            //修改房间信息请求
                            out.print(updateRoom(room));
                            break;
                        }case 4:{
                            //销毁房间请求
                            out.print(destoryRoom(room));
                            //关闭Socket
                            socket.close();
                            return;
                        }case 5:{
                            //退出房间请求
                            out.print(deleteGuest(room));
                            break;
                        } case 6:{
                            //开始游戏请求
                            break;
                        } default:{
                            throw new RuntimeException("发送了不符合要求的请求");
                        }
                    }
                    requestType = -1;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static Room parseRoom(BufferedReader in) throws IOException {
        //创建房间请求,先获取并解析客户端发送的room的JSON字符串
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        //重复读取直到读入的信息不为空时，即收到房间信息
        while((line = in.readLine())==null);
        //将读到的JSON字符串存放到stringBuilder中
        stringBuilder.append(line);
        return JSON.parseObject(stringBuilder.toString(),Room.class);
    }

    public static boolean buildRoom(Room room) {
        return roomServiceImp.buildRoom(room);
    }

    public static boolean joinRoom(int guestId, Room room) {
        return roomServiceImp.joinRoom(room,guestId);
    }

    public static boolean updateRoom(Room room) {
        return roomServiceImp.updateRoom(room);
    }

    public static boolean deleteGuest(Room room) {
        return roomServiceImp.deleteGuest(room);
    }

    public static boolean destoryRoom(Room room) {
        return roomServiceImp.destoryRoom(room.getRoomId());
    }
}
