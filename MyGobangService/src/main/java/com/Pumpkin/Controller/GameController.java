package com.Pumpkin.Controller;

import com.Pumpkin.Service.GameService;
import com.Pumpkin.Service.ServiceImp.GameServiceImp;
import com.Pumpkin.entity.HistoricGame;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class GameController {

    GameService gameService = new GameServiceImp();
    /**
     * 游戏开始，服务器生成id，并将id打包后发送给两个客户端
     */
    public static String startGame(){
        //获得当前时间
        String currentTime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
        Random rand = new Random();
        //获得随机的三位数
        String randomNum = String.valueOf((rand.nextInt(899)+100));
        return currentTime+randomNum;
    }
    /**
     * 保存游戏信息,包括:对局id，黑方，白方，对局结果，棋盘样式id
     */
    public  void saveGameInfo(HistoricGame historicGame){
        gameService.saveGame(historicGame);
    }
}
