package com.hz.voa.canal.support;

import com.hz.voa.canal.support.mqtx.TxType;
import com.hz.voa.canal.support.rmqtx.Ext1RocketMQTemplate;
import com.hz.voa.canal.support.rmqtx.Ext2RocketMQTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 
 * @author rhb
 * @date 2025/12/18 17:23
 **/
@Slf4j
@Component
public class MqTxFactory {

    @Resource
    RocketMQTemplate rocketMQTemplate;
    @Resource
    Ext1RocketMQTemplate ext1RocketMQTemplate;
    @Resource
    Ext2RocketMQTemplate ext2RocketMQTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event){
        if (event.getApplicationContext().getParent() == null) {
            log.info("rocketMQTemplate.group: " + rocketMQTemplate.getProducer().getProducerGroup());
            log.info("ext1RocketMQTemplate.group: " + ext1RocketMQTemplate.getProducer().getProducerGroup());
            log.info("ext2RocketMQTemplate.group: " + ext2RocketMQTemplate.getProducer().getProducerGroup());
        }
    }

    private Message<String> buildTransactionMsg(String msgBody, String msgId, String txId, TxType bizId){
        return MessageBuilder.withPayload(msgBody)
                .setHeader(RocketMQHeaders.MESSAGE_ID, msgId)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, txId)
                .setHeader(RocketMQHeaders.KEYS, bizId)
                .build();
    }

    public void sendTransactionMsg_1(String topic,String msgBody,String txId, TxType bizId){
        String msgId = MqProducerFactory.buildMsgId();
        // arg: 扩展参数，本地方法传输，只能在当前线程链路使用，回查不能使用，不可以跨线程
        ext1RocketMQTemplate.sendMessageInTransaction(topic, buildTransactionMsg(msgBody, msgId, txId, bizId), null);
    }

    public void sendTransactionMsg_2(String topic,String msgBody,String txId, TxType bizId){
        String msgId = MqProducerFactory.buildMsgId();
        // arg: 扩展参数，本地方法传输，只能在当前线程链路使用，回查不能使用，不可以跨线程
        ext2RocketMQTemplate.sendMessageInTransaction(topic, buildTransactionMsg(msgBody, msgId, txId, bizId), null);
    }

    public void sendTransactionMsg(String topic,String msgBody,String txId, TxType bizId){
        String msgId = MqProducerFactory.buildMsgId();
        // arg: 扩展参数，本地方法传输，只能在当前线程链路使用，回查不能使用，不可以跨线程
        rocketMQTemplate.sendMessageInTransaction(topic, buildTransactionMsg(msgBody, msgId, txId, bizId), null);
    }

}
