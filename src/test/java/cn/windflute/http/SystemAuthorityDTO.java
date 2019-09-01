package cn.windflute.http;

import java.io.Serializable;

/**
 *
 * @author zan00
 * @date 2018/7/7
 */
public class SystemAuthorityDTO implements Serializable {

    private static final long serialVersionUID = -9060402067820870042L;

    private String name;

    private String code;

    private String module;

    private String action;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return "SystemAuthorityDTO{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", module='" + module + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
