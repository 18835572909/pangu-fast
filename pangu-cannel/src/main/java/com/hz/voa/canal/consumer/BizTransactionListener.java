package com.hz.voa.canal.consumer;

import com.hz.voa.canal.support.DefaultMqTransactionListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;

/**
 * 
 * @author rhb
 * @date 2025/12/16 22:51
 **/
@Slf4j
@RocketMQTransactionListener
public class BizTransactionListener extends DefaultMqTransactionListener {

    @Override
    protected void localTransactionHandle(String txId, String bizId, String body) {
        log.info("txId:{}, bizId:{}, body:{}", txId, bizId, body);
    }

    @Override
    protected boolean match() {
        return true;
    }

}
