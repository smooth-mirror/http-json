package cn.windflute.http.filter;

import cn.windflute.http.annotation.TamperResistantHandler;
import cn.windflute.http.dto.ApiRequestDTO;
import cn.windflute.http.servlet.PostParameterRequestWrapper;
import cn.windflute.uitls.CollectionsUtil;
import cn.windflute.uitls.SignUtil;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zan00
 * @date 2019/8/31.
 */
public class DefaultApiRequestAuthFilter extends BaseApiRequestAuthFilter {

    /**
     * 数据库、缓存、上下文等方式获取应用的id所注册的Key
     * 这里默认使用ServletContext，因为一般接入的系统不多。
     * ServletContext的设置可以使用数据库方式等
     *
     * @param request 请求
     * @param appId   应用id
     * @return appKey
     */
    @Override
    public String getAppKey(PostParameterRequestWrapper request, String appId) {
        ServletContext context = request.getServletContext();
        Object appKey = context.getAttribute(appId);
        return null == appKey ? null : appKey.toString();
    }

    @Override
    public boolean signatureRuleCheck(PostParameterRequestWrapper request, ApiRequestDTO apiRequestDTO, String appKey) {
        return SignUtil.checkTamperResistantSign(
                //获取需要参与签名的列表拼接
                TamperResistantHandler.concatTamperResistantParams(getMeedToSignParamList(request, apiRequestDTO)),
                appKey, apiRequestDTO.getSignature(), TamperResistantHandler.DEFAULT_SALT_LENGTH);
    }

    /**
     * 获取需要参与签名的列表
     *
     * @param request
     * @param apiRequestDTO
     * @return 需要参与签名的列表
     */
    public List<String> getMeedToSignParamList(PostParameterRequestWrapper request, ApiRequestDTO apiRequestDTO) {
        List<String> paramList = new ArrayList<>();
        paramList.add(apiRequestDTO.getAppId());
        paramList.add(apiRequestDTO.getTimestamp());
        List<String> nameList = apiRequestDTO.getTamperResistantNameList();
        if (CollectionsUtil.isEmpty(nameList)) {
            return paramList;
        }
        for (String name : nameList) {
            paramList.add(String.valueOf(request.getParameter(name)));
        }
        return paramList;
    }
}
