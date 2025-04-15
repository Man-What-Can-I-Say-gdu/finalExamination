import com.pumpkin.tool.entry.entry;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {
        byte[] key = entry.deriveKeyFromPassword("qwertyuiop",entry.getRandomSalt(entry.BS));
        System.out.println(key.length);
        String payload = entry.encryUserInfo(1, key,"AES",false);
        System.out.println("key:"+entry.byte2HexStr(key));
        System.out.println(payload);
        String before = entry.decryInfoString(payload,key,"AES",false);
        System.out.println(before);

        //测试签名
        String signature = entry.hamcsha256(payload,key);
        System.out.println("signature:"+signature);
    }
}
