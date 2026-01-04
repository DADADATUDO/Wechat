package com.winter.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
        /**
     * 计算输入字符串的MD5哈希值
     *
     * @param input 需要计算MD5哈希值的输入字符串
     * @return 返回输入字符串的MD5哈希值（十六进制字符串格式），如果算法不可用则返回null
     */
    public static String md5(String input) {
        try {
            // 获取MD5消息摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 更新摘要数据
            md.update(input.getBytes());
            // 计算摘要结果
            byte[] bytes = md.digest();
            // 构建十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

        /**
     * 计算输入字符串的SHA-256哈希值
     *
     * @param input 需要计算哈希值的输入字符串
     * @return 返回输入字符串的SHA-256哈希值的十六进制表示，如果算法不可用则返回null
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
