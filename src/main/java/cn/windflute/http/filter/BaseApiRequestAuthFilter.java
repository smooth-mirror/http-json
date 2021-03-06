package cn.windflute.http.filter;

import cn.windflute.http.dto.ApiRequestDTO;
import cn.windflute.http.servlet.PostParameterRequestWrapper;
import cn.windflute.uitls.ReflectUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public abstract class BaseApiRequestAuthFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseApiRequestAuthFilter.class);
  /**
   * 应用验证失败默认提示
   */
  private static final String APP_DEFAULT_ERROR = "app invalid!";

  /**
   * 时间戳，当前秒数
   */
  private static final String POST = "POST";

  /**
   * 超时时间默认30秒
   */
  private int maxTimeoutSecond = 30;


  /**
   * 超前时间默认30秒
   */
  private int maxLeadingTimeSecond = 30;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //有效时间配置
    setValidTimeConfig(filterConfig);
  }

  /**
   * 根据appId获取appKey,获取的方式可以在数据库，简单的情况可以配置文件
   * @param request 请求
   * @param appId 应用id
   * @return appKey
   */
  public abstract String getAppKey(PostParameterRequestWrapper request,String appId);

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    //过滤掉非post请求
    if (!POST.equals(httpServletRequest.getMethod())) {
      LOGGER.error("非post请求！");
      throw new ServletException(APP_DEFAULT_ERROR);
    }
    //包装post请求，重复利用数据流
    PostParameterRequestWrapper request = new PostParameterRequestWrapper(httpServletRequest);
    //获取post请求参数
    ApiRequestDTO apiRequestDTO = getPostParams(request);
    if (null == apiRequestDTO) {
      LOGGER.error("获取post签名参数异常！");
      throw new ServletException(APP_DEFAULT_ERROR);
    }

    String timestamp = apiRequestDTO.getTimestamp();
    String appId = apiRequestDTO.getAppId();
    //验证时间戳
    if (isNotValidTime(timestamp)) {
      LOGGER.error("应用：{}，时间戳异常！", appId);
      throw new ServletException(APP_DEFAULT_ERROR);
    }
    //签名验证
    String signature = apiRequestDTO.getSignature();
    if (null == appId || null == signature) {
      LOGGER.error("数据异常！");
      throw new ServletException(APP_DEFAULT_ERROR);
    }
    String appKey =getAppKey(request,appId);
    if (null == appKey) {
      LOGGER.error("应用：{}，appKey为空！", appId);
      throw new ServletException(APP_DEFAULT_ERROR);
    }

    if (!signatureRuleCheck(request,apiRequestDTO,appKey)) {
      LOGGER.error("应用：{}，签名验证失败！", appId);
      throw new ServletException("签名验证失败！");
    }
    handleRequestBeforeDoFilter(request);
    filterChain.doFilter(request, servletResponse);
  }

  /**
   * 签名验证
   *
   * @param request 请求
   * @param apiRequestDTO 接口请求dto
   * @param appKey 密钥
   * @return 签名验证结果
   */
  public abstract boolean signatureRuleCheck(PostParameterRequestWrapper request,ApiRequestDTO apiRequestDTO,String appKey);

  public void handleRequestBeforeDoFilter(PostParameterRequestWrapper request) {

    try {
      request.setParameterFromRequestInputStream();

    } catch (IOException e) {
      LOGGER.error("设置post参数到Parameter失败",e);
    }
    try {
      request.removeParametersByMapKey(ReflectUtil.transBean2Map(new ApiRequestDTO()));
    } catch (IllegalAccessException e) {
      LOGGER.error("过滤掉参与签名的参数失败！",e);
    }

  }

  private ApiRequestDTO getPostParams(PostParameterRequestWrapper request) throws IOException {
    //获取post参数
    String params = request.getRequestParams();
    return JSON.parseObject(params, ApiRequestDTO.class);
  }

  @Override
  public void destroy() {
    //nothing
  }

  /**
   * 设置有效时间配置
   */
  private void setValidTimeConfig(FilterConfig filterConfig) {
    //设置最大超时
    String maxTimeoutSecondStr = filterConfig.getInitParameter("maxTimeoutSecond");
    if (null != maxTimeoutSecondStr) {
      this.maxTimeoutSecond = Integer.parseInt(maxTimeoutSecondStr);
    }
    //设置最大超前时间
    String maxLeadingTimeSecondStr = filterConfig.getInitParameter("maxLeadingTimeSecond");
    if (null != maxTimeoutSecondStr) {
      this.maxLeadingTimeSecond = Integer.parseInt(maxLeadingTimeSecondStr);
    }
  }

  /**
   * 时间戳是否无效
   *
   * @return boolean
   */
  private boolean isNotValidTime(String timestamp) {

    long time = Long.parseLong(timestamp);
    long currTime = System.currentTimeMillis();
    //时间有效验证
    long validSecond = (currTime - time) / 1000L;
    return validSecond > maxTimeoutSecond || validSecond < -maxLeadingTimeSecond;
  }

}