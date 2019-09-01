package cn.windflute.http;

import cn.windflute.http.annotation.TamperResistantAnnotation;
import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author zan00
 * @date 2018/7/7
 */
public class SystemAuthorityDTO implements Serializable {

    private static final long serialVersionUID = -9060402067820870042L;

    private String name;

    private String module;

    private String action;
    @TamperResistantAnnotation(isTransJson = true)
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
