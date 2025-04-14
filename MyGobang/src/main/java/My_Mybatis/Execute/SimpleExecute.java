package My_Mybatis.Execute;

import DataBasePool.ConnectionPool;
import My_Mybatis.configration.BoundSql;
import My_Mybatis.configration.Configuration;
import My_Mybatis.configration.MappedStatement;
import com.pumpkin.entity.User;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class SimpleExecute implements Execute{
    Map<Integer,Integer> map = new HashMap<Integer,Integer>();
    int findPosition = 0;
    //SQL语句的参数
    List<String> parameterMappings = new ArrayList<String>();

    @Override
    public <E> List<E> query(MappedStatement mappedStatement, Object... parms) throws Exception {

        //获取数据库连接
        ConnectionPool.InitConnectionPool();
        Connection connection = ConnectionPool.GetConnection();
        //获取要执行的SQL语句
        //得到原始sql语句
        String sql = mappedStatement.getSql();
        //SQL语句的转换（替换占位符）//未获取正确的sql
        BoundSql boundSql = this.getBoundSql(sql);
        assert connection != null;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = this.getClassType(parameterType);
        //获取SQL语句的参数集合
        List<String> paraeterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < paraeterMappingList.size(); i++) {
            String content = paraeterMappingList.get(i);
            //进行反射
            Field declareField = parameterTypeClass.getDeclaredField(content);
            declareField.setAccessible(true);
            //取出参数
            Object o = declareField.get(parms[0]);
            preparedStatement.setObject(i+1,o);
        }
        //执行sql
        String id = mappedStatement.getId();
        ResultSet resultSet = null;
        //sqlType==null
        if("insert".equals(mappedStatement.getSqlType())||"update".equals(mappedStatement.getSqlType())||"delete".equals(mappedStatement.getSqlType())){
            //You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near '' at line 1
            Integer result = preparedStatement.executeUpdate();
            List<Integer> resultList = new ArrayList<Integer>();
            resultList.add(result);
            return (List<E>) resultList;
        }else{
            //执行查询
            resultSet = preparedStatement.executeQuery();
        }
        //获取返回值的类型
        String returnType = mappedStatement.getResultType();
        Class<?> returnTypeClass = this.getClassType(returnType);
        List<Object> objects = new ArrayList<Object>();
        //查询的结果集封装
        while (resultSet.next()){
            //调用无参方法构造对象
            Object o = returnTypeClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for(int i=1; i <= metaData.getColumnCount(); i++){
                //获取字段名
                String columnName = metaData.getColumnName(i);
                //获取值
                Object value = resultSet.getObject(columnName);
                //属性封装，使用内省根据反射数据库表和实体类的属性和字段关系数据封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName,returnTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);
            }
            objects.add(o);
        }


        return (List<E>)objects;
    }

    /**
     * 根据类的全名称获取Class
     * @param parameterType
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if(parameterType!=null){
            return Class.forName("com.pumpkin.entity.User");
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作，先将#{}替换为？，解析出#{}里的值进行存储
     * @param sql 原生sql
     * @return 解析后的sql
     */
    private BoundSql getBoundSql(String sql) throws SQLException {
        //获取占位符所在的位置
        this.parserSql(sql);
        Set<Map.Entry<Integer,Integer>> entries = this.map.entrySet();
        for (Map.Entry<Integer,Integer> entry : entries) {
            Integer key = entry.getKey()+2;
            Integer value = entry.getValue();
            //获取填充的字段名
            parameterMappings.add(sql.substring(key,value));
        }
        for(String s : parameterMappings){
            sql = sql.replace("#{"+s+"}","?");
        }
        return new BoundSql(sql,parameterMappings);
    }

    /**
     * 获取占位符所在的索引
     * @param sql
     */
    private void parserSql(String sql) throws SQLException {
        int openIndex=0,closeIndex=0;
        while((openIndex = sql.indexOf("#{",findPosition))!=-1){
            //存在#{占位符
            if((closeIndex = sql.indexOf("}",findPosition+1))!= -1){
                //存在}闭合,将位置信息传入map
                this.map.put(openIndex,closeIndex);
                findPosition=closeIndex+1;
            }else{
                //不存在闭合，抛出错误
                throw new SQLException("SQL格式错误");
            }
        }

    }

}
