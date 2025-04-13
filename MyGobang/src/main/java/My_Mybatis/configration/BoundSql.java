package My_Mybatis.configration;


import java.util.ArrayList;
import java.util.List;

public class BoundSql {

    //要执行的Sql语句(转换后的)
    private String sqlText;

    //执行sql语句的参数的集合
    private List<String> parameterMappingList= new ArrayList<String>();

    public BoundSql(String sql, List<String> parameterMappings) {
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<String> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<String> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }
}
