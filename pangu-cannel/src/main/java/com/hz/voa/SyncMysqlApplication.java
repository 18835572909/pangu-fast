package com.hz.voa;

import com.hz.voa.canal.property.CanalServerProperties;
import com.hz.voa.canal.support.MqProducerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * 
 * @author rhb
 * @date 2025/12/16 13:25
 **/
@SpringBootApplication
public class SyncMysqlApplication implements CommandLineRunner {

    @Resource
    CanalServerProperties serverProperties;

    @Resource
    MqProducerFactory mqProducerFactory;

    public static void main(String[] args) {
        SpringApplication.run(SyncMysqlApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //new Thread(new DirectCanalClient(serverProperties)).start();
        mqProducerFactory.asyncSend("test","12312");
    }

}
