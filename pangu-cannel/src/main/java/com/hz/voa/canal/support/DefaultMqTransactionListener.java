package com.hz.voa.canal.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;

/**
 * Half Message - MQ的事务支持
 * @author rhb
 * @date 2025/12/16 22:26
 **/
@Slf4j
public abstract class DefaultMqTransactionListener implements RocketMQLocalTransactionListener {

    private static final String BIZ_ORDER = "BIZ_ORDER";

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            String txId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            String bizId = (String) msg.getHeaders().get(RocketMQHeaders.KEYS);

            byte[] payload = (byte[]) msg.getPayload();
            String body = new String(payload);

            if(match()){
                localTransactionHandle(txId, bizId, body);
                return RocketMQLocalTransactionState.COMMIT;
            }else {
                return RocketMQLocalTransactionState.ROLLBACK;
            }

        }catch (Exception e){
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {

        return null;
    }

    /**
     * 处理本地事务
     */
    protected abstract void localTransactionHandle(String txId, String bizId, String body);

    protected abstract boolean match();

}
