package com.pumpkin.tool.entry;

import org.apache.commons.codec.binary.Hex;
import org.apache.xerces.impl.dv.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class entry {
    /**
     * 加密类型：DES
     */
    private final static String DES = "DES";

    /**
     * 加密方式：AES
     */
    private final static String AES = "AES";

    /**
     * 盐值长度：16
     */
    public static final int BS = 16;





    /**
     * 对传入的数据进行加密，此处只对用户的id进行加密，不设置保存时间
     * @param info 用户id
     * @param key 密钥
     * @return 加密后的字符串
     */
    public static String encryUserInfo(String info,byte[] key,String algorithm,boolean cbc) throws Exception {
        // 根据加密类型判断key字节数
        String UserInfo = gerInfoString(info,key);
        //CBC模式
        String transformation = cbc? algorithm+ "CBC" : algorithm;
        //获取加密对象
        Cipher cipher = Cipher.getInstance(transformation);
        //创建加密规则
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,algorithm);
        //初始化加密模式和算法
        if(cbc){
            //使用cbc模式
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec,new IvParameterSpec(key));
        }else{
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        }
        //加密并返回
        return Base64.encode(cipher.doFinal(UserInfo.getBytes()));
    }
    public static String gerInfoString(String info,byte[] key){
        //获取InfoString
        return "key="+ Arrays.toString(key) +"&info="+info+"&starttime="+System.currentTimeMillis();
    }

    /**
     * 对密文进行解密
     * @param entryInfo 传入的密文
     * @param key 传入的密钥
     * @param algorithm 加密算法
     * @param cbc 是否采用cbc模式
     * @return
     */
    public static String decryInfoString(String entryInfo,byte[] key,String algorithm,boolean cbc) throws Exception {
        // 根据加密类型判断key字节数
        String transformation = cbc? algorithm+ "CBC" : algorithm;
        //获取Cipher对象
        Cipher cipher = Cipher.getInstance(transformation);
        //指定密钥规则
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,algorithm);
        if(cbc){
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,new IvParameterSpec(key));
        }else{
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        }
        //解密
        return new String(cipher.doFinal(Base64.decode(entryInfo)));
    }


    /**
     * 获得签名
     * @param plainStr 加密后的字符串
     * @param key 密钥
     * @return 加密后的字符串
     */
    public static String getSignature(String plainStr,byte[] key,String header){
        //将java字符串形式的密钥转化为java加密标准密钥对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        //初始化mac实例
        Mac mac = null;
        try{
            mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        //计算hmac签名
        byte[] digest = mac.doFinal((header+"."+plainStr).getBytes(StandardCharsets.UTF_8));
        //转化为16进制字符串
        return byte2HexStr(digest);
    }


    /**
     *  将传入的digest转化为16进制字符串
     * @param digest
     * @return
     */
    public static String byte2HexStr(byte[] digest) {
        return digest != null? new String(Hex.encodeHex(digest)) : null;
    }


    /**
     * 通过密码生成密钥
     * @param password 传入的密码
     * @param salt 传入的盐
     */
    public static byte[] deriveKeyFromPassword(String password,byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 100000;
        int keyLength = 256;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),salt,iterations,keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey secretKey = skf.generateSecret(spec);
        return secretKey.getEncoded();
    }

    /**
     * 随机生成盐
     */
    public static byte[] getRandomSalt(int size){
        byte[] salt = new byte[size];
        new Random().nextBytes(salt);
        return salt;
    }

    /**
     * 设置token头
     */
    public static Map<String,Object> getTokenHeader(int id){
        Map<String,Object> map = new HashMap<>();
        map.put("algorithm","HmacSHA256");
        map.put("typ","jwt");
        map.put("id",id);
        return map;
    }


}
