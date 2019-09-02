# java-http-json接口sdk使用指南
环境要求：JDK1.8+、servlet3.1+
使用前记得clone到本地然后install到本地maven仓库。

## 接口提供方使用方式
首先引入maven依赖或者引入jar包，参考DefaultApiRequestAuthFilter继承BaseApiRequestAuthFilter自定义filter过滤器，接口提供方提供了两种接入方式。
### web.xml配置过滤器接入
注意这里的DefaultApiRequestAuthFilter，都应该换掉使用自定义过滤器，这里只是演示用
```
 <filter>
        <filter-name>DefaultApiRequestAuthFilter</filter-name>
        <filter-class>cn.windflute.http.filter.DefaultApiRequestAuthFilter</filter-class>
        <init-param>
         <!--  最大超前时间，默认30秒 -->
            <param-name>maxLeadingTimeSecond</param-name>
            <param-value>30</param-value>
        </init-param>
        <init-param>
         <!--  最大超时时间，默认30秒 -->
            <param-name>maxTimeoutSecond</param-name>
            <param-value>30</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>DefaultApiRequestAuthFilter</filter-name>
        <!-- 修改成需要拦截的接口地址1  -->
        <url-pattern>URL1</url-pattern>
    </filter-mapping>
     <filter-mapping>
        <filter-name>DefaultApiRequestAuthFilter</filter-name>
        <!-- 修改成需要拦截的接口地址2  -->
        <url-pattern>URL2</url-pattern>
    </filter-mapping>
```
### 整合spring注解接入方式（spring4.3.8+）
修改继承BaseApiRequestAuthFilter自定义filter过滤器，添加注解配置并重写init方法,使springbean的注解生效。

```
@WebFilter(urlPatterns = "修改成需要拦截的接口地址", initParams = {
//最大超前时间，默认30秒
        @WebInitParam(name = "maxLeadingTimeSecond", value = "30"),
// 最大超时时间，默认30秒
        @WebInitParam(name = "maxTimeoutSecond", value = "30"),
})
public class TestFilter extends BaseApiRequestAuthFilter{
//添加需要用到的springbean，这里需要自己写服务，仅供参考
    @Autowired
    IAppKeyService appKeyService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        WebApplicationContextUtils
            .getWebApplicationContext(filterConfig.getServletContext())
            .getAutowireCapableBeanFactory().autowireBean(this);
    }
//使用springbean获取AppKey的方法，仅供参考
    @Override
    public String getAppKey(PostParameterRequestWrapper request, String appId) {
        return appKeyService.getAppKey(request,appId);
    }
}
```
### 接口代码的写法（spring4.3.8+）
接口方法使用@ResponseBody、@PostMapping等注解，如果方法的参数是对象则需要增加@RequestBody注解。
例如：
```
  @ResponseBody
  @PostMapping(value = "find.json")
  public Result find(@RequestBody ApiFindDTO findDTO) {
   // todo
   }
```
## 接口调用方使用方式
```
1.引入maven依赖或者引入jar包
2.使用map传入接口的业务参数或者使用bean对象作为传入接口的业务参数
3.如果使用bean对象作为传入接口的业务参数，可以在对应的属性上添加防篡改的注解@TamperResistantAnnotation(isTransJson=false)，isTransJson可以设置这个字段是否转成json字符串，来兼容集合等非基本类型字段
4.参考无SignWithTamperResistant参数的TamperResistantHandler.getRequestJsonWithSign方法，创建匿名SignWithTamperResistant接口对象实现使用签名方法sign和签名的参数map拼接方法concatForSign
5.传入创建匿名SignWithTamperResistant接口对象调用有SignWithTamperResistant参数的TamperResistantHandler.getRequestJsonWithSign方法
6.步骤3的返回值转成jsonstring，使用OkHttpClientUtil.postJson调用http-json接口即可。这一步的调用方式仅供可参考，也可以用其他的http工具类实现。
```
