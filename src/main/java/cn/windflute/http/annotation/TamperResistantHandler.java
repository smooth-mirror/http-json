package cn.windflute.http.annotation;

import cn.windflute.http.dto.ApiRequestDTO;
import cn.windflute.uitls.ReflectUtil;
import cn.windflute.uitls.SignUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class TamperResistantHandler {

    private TamperResistantHandler() {
        //nothing
    }
    /**
     * 盐的位数
     */
    public static final int DEFAULT_SALT_LENGTH = 16;
    /**
     * 获取需要防篡改的对象的属性名-属性值map
     *
     * @param object 需要防篡改的对象
     * @return 属性名-属性值map
     * @throws IllegalAccessException
     */
    public static Map<String, String> getTamperResistantMap(Object object) throws IllegalAccessException {
        if (null == object) {
            return null;
        }
        List<Field> fieldList = ReflectUtil.getDeclaredFields(object);
        TamperResistantAnnotation tamperResistantAnnotation = null;
        Object value = null;
        Map<String, String> map = new HashMap<>(5);
        for (Field field : fieldList) {
            // 如果属性有注解，就把属性名-属性值添加到map
            if (field.isAnnotationPresent(TamperResistantAnnotation.class)) {
                tamperResistantAnnotation = field.getAnnotation(TamperResistantAnnotation.class);
                value = ReflectUtil.getFieldValue(object, field);
                //注释了转成json，则先转成json
                if (tamperResistantAnnotation.isTransJson()) {
                    value = JSON.toJSONString(value, SerializerFeature.SortField);
                }
                //属性值为null的转成null字符串
                map.put(field.getName(), String.valueOf(value));
            }
        }
        return map;
    }

    /**
     * 防篡改参数列表处理，按ascii码 从小到大排序，拼接成字符串
     *
     * @return 参与签名防篡改的参数值拼接
     */
    public static String concatTamperResistantParams(List<String> tamperResistantParamList) {
        Collections.sort(tamperResistantParamList);
        StringBuilder sb = new StringBuilder();
        for (String item : tamperResistantParamList) {
            sb.append(item);
        }
        return sb.toString();
    }

    public static Map getRequestJsonWithSign(Object params, String appId, String appKey, String salt,
                                             SignWithTamperResistant signWithTamperResistant)
            throws IllegalAccessException {
        //参数验证
        boolean isAnyParamEmpty = null == appId || appId.isEmpty() || null == appKey || appKey.isEmpty();
        if (isAnyParamEmpty) {
            return null;
        }
        Map<String, String> tamperResistantMap = null;
        Map map = null;
        //请求的参数转成map
        if (params instanceof Map) {
            map = (Map) params;
        } else {
            //获取需要防篡改的对象的属性名-属性值map
            tamperResistantMap = TamperResistantHandler.getTamperResistantMap(params);
            map = ReflectUtil.transBean2Map(params);
        }
        //开始组装api请求的dto
        ApiRequestDTO apiRequestDTO = new ApiRequestDTO();
        if (null != tamperResistantMap && !tamperResistantMap.isEmpty()) {
            //需要防篡改的对象的属性名设置
            apiRequestDTO.setTamperResistantNameList(tamperResistantMap.keySet());
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        apiRequestDTO.setAppId(appId);
        apiRequestDTO.setTimestamp(timestamp);

        //进行签名以及设置签名参数
        apiRequestDTO.setSignature(signWithTamperResistant.sign(
                //签名拼接
                signWithTamperResistant.concatForSign(tamperResistantMap, appId, timestamp), appKey, salt));
        //api请求的dto转成map设置到请求的map中
        map.putAll(apiRequestDTO.toMap());

        return map;

    }

    public static Map getRequestJsonWithSign(Object params, String appId, String appKey) throws IllegalAccessException {
        SignWithTamperResistant signWithTamperResistant = new SignWithTamperResistant() {
            @Override
            public String sign(String params, String appKey, String salt) {
                //这里的签名供参考
                return SignUtil.tamperResistantAndAuthRule(params, appKey, salt);
            }

            @Override
            public String concatForSign(Map<String, String> paramMap, String appId, String timestamp) {
                //这里的参数拼接供参考
                List<String> tamperResistantList = new ArrayList<>();
                if (null != paramMap && !paramMap.isEmpty()) {
                    //获取需要参与防篡改的值集合
                    tamperResistantList.addAll(paramMap.values());
                }
                //毫秒级时间戳以及appid参与防篡改签名
                tamperResistantList.add(appId);
                tamperResistantList.add(timestamp);
                return concatTamperResistantParams(tamperResistantList);
            }
        };
        return getRequestJsonWithSign(params, appId, appKey, RandomStringUtils
                        .random(DEFAULT_SALT_LENGTH, true, true), signWithTamperResistant);

    }

    public interface SignWithTamperResistant {
        /**
         * 签名
         *
         * @param params 参与签名防篡改的参数值拼接
         * @param appKey 签名的密钥
         * @param salt   签名的盐
         * @return
         */
        String sign(String params, String appKey, String salt);

        /**
         * 签名的参数map拼接
         *
         * @param paramMap 参数map
         * @return 签名的参数map拼接
         */
        String concatForSign(Map<String, String> paramMap, String appId, String timestamp);
    }

}