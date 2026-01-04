package com.winter.utils;

public class LoginUtil {
    /**
     * 将表单密码转换为数据库存储密码
     * 通过将表单密码与盐值拼接后进行MD5加密，生成用于数据库存储的密码
     *
     * @param formPwd 表单提交的原始密码
     * @param salt 用于加密的盐值
     * @return 经过MD5加密后的数据库存储密码
     */
    public static String formPwdToDbPwd(String formPwd,String salt) {
        return HashUtil.md5(formPwd + salt);
    }


    /**
     * 验证表单密码与数据库密码是否相等
     * 该方法将表单提交的密码使用盐值进行加密处理，然后与数据库中存储的密码进行比较
     *
     * @param formPwd 表单提交的原始密码
     * @param dbPwd 数据库中存储的已加密密码
     * @param salt 用于密码加密的盐值
     * @return 如果表单密码加密后与数据库密码相等则返回true，否则返回false
     */
    public static boolean formPwdEqDbPWd(String formPwd,String dbPwd,String salt) {
        return formPwdToDbPwd(formPwd,salt).equals(dbPwd);
    }

}