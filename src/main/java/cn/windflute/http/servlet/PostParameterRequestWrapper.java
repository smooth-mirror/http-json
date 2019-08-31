package cn.windflute.http.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PostParameterRequestWrapper extends HttpServletRequestWrapper {

  HttpServletRequest orgRequest = null;

  private final byte[] bytes;

  /**
   * 用于存储请求参数
   */
  private Map<String , String[]> params = new HashMap<>(5);

  public PostParameterRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    this.orgRequest = request;
    this.params.putAll(request.getParameterMap());
    //读取输入流的请求参数，保存到bytes中
    this.bytes = IOUtils.toByteArray(request.getInputStream());
  }

  /**
   * 添加参数到map中
   * @param name 参数名
   * @param value 参数值
   */
  public void setParameter(String name, Object value) {
    if (null != value) {
      if (value instanceof String[]) {
        params.put(name, (String[]) value);
      } else if (value instanceof String) {
        params.put(name, new String[]{(String) value});
      } else {
        params.put(name, new String[]{String.valueOf(value)});
      }
    }
  }



  /**
   * 添加map的参数到请求的map中
   * @param map 需要设置的map
   */
  public void setParametersByMap(Map<String,Object> map) {
    if(null==map){
      return;
    }

    Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
    Map.Entry<String, Object> entry = null;
    while (entries.hasNext()) {
       entry = entries.next();
      setParameter(entry.getKey(),entry.getValue());
    }

  }

  /**
   * 根据map删除参数
   * @param map 需要删除的属性
   */
  public void removeParametersByMapKey(Map<String,Object> map) {
    if(null==map){
      return;
    }
   //遍历map中的键
    for (String key : map.keySet()) {
      params.remove(key);
    }
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    return new ServletInputStream() {

      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
        //nothing
      }

      @Override
      public int read() throws IOException {
        return bais.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  /**
   * 获取post参数
   */
  public String getRequestParams() throws IOException {
    return new String(bytes, this.getCharacterEncoding());
  }

  /**
   * 获取post参数的json对象
   * @return json对象
   * @throws IOException
   */
  public JSONObject getPostParamJson() throws IOException {
    return JSON.parseObject(getRequestParams());
  }
  /**
   * enctype是application/x-www-form-urlencoded
   * 可以使用该方式获取参数列表
   * 清洗参数，防止xss注入
   * @param parameter 参数名
   */
  public String[] getParameterValuesByXssEncode(String parameter)
  {
    String[] values =  getParameterValues(parameter);
    int count = values.length;
    String[] encodedValues = new String[count];
    for (int i = 0; i < count; i++) {
      encodedValues[i] = xssEncode(values[i]);
    }
    return encodedValues;
  }

  /**
   * 获取参数列表
   * @param parameter 参数名
   * @return 参数列表
   */
  @Override
  public String[] getParameterValues(String parameter)
  {
    String[] values =  params.get(parameter);
    if (values == null) {
      return new String[0];
    }

    return values;
  }

  /**
   * enctype是application/x-www-form-urlencoded
   * 可以使用该方式获取参数值
   * @param name
   * @return
   */
  @Override
  public String getParameter(String name)
  {
    String[] value = getParameterValues(name);
    if (value.length==0) {
      return null;
    }
    return value[0];
  }

  @Override
  public Map<String, String[]> getParameterMap()
  {
    return params;
  }

  /**
   * 添加输入流的参数到Parameter中
   */
  public void setParameterFromRequestInputStream() throws IOException {
    setParametersByMap(getPostParamJson());
  }

  private static String xssEncode(String s)
  {
    return StringEscapeUtils.escapeHtml4(s);
  }

  public HttpServletRequest getOrgRequest() {
    return this.orgRequest;
  }


}