package My_Mybatis.configration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration类时MyBatis框架的SQL配置封装类，后期使用Dom4J解析
 *
 */
public class Configuration {
    /**
     * 数据源
     */
    private DataSource dataSource;


    /**
     * 封装mapper.xml文件中的SQL语句
     */
    Map<String,MappedStatement> mappedStatementMap=new HashMap<String,MappedStatement>();


    public Configuration() {
    }

    public Configuration(DataSource dataSource, Map<String, MappedStatement> mappedStatementMap) {
        this.dataSource = dataSource;
        this.mappedStatementMap = mappedStatementMap;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}



























