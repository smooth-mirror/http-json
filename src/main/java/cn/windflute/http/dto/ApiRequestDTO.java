package cn.windflute.http.dto;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.*;

public class ApiRequestDTO implements Serializable {

  /**
   * 签名
   */
  private String signature;
  /**
   * 时间戳
   */
  private String timestamp;
  /**
   * 应用id
   */
  private String appId;
  /**
   * 防篡改参数名列表
   */
  private List<String> tamperResistantNameList;

  public ApiRequestDTO() {
    //nothing
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

  public String toJSONString() {
    return JSON.toJSONString(this);
  }
  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public List<String> getTamperResistantNameList() {
    return tamperResistantNameList;
  }

  public void setTamperResistantNameList(List<String> tamperResistantNameList) {
    this.tamperResistantNameList = tamperResistantNameList;
  }
  public void setTamperResistantNameList(Set<String> tamperResistantNameSet) {
    if (null == tamperResistantNameSet || tamperResistantNameSet.isEmpty()) {
      return;
    }
    tamperResistantNameList = new ArrayList<>();
    for (String key : tamperResistantNameSet) {
      tamperResistantNameList.add(key);
    }
  }

  public Map toMap() {
    Map map = new HashMap<>(4);
    map.put("signature", this.signature);
    map.put("timestamp", this.timestamp);
    map.put("appId", this.appId);
    map.put("tamperResistantNameList", this.tamperResistantNameList);
    return map;
  }

}