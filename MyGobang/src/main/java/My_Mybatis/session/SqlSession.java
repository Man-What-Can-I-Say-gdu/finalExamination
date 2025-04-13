package My_Mybatis.session;

import java.util.List;

public interface SqlSession {
    /**
     * 查询所有数据
     * @param <E>
     * @param parms 查询sql需要的参数
     * @param statementId sql语句的唯一id
     * @return 返回对象
     * @throws Exception
     */
    <E> List<E> selectList(String statementId,Object... parms)throws  Exception;

    /**
     * 查询指定数据
     * @param <E>
     * @param parms 查询sql需要的参数
     * @param statementId sql语句的唯一id
     * @return 返回对象
     * @throws Exception
     */
    <E> E selectOne(String statementId,Object... parms)throws  Exception;

    /**
     * 增加数据
     * @param <E>
     * @param parms sql需要的参数
     * @param statementId sql语句的唯一id
     * @return 返回对象
     * @throws Exception
     */
    <E> E insert(String statementId,Object... parms)throws  Exception;

    /**
     * 更新
     * @param <E>
     * @param parms sql需要的参数
     * @param statementId sql语句的唯一id
     * @return 返回对象
     * @throws Exception
     */
    <E> E update(String statementId,Object... parms)throws  Exception;

    /**
     * 删除数据
     * @param <E>
     * @param parms sql需要的参数
     * @param statementId sql语句的唯一id
     * @return 返回对象
     * @throws Exception
     */
    <E> E delete(String statementId,Object... parms)throws  Exception;


    /**
     * 为mapper层的接口动态代理生成实现类
     * @param mapperCLass 字节码
     * @param <T> 反向
     * @return 接口的代理类对象
     */
    <T> T getMapper(Class<T> mapperCLass)throws  Exception;


}
