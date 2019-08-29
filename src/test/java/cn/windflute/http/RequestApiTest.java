package cn.windflute.http;

import cn.windflute.http.annotation.TamperResistantHandler;
import cn.windflute.uitls.OkHttpClientUtil;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;

/**
 * @author zan00
 * @date 2019/8/30.
 */
public class RequestApiTest {
    @Test
    public void test() throws IllegalAccessException, IOException {
        String json = JSON.toJSONString(TamperResistantHandler.getRequestJsonWithSign(null,"appId","appKey"));
        String result = OkHttpClientUtil.postJson("http://url",json);
        System.out.print(result);
    }
}
