package My_Mybatis.session;

import My_Mybatis.configration.Configuration;
import My_Mybatis.configration.XmlMapperBuilder;
import org.dom4j.DocumentException;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {


    public SqlSessionFactory build(InputStream inputStream) throws DocumentException {

        Configuration configuration = new Configuration();
        //获取configuration对象
        XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder(configuration);
        xmlMapperBuilder.parse(inputStream);
        //创建SqlSessionFactory
        return new DefaultSqlSessionFactory(configuration);
    }
}
