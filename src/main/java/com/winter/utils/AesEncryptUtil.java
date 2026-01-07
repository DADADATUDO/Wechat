package com.winter.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * 对称加密，用于验证码加密存储
 */
public class AesEncryptUtil {
    //钥匙是16字节
    private static final String KEY = "nLfSadFxVbjeW2OJ";

    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/CBC/PKCS7Padding";
    // CBC必须设置iv 16字节
    private static final String IV = "123456789abcdefg";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    /**
     * 加密
     * @param content 待加密内容
     * @return 加密后的字符串
     * @throws GeneralSecurityException
     */
    public static String encrypt(String content) throws GeneralSecurityException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        byte[] aseKey = KEY.getBytes();
        Key key = new SecretKeySpec(aseKey, "AES");
        AlgorithmParameters _iv = generateIV(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, key,_iv);
        byte[] b = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.getEncoder().encodeToString(b);
    }

    public static AlgorithmParameters generateIV(byte[] iv) throws GeneralSecurityException {
        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        // Arrays.fill(iv, (byte) 0x00);
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /**
     * 解密
     * @param encrypted 待解密内容
     * @return 解密后的字符串
     * @throws GeneralSecurityException
     */
    public static String decrypt(String encrypted) throws GeneralSecurityException {
        byte[] encrypted64 = Base64.getDecoder().decode(encrypted);
        byte[] key64 = KEY.getBytes();
        byte[] iv64 = IV.getBytes();
        Security.addProvider(new BouncyCastleProvider());
        KeyGenerator.getInstance("AES").init(128);
        return new String(decrypt(encrypted64, key64, generateIV(iv64)), StandardCharsets.UTF_8);
    }
    public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv) throws GeneralSecurityException {
        Key key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        // 设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }
}
