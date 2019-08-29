package cn.windflute.uitls;

import cn.windflute.http.dto.ApiResponseDTO;
import okhttp3.*;

import java.io.IOException;

import java.util.concurrent.TimeUnit;


public class OkHttpClientUtil {

  /**
   * 懒汉 安全 加同步 私有的静态成员变量 只声明不创建 私有的构造方法 提供返回实例的静态方法
   */
  private static OkHttpClient okHttpClient = null;

  private OkHttpClientUtil() {
  }

  public static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");
  private static final int SUCCESS_CODE = 200;

  public static OkHttpClient getDefaultInstance() {

    if (okHttpClient == null) {
      //加同步安全
      synchronized (OkHttpClientUtil.class) {
        okHttpClient = new OkHttpClient.Builder()
                //连接超时
                .connectTimeout(15, TimeUnit.SECONDS)
                //写入超时
                .writeTimeout(60, TimeUnit.SECONDS)
                //读取超时
                .readTimeout(45, TimeUnit.SECONDS)
                .build();
      }
    }
    return okHttpClient;
  }
  /**
   * post请求
   */
  public static String postJson(String url, String json) throws IOException {
    RequestBody body = RequestBody.create(APPLICATION_JSON, json);
    Request request = new Request.Builder().url(url).post(body).build();
    ;
    Response response = getDefaultInstance().newCall(request).execute();
    return result(response);
  }

  private static String result(Response response) throws IOException {
    if (response.code() == SUCCESS_CODE) {
      return response.body().string();
    } else {
      return new ApiResponseDTO(response.code(),response.message()).toJSONString();
    }
  }

}