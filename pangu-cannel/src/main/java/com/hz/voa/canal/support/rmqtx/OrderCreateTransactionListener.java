package com.hz.voa.canal.support.rmqtx;

import com.hz.voa.canal.support.mqtx.TxType;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * 
 * @author rhb
 * @date 2025/12/18 16:26
 **/
@Slf4j
@RocketMQTransactionListener(rocketMQTemplateBeanName = "ext1RocketMQTemplate")
public class OrderCreateTransactionListener implements RocketMQLocalTransactionListener {
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();
        byte[] payload = (byte[]) msg.getPayload();
        String body = new String(payload);
        String txId = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        log.info("exe.tx.create txId:{}, body:{}", txId, body);
        return null;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String body = new String( (byte[]) msg.getPayload());
        String txId = (String) msg.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        log.info("check.tx.create txId:{}, body:{}", txId, body);
        return null;
    }
}
