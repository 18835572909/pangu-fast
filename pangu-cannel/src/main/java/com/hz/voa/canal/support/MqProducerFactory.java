package com.hz.voa.canal.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.IdUtil;
import com.hz.voa.canal.support.mqtx.TxType;
import com.hz.voa.canal.support.rmqtx.Ext1RocketMQTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 
 * @author rhb
 * @date 2025/12/16 21:53
 **/
@Slf4j
@Component
public class MqProducerFactory {

    @Resource
    RocketMQTemplate rocketMQTemplate;

    private Message<String> buildMsg(String msgBody,String msgId){
        return MessageBuilder.withPayload(msgBody)
                .setHeader(RocketMQHeaders.MESSAGE_ID, msgId)
                .build();
    }

    public static String buildMsgId(){
        return IdUtil.getSnowflake().nextIdStr();
    }

    /**
     * 同步发送
     */
    public void syncSend(String topic, String msgBody){
        String msgId = buildMsgId();
        rocketMQTemplate.syncSend(topic, buildMsg(msgBody, msgId));
    }

    public void asyncSend(String topic, String msgBody){
        String msgId = buildMsgId();
        rocketMQTemplate.asyncSend(topic, buildMsg(msgBody, msgId), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if(sendResult.getSendStatus()== SendStatus.SEND_OK){
                    log.info("topic: {}, msgId:{}  ok!", topic, msgId);
                }else{
                    log.info("topic: {}, msgId:{}  fail!", topic, msgId);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("topic: {}, msgId:{}  error:{}", topic, msgId, ExceptionUtil.stacktraceToOneLineString(throwable));
            }
        });
    }

    public void sendOnWay(String topic, String msgBody){
        String msgId = buildMsgId();
        rocketMQTemplate.sendOneWay(topic, buildMsg(msgBody, msgId));
    }

    public void sendOneWayOrderly(String topic, String msgBody, String orderlyKey){
        String msgId = buildMsgId();
        rocketMQTemplate.sendOneWayOrderly(topic, buildMsg(msgBody, msgId), orderlyKey);
    }

    public void asyncSendDelay(String topic, String msgBody, long timeout, int level){
        String msgId = buildMsgId();
        rocketMQTemplate.asyncSend(topic, buildMsg(msgBody, msgId),  new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if(sendResult.getSendStatus()== SendStatus.SEND_OK){
                    log.info("topic: {}, msgId:{}  ok!", topic, msgId);
                }else{
                    log.info("topic: {}, msgId:{}  fail!", topic, msgId);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("topic: {}, msgId:{}  error:{}", topic, msgId, ExceptionUtil.stacktraceToOneLineString(throwable));
            }
        }, timeout, level);
    }

    public void asyncSendDelay30Min(String topic, String msgBody){
        asyncSendDelay(topic, msgBody, 1000, MqDelayLevel.LEVEL_30M);
    }

}
