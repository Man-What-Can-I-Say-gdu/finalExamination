package My_Mybatis.io;

import java.io.InputStream;

public class Resource {


    /**
     * 根据配置文件的路径，将配置文件加载为字节流形式，存储在内存中
     * @param path 配置文件的位置
     * @return 返回的字节流
     */
    public static InputStream getResourceAsStream(String path) {
        return Resource.class.getClassLoader().getResourceAsStream(path);
    }
}
