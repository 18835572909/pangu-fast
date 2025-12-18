package com.hz.voa.canal.consumer;

import cn.hutool.json.JSONUtil;
import com.hz.voa.canal.sample.OrderTbl;
import com.hz.voa.canal.support.CanalMessageListenerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author rhb
 * @date 2025/12/16 18:12
 **/
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "canal_group", topic = "canal_topic")
public class CanalMessageListener extends CanalMessageListenerAdapter<OrderTbl, OrderTbl> {

    @Override
    protected String queueName() {
        return "canal_topic";
    }


    @Override
    protected boolean checkOrderly(String uniqueKey, long ts) {
        // 顺序逻辑
        return true;
    }

    @Override
    protected OrderTbl dataConvert(OrderTbl source) {
        // 数据转换
        return source;
    }

    @Override
    protected void dataSave(List<OrderTbl> target) {
        // 数据存储
        log.info("NewData Save: {}", JSONUtil.toJsonStr(target));
    }
}
