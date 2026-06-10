package cc.shiyi.coleditor.forum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cc.shiyi")
@EnableAsync
@ComponentScan(basePackages = {"cc.shiyi"})
@MapperScan(basePackages = {
        "cc.shiyi.coleditor.forum.mapper",
        "cc.shiyi.coleditor.markdown.mapper",
        "cc.shiyi.coleditor.user.mapper",
        "cc.shiyi.search.db"
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
