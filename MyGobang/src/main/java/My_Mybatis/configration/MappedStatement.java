package My_Mybatis.configration;

/**
 * SQL语句映射
 */
public class MappedStatement {
    /**
     * id标识
     */
    private String id;
    /**
     * SQL语句返回值
     */
    private String resultType;
    /**
     * 参数类型
     */
    private String parameterType;

    /**
     * sql语句
     *
     */
    private String sql;

    /**
     *
     * sql语句的类型
     */
    private String sqlType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public MappedStatement() {
    }

    public MappedStatement(String id, String resultType, String parameterType) {
        this.id = id;
        this.resultType = resultType;
        this.parameterType = parameterType;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
