package hk.fish.fishpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("hk.fish.fishpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class FishPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishPictureBackendApplication.class, args);
    }

}
