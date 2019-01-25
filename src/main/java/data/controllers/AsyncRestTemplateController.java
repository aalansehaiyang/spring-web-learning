package data.controllers;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * <pre>
 *  异步调用
 *
 *  &#64;author onlyone
 * </pre>
 */

@RestController
@RequestMapping("/api/async")
public class AsyncRestTemplateController {

    private final Logger logger = LoggerFactory.getLogger(AsyncRestTemplateController.class);

    /**
     * <pre>
     *  采用 HttpComponentsClientHttpRequestFactory
     *
     *  http://localhost:8091/api/async/query_with_timeout_httpcomponent
     * </pre>
     */
    @RequestMapping(value = "/query_with_timeout_httpcomponent")
    public String queryWithTimeoutHttpcomponent(HttpServletRequest request,
                                                HttpServletResponse response) throws IOReactorException,
                                                                              KeyStoreException,
                                                                              NoSuchAlgorithmException,
                                                                              KeyManagementException {
        String url = null;
        url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15564592781";
        // url = "http://www.google.com/";
        // url = "https://www.facebook.cn/";

        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        // 设置超时时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(3000).setConnectionRequestTimeout(3000).build();
        CloseableHttpAsyncClient asyncClient = HttpAsyncClientBuilder.create().setConnectionManager(connectionManager).setDefaultRequestConfig(config).build();

        HttpComponentsAsyncClientHttpRequestFactory asyncHttpRequestFactory = new HttpComponentsAsyncClientHttpRequestFactory(asyncClient);

        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(asyncHttpRequestFactory);

        // 拦截器设置
        // asyncRestTemplate.getInterceptors().add(new LogClientHttpRequestInterceptor());

        Long startTime = System.currentTimeMillis();
        Long endTime = 0L;
        String success = "调用成功";
        ListenableFuture<ResponseEntity<String>> resultFuture = null;
        String result = "";
        try {
            // 调用完后立即返回（没有阻塞）
            resultFuture = asyncRestTemplate.getForEntity(url, String.class);
            endTime = System.currentTimeMillis();
            // 异步调用后的回调函数
            resultFuture.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {

                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("asyncRestTemplate call onFailure! 耗时="
                                       + (System.currentTimeMillis() - startTime));
                    ex.printStackTrace();
                }

                @Override
                public void onSuccess(ResponseEntity<String> result) {
                    System.out.println("asyncRestTemplate call onSuccess! result=" + result.getBody());
                }
            });
            // result = resultFuture.get(3, TimeUnit.SECONDS).getBody();
        } catch (Exception e) {
            success = "调用失败";
            e.printStackTrace();
        }

        return success + ", 共耗费：" + (endTime - startTime) + "毫秒 , result=" + result;

    }
}
