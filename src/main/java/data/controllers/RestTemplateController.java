package data.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author onlyone
 */
@RestController
@RequestMapping("/api")
public class RestTemplateController {

    private final Logger logger = LoggerFactory.getLogger(RestTemplateController.class);

    /**
     * http://localhost:8091/api/query_with_timeout
     */
    @RequestMapping(value = "/query_with_timeout")
    public String queryWithTimeout(HttpServletRequest request, HttpServletResponse response) {
        String url = null;
        // url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15564592781";
        url = "http://www.google.com/";
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(4000);// 设置超时
        requestFactory.setReadTimeout(4000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Charset", "utf-8");
        headers.set("Content-type", "application/json; charset=UTF-8");// 设置编码
        // if (headerObj != null) {
        // headerObj.keySet().forEach(key -> headers.add(key, headerObj.getString(key)));
        // }
        // HttpEntity<JSONObject> httpEntity = new HttpEntity<>(paramObj, headers);

        String result = null;
        Long startTime = System.currentTimeMillis();
        String success = "调用成功";
        try {
            result = restTemplate.postForObject(url, null, String.class);
        } catch (RestClientException e) {
            success = "调用失败";
            e.printStackTrace();
        }
        Long endTime = System.currentTimeMillis();

        return success + ", 共耗费：" + (endTime - startTime) + "毫秒 , result=" + result;
    }
}
