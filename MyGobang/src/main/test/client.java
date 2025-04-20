import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client {
    private static final String SERVER_IP = "101.37.135.39";
    private static final int SERVER_PORT = 8082;

    public static void main(String[] args) {
        try(Socket socket = new Socket(SERVER_IP,SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in)){


            //注册客户端id
            System.out.println("id:");
            String clientId = sc.nextLine();
            out.println(clientId);

            //启动消息接收线程
            new Thread(()->{
                try{
                    String received;
                    while(true){

                            while ((received = in.readLine()) != null){
                                System.out.println("\n收到消息"+received);
                            }
                        }
                    } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            //发送消息
            while(true){
                System.out.print("输入目标ID和消息 (格式：目标ID#消息): ");
                String input = sc.nextLine();
                out.print(input);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
