package My_Mybatis.configration;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlMapperBuilder {

    /**
     * 配置数据封装对象
     */
    private Configuration configuration;

    /**
     * 有参的构造方法
     * @param configuration
     */
    public XmlMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public XmlMapperBuilder() {

    }


    /**
     * 传入配置文件的字节流，解析配置文件
     * @param inputStream
     */
    public Configuration parse(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        //XML文档对象
        Document document = reader.read(inputStream);
        //获取文档的根节点
        Element rootElement = document.getRootElement();
        //获取根节点的属性对象
        Attribute attributeNamespace = rootElement.attribute("namespace");
        //com.bruce.mapper.UserMapper
        String namespace = attributeNamespace.getValue();
        //xpath解析，解析xml配置文件
        List<Element> selectList = rootElement.selectNodes("//select");
        List<Element> insertList = rootElement.selectNodes("//insert");
        List<Element> deleteList = rootElement.selectNodes("//delete");
        List<Element> updateList = rootElement.selectNodes("//update");

        List<Element> allList = new ArrayList<Element>();
        allList.addAll(selectList);
        allList.addAll(insertList);
        allList.addAll(deleteList);
        allList.addAll(updateList);
        for (Element element : allList) {
            //获取SQL的id属性值
            String id = element.attributeValue("id");
            //获取返回值类型
            String resultType = element.attributeValue("resultType");
            //获取参数类型
            String parameterType = element.attributeValue("parameterType");
            //获取每个mapper节点中的SQL语句
            String sqlText = element.getTextTrim();
            //封装对象
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sqlText);
            //key为namespace+.+id
            String key = namespace+"."+id;
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }


        return configuration;

    }
}









































