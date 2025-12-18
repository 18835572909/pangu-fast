package com.hz.voa.canal.support;


import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;

/**
 * @author rhb
 * @date 2025/1/11 2:30
 *
 * RocketMQPushConsumerLifecycleListener  - 可以指明消费的起始位置
 * RocketMQReplyListener - 消费并且回复消息
 * RocketMQListener - 单向的消费消息。
**/
@Slf4j
public abstract class DefaultMessageListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        MDC.put("TRACE_ID", IdUtil.fastSimpleUUID());
        log.info("[{}]开始消费：{}", queueName(), message);
        long startTime = System.currentTimeMillis();
        try {
            handle(message);
        }catch (Exception e){
            log.error("[{}] 消费异常，耗时:{}ms，message={}", queueName(), System.currentTimeMillis() - startTime, message, e);
            throw e;
        }finally {
            log.info("[{}]消费完成 耗时:{}ms", queueName(), System.currentTimeMillis() - startTime);
            MDC.clear();
        }
    }

    protected abstract String queueName();

    protected abstract void handle(String message);

}

