package My_Mybatis.Execute;

import My_Mybatis.configration.Configuration;
import My_Mybatis.configration.MappedStatement;

import java.util.List;

/**
 * SQL执行器
 */
public interface Execute {
    /**
     * SQL执行器
     * @param mappedStatement
     * @param parms
     * @param <E>
     * @return
     */
    <E> List<E> query(MappedStatement mappedStatement,Object...parms)throws Exception;
}
