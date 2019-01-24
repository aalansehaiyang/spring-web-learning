package data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * spring boot工程启动入口
 * 
 * @author onlyone
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
                                   DataSourceTransactionManagerAutoConfiguration.class })
public class WebStart extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebStart.class, "classpath*:/spring/*.xml");
        app.run(args);
    }

}
