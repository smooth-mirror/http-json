package cn.windflute.uitls;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

public class SignUtil {

    private SignUtil() {
        //nothing
    }

    /**
     * md5加密
     *
     * @param params 加密的字符串
     * @return 32位md5
     */
    public static String md5Hex(String params) {
        return DigestUtils.md5Hex(params.getBytes());
    }

    /**
     * 防篡改和认证签名
     *
     * @param params
     * @param appKey
     * @param saltValue
     * @return
     */
    public static String tamperResistantAndAuthRule(String params, String appKey, String saltValue) {
        return saltValue + md5Hex(appKey + params + saltValue);
    }

    /**
     * 从密钥中获取盐值
     *
     * @param signature   签名
     * @param saltLength 盐值的长度
     */
    public static String getSaltValue(String signature, int saltLength) {
        //参数验证
        return signature.substring(0, saltLength);
    }

    /**
     * 签名校验
     */
    public static boolean checkTamperResistantSign(String params, String apiKey, String needCheckSign, int saltLength) {
        return needCheckSign.equals(tamperResistantAndAuthRule(params, apiKey, getSaltValue(needCheckSign, saltLength)));
    }


}
