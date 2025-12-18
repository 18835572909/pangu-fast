package com.hz.voa;

import cn.hutool.extra.spring.SpringUtil;
import com.hz.voa.canal.property.CanalServerProperties;
import com.hz.voa.canal.support.MqProducerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 
 * @author rhb
 * @date 2025/12/16 13:25
 **/
@Slf4j
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

        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(RocketMQTemplate.class);
        log.info("beanName: {}", Arrays.asList(beanNamesForType));

        log.info("Service Success!!!");
    }

}
