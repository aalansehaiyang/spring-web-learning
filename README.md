## Spring框架封装httpclient
---

**远程服务时就必须使用HTTP客户端。我们可以使用JDK原生的URLConnection、Apache的Http Client、Netty的异步HTTP Client, Spring的RestTemplate。这里介绍的是RestTemplate。**


###  一、同步 RestTemplate

简单介绍了 getForEntity()、getForObject()、postForEntity()、postForObject() 等几大类重载方法的使用说明。


getForEntity() 的返回值类型 ResponseEntity，继承了 HttpEntity，里面封装了响应信息，包括状态码、响应头和响应体。

常用接口代码示例：https://blog.csdn.net/itguangit/article/details/78825505


**RestTemplate提供了多种便捷访问远程Http服务的方法，大大提高客户端的编码效率。通过使用ClientHttpRequestFactory指定不同的HTTP请求方式。**

##### ClientHttpRequestFactory接口主要提供了两种实现方式：

* 一种是SimpleClientHttpRequestFactory，使用J2SE提供的方式（即 java.net包提供的方式）创建底层的Http请求连接。

* 一种方式是使用HttpComponentsClientHttpRequestFactory方式，底层使用HttpClient访问远程的Http服务，使用HttpClient可以配置连接池和证书等信息。

```
RestTemplate默认使用SimpleClientHttpRequestFactory，内部是调用jdk的HttpConnection，默认超时为-1


```

代码示例：data.controllers.SyncRestTemplateController

#### 网上资料：

* [Spring Cloud Alibaba基础教程：几种服务消费方式（RestTemplate、WebClient、Feign）
](https://mp.weixin.qq.com/s/udVIPbkuoHRyGbIgZDtrpg)

###  二、异步 AsyncRestTemplate

* 设置timeout时间
* 设置连接数
* 可以添加自定义拦截器

代码示例：data.controllers.AsyncRestTemplateController