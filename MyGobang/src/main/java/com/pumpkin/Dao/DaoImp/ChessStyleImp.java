package com.pumpkin.Dao.DaoImp;


import DataBasePool.ConnectionPool;
import com.pumpkin.Dao.ChessStyle;
import com.pumpkin.entity.Chess;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChessStyleImp implements ChessStyle {

    /**
     * 创建chessStyle表
     * @param chessStyleId 棋盘样式id，即棋盘名称
     * @return
     */
    @Override
    public boolean buildChessBoard(String chessStyleId) {
        //操作数据表
        String buildTableSQL = "create table ? ( steps int , position char(2), forward boolean, rear boolean, theLeft boolean,theRight boolean,leftFront boolean,rightFront boolean,leftRear boolean,rightRear boolean,type boolean);";
        int result;
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(buildTableSQL);
            preparedStatement.setString(1, chessStyleId);
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result == 1;
    }


    @Override
    public boolean insertChessPosition(String chessStyleId, String position, int steps,boolean type) {
        String SQL = "insert into ? (postion, steps,type) values (? ,? ,?)";
        int result = 0;
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, chessStyleId);
            preparedStatement.setString(2, position);
            preparedStatement.setInt(3, steps);
            preparedStatement.setBoolean(4,type);
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result == 1;
    }

    /**
     * 悔棋时为己方，并且未下棋，因此要先查看上一个是否为同色棋，未下棋时，棋子保存的是上一步的数据
     * @param chessStyleId
     * @param chess
     * @return
     */
    @Override
    public boolean removeChessPosition(String chessStyleId,Chess chess,boolean myType) {
        String SQL = "delete from ? where steps = ?";
        String selectSQL = "select type from ? where steps = ?";
        int result = 0;
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            //记录上一步棋的棋子花色
            boolean isBlack = false;
            ResultSet resultSet;
            do{
                //查看上一步是否为同色棋
                preparedStatement.setString(1, chessStyleId);
                preparedStatement.setInt(2, chess.getSteps());
                //获取查询到的花色数据
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                isBlack = resultSet.getBoolean("type");
                //花色相同只悔棋一步，花色不同一直悔棋到自己的第一颗棋子
                preparedStatement.setString(1, chessStyleId);
                preparedStatement.setInt(2,chess.getSteps());
                result = preparedStatement.executeUpdate();
                //判断是否悔棋到自己的棋
            }while(isBlack == myType);
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result > 0;
    }

    @Override
    public ArrayList<Chess> selectChessStyle(String chessStyleId) {
        ArrayList<Chess> finalChessStyleChess = new ArrayList<>();
        String SQL = "select * from ?";
        Chess chess = new Chess();
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, chessStyleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                chess.setSteps(resultSet.getInt("steps"));
                chess.setPosition(resultSet.getString("position"));
                finalChessStyleChess.add(chess);
            }
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return finalChessStyleChess;
    }

    /**
     *
     * 查找特定位置是否存在棋子并获得棋子类型,并获取相同方向的下一个棋子是否为同色棋子
     * @param x,y 要查找的棋子的x和y坐标
     * @param direction 指定查找的方位
     *
     */
    public Chess selectChessBySpecialPosition(String direction,String chessStyleId,int x,int y){
        String SQL = "select * from ? where position = ?";
        Chess chess = new Chess();
        String chessPosition = getPositionString(direction, x, y);
        if (chessPosition == null)
            //当位置超出棋盘边缘时，返回null
            return null;
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, chessStyleId);
            preparedStatement.setString(2, chessPosition);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                //存在棋子
                chess.setExist(true);
                //获取棋子再特定方向是否有同色棋子,使用反射
                Method method = Chess.class.getMethod("set"+direction,boolean.class);
                //执行方法
                method.invoke(chess,resultSet.getBoolean(direction));
                //更新棋子位置
                chess.setPosition(chessPosition);
            }else{
                chess.setExist(false);
            }
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return chess;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定的方位位置坐标
     * @param direction
     * @param x
     * @param y
     * @return
     */

    public String getPositionString(String direction, int x, int y) {
        //获取存入的方向值
        String chessPosition;
        if("forward".equals(direction)){
            if(y == 0)return null;
            chessPosition = "("+ x +","+(y -1)+")";
        }else if("rear".equals(direction)){
            chessPosition = "("+ x +","+(y +1)+")";
        }else if("theLeft".equals(direction)){
            if(x == 0)return null;
            chessPosition = "("+(x -1)+","+ y +")";
        }else if("theRight".equals(direction)){
            chessPosition = "("+(x +1)+","+ y +")";
        }else if("leftFront".equals(direction)){
            if(y == 0)return null;
            chessPosition = "("+(x -1)+","+(y -1)+")";
        }else if("rightFront".equals(direction)){
            if(x == 0)return null;
            if(y == 0)return null;
            chessPosition = "("+(x +1)+","+(y -1)+")";
        }else if("leftRear".equals(direction)){
            if(x == 0)return null;
            chessPosition = "("+(x -1)+","+(y +1)+")";
        }else if("rightRear".equals(direction)){
            chessPosition = "("+(x +1)+","+(y +1)+")";
        }else if("middle".equals(direction)){
            chessPosition = "("+x+","+y+")";
        }else{
            return null;
        }
        return chessPosition;
    }

    /**
     * 更新周围是否存在同色棋子的值
     * @param chessStyleId
     * @param chess
     * @return
     */
    public boolean updateChessSurrounding(String chessStyleId,Chess chess){
        String SQL = "update ? set forward = ? ,rear = ? ,theRight = ?, theLeft=?, leftFront = ?, rightFront = ?,rightRear = ?,leftRear = ? where steps = ?";
        int result = 0;
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, chessStyleId);
            preparedStatement.setBoolean(2, chess.isForward());
            preparedStatement.setBoolean(3, chess.isRear());
            preparedStatement.setBoolean(4, chess.isTheRight());
            preparedStatement.setBoolean(5, chess.isTheLeft());
            preparedStatement.setBoolean(6, chess.isLeftFront());
            preparedStatement.setBoolean(7, chess.isRightFront());
            preparedStatement.setBoolean(8, chess.isLeftRear());
            preparedStatement.setBoolean(9, chess.isRightRear());
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result > 0;
    }

    /**
     *
     */

//    /**
//     * 查找一颗棋子周围的棋子的情况
//     * @param position
//     * @return
//     */
//    public boolean selectAroundChess(String chessStyleId, String position){
//        int[] positionArray = new int[2];
//        String[] splitPosition = position.split(",");
//        //获取x
//        positionArray[0] = splitPosition[0].toCharArray()[1];
//        //获取y
//        positionArray[1] = splitPosition[1].toCharArray()[0];
//    }
}
