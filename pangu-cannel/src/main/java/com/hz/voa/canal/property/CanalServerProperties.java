package com.hz.voa.canal.property;

import com.hz.voa.canal.support.YmlSourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 
 * @author rhb
 * @date 2025/12/16 13:35
 **/
@Data
@Component
@ConfigurationProperties(prefix = "canal.server")
@PropertySource(value = "classpath:canal-server.yml", factory = YmlSourceFactory.class, ignoreResourceNotFound = true)
public class CanalServerProperties {

    private String host;

    private Integer port;

    private String model;

    private String destination;

    private String username;

    private String password;

    private String subscribe;

}
