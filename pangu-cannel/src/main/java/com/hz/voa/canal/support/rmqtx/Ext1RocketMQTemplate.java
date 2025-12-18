package com.hz.voa.canal.support.rmqtx;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;


/**
 * 
 * @author rhb
 * @date 2025/12/18 16:21
 **/
@Slf4j
@ExtRocketMQTemplateConfiguration(group = "pangu_1", nameServer = "${rocketmq.name-server}")
public class Ext1RocketMQTemplate extends RocketMQTemplate {

}
