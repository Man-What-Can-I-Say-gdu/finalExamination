package My_Mybatis.session;

import My_Mybatis.Execute.SimpleExecute;
import My_Mybatis.configration.Configuration;
import My_Mybatis.configration.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession{
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... parms) throws Exception {
        SimpleExecute simpleExecute = new SimpleExecute();
        //mappedStatement为空
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get((configuration.getNamespace()+statementId));
        List<Object> list = simpleExecute.query(mappedStatement, parms);
        return (List<E>) list;
    }

    @Override
    public <E> E selectOne(String statementId, Object... parms) throws Exception {
        List<E> objects = selectList(statementId, parms);
        if(objects.size() == 1){
            return (E) objects.get(0);
        }else if(objects.size()>1){
            throw new Exception("查询结果不唯一");
        }else{
            throw new Exception("查询结果为空");
        }
    }

    @Override
    public <E> E insert(String statementId, Object... parms) throws Exception {
        SimpleExecute simpleExecute = new SimpleExecute();
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(configuration.getNamespace()+"."+statementId);
        //
        List<Object> list = simpleExecute.query(mappedStatement, parms);
        if(list.size()>0){
            return (E) list.get(0);
        }else{
            return (E)"0";
        }
    }

    @Override
    public <E> E update(String statementId, Object... parms) throws Exception {
        SimpleExecute simpleExecute = new SimpleExecute();
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(configuration.getNamespace()+"."+statementId);
        List<Object> list =  simpleExecute.query(mappedStatement, parms);
        if(list.size()>0){
            return (E) list.get(0);
        }else{
            return (E)"0";
        }
    }

    @Override
    public <E> E delete(String statementId, Object... parms) throws Exception {
        SimpleExecute simpleExecute = new SimpleExecute();
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(configuration.getNamespace()+"."+statementId);
        List<Object> list =  simpleExecute.query(mappedStatement, parms);
        if(list.size()>0){
            return (E) list.get(0);
        }else{
            return (E)"0";
        }
    }

    @Override
    public <T> T getMapper(Class<T> mapperCLass) throws Exception {
        Object instance = Proxy.newProxyInstance(mapperCLass.getClassLoader(), new Class[]{mapperCLass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //接口中的方法名
                String methodName = method.getName();
                //接口的全类名
                String className = method.getDeclaringClass().getName();
                //SQL的唯一标识
                String statementId = className+"."+methodName;
                //获取方法被调用后的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                //方法的参数类型
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                //i为0时为id，此时恒为String，我们只需要第二个及以后的参数
                configuration.getMappedStatementMap().get(statementId).setParameterType(String.valueOf(genericParameterTypes[1]));
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                if("insert".equals(mappedStatement.getSqlType())){
                    return insert(statementId,args);
                }else if("update".equals(mappedStatement.getSqlType())){
                    return update(statementId,args);
                }else if("delete".equals(mappedStatement.getSqlType())){
                    return delete(statementId,args);
                }

                //判断是否进行的泛型类型的参数化
                if(genericReturnType instanceof ParameterizedType){
                    List<Object> objects = selectList(statementId,args);
                    return objects;
                }else{
                    //查询的结果不是泛型
                    return selectOne(statementId,args);
                }
            }
        });
                return (T)instance;
    }
}
