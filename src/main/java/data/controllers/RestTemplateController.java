package data.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
     * <pre>
     *    采用 SimpleClientHttpRequestFactory
     *    
     *    http://localhost:8091/api/query_with_timeout_simpleclient
     * </pre>
     */
    @RequestMapping(value = "/query_with_timeout_simpleclient")
    public String queryWithTimeoutSimpleclient(HttpServletRequest request, HttpServletResponse response) {
        String url = null;
        // url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15564592781";
        url = "http://www.google.com/";

        RestTemplate restTemplate = new RestTemplate();

        // 超时配置
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(4000);// 设置超时
        requestFactory.setReadTimeout(4000);
        restTemplate.setRequestFactory(requestFactory);

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

    /**
     * <pre>
     *    采用 HttpComponentsClientHttpRequestFactory
     *
     *    http://localhost:8091/api/query_with_timeout_httpcomponent
     * </pre>
     */
    @RequestMapping(value = "/query_with_timeout_httpcomponent")
    public String queryWithTimeoutHttpcomponent(HttpServletRequest request, HttpServletResponse response) {
        String url = null;
         url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15564592781";
//        url = "http://www.google.com/";

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        // 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30,
                                                                                                             TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(1000);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(1000);

        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(1, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        // 请求header
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent",
                                    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));

        httpClientBuilder.setDefaultHeaders(headers);
        HttpClient httpClient = httpClientBuilder.build();

        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接池获取连接的超时时间
        clientHttpRequestFactory.setConnectionRequestTimeout(200);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(5000);
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(5000);

        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        // clientHttpRequestFactory.setBufferRequestBody(false);

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

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
