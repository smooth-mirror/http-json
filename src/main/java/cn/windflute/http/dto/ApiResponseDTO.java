package cn.windflute.http.dto;

import com.alibaba.fastjson.JSON;

/**
 * @author zan00
 * @date 2019/8/30.
 */
public class ApiResponseDTO {
    private Integer code;
    private String msg;
    private Object object;

    public static final int ERROR_CODE = 500;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int SUCCESS_CODE = 200;

    public ApiResponseDTO(){

    }

    public ApiResponseDTO(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ApiResponseDTO(int code, String msg, Object object){
        this.code = code;
        this.msg = msg;
        this.object = object;
    }

    public static ApiResponseDTO fail(){
        return new ApiResponseDTO(ERROR_CODE, "error");
    }

    public static ApiResponseDTO fail(String msg){
        return new ApiResponseDTO(ERROR_CODE, msg);
    }

    public static ApiResponseDTO forbidden(){
        return new ApiResponseDTO(FORBIDDEN_CODE, "forbidden");
    }

    public static ApiResponseDTO forbidden(String msg){
        return new ApiResponseDTO(FORBIDDEN_CODE, msg);
    }

    public static ApiResponseDTO not_found(){
        return new ApiResponseDTO(NOT_FOUND_CODE, "not_found");
    }

    public static ApiResponseDTO not_found(String msg){
        return new ApiResponseDTO(NOT_FOUND_CODE, msg);
    }

    public static ApiResponseDTO success(){
        return new ApiResponseDTO(SUCCESS_CODE, "success");
    }

    public static ApiResponseDTO success(Object object){
        return new ApiResponseDTO(SUCCESS_CODE, "success", object);
    }

    public static ApiResponseDTO success(String msg, Object object){
        return new ApiResponseDTO(SUCCESS_CODE, msg, object);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
