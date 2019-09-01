package cn.windflute.http;

import cn.windflute.http.annotation.TamperResistantHandler;
import cn.windflute.http.dto.ApiResponseDTO;
import cn.windflute.uitls.OkHttpClientUtil;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zan00
 * @date 2019/8/30.
 */
public class RequestApiTest {
    @Test
    public void test() throws IllegalAccessException, IOException {

        SystemAuthorityDTO authorityDTO =new SystemAuthorityDTO();
        authorityDTO.setAction("11");
        authorityDTO.setModule("rtest");
        authorityDTO.setName("ddd");
        List<String> list = new ArrayList<>();
        list.add("ddd");
        list.add("aaa");
        authorityDTO.setList(list);
        String json = JSON.toJSONString(TamperResistantHandler.getRequestJsonWithSign(authorityDTO,"test","test11111"));
        System.out.println(json);
        String result = OkHttpClientUtil.postJson("http://localhost:8080/json/test",json);
        System.out.println(result);
        System.out.println(JSON.toJSONString((null)));
    }
}
