package com.hz.voa.canal.support.mqtx;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hz.voa.canal.annotation.TransactionHandler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Half Message - MQ的事务支持
 * @author rhb
 * @date 2025/12/16 22:26
 **/
@Slf4j
@RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
public class GlobalMqTransactionListener implements RocketMQLocalTransactionListener {

    private ConcurrentHashMap<TxType, Triple<String,Method,Method>> txContext = new ConcurrentHashMap<>(8);

    @PostConstruct
    public void init(){
        ApplicationContext ac = SpringUtil.getApplicationContext();

        Map<String, Object> beansWithAnnotation = ac.getBeansWithAnnotation(TransactionHandler.class);
        beansWithAnnotation.forEach((beanName, bean)->{
            TransactionHandler annotation = bean.getClass().getAnnotation(TransactionHandler.class);

            TxType txType = annotation.type();
            String exeTxMethodName = annotation.exeTx();
            String checkTxName = annotation.checkTx();

            Method[] declaredMethods = bean.getClass().getDeclaredMethods();

            Method txMethod = null, checkTxMethod = null;
            for (Method m : declaredMethods){
                if(StrUtil.equals(m.getName(), exeTxMethodName)){
                    txMethod = m;
                }else if(StrUtil.equals(m.getName(), checkTxName)){
                    checkTxMethod = m;
                }
            }

            txContext.put(txType, Triple.of(beanName, txMethod, checkTxMethod));
        });
    }

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            MsgBody msgBody = getMsgBody(msg);
            invoke(FlowOrder.FIRST, msgBody.getBizType(), msgBody.getTxId(), msgBody.getBody());
            return RocketMQLocalTransactionState.COMMIT;
        }catch (Exception e){
            log.error("global.tx.exe error:{}", e.getMessage());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try{
            log.info("tx check !!!");
            MsgBody msgBody = getMsgBody(msg);
            invoke(FlowOrder.END, msgBody.getBizType(), msgBody.getTxId(), msgBody.getBody());
            return RocketMQLocalTransactionState.COMMIT;
        }catch (Exception e){
            log.error("global.tx.check error:{}", e.getMessage());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    private MsgBody getMsgBody(Message msg){
        MessageHeaders headers = msg.getHeaders();
        byte[] payload = (byte[]) msg.getPayload();
        String body = new String(payload);
        String txId = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        String sysTxId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        String bizType = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.KEYS);
        return new MsgBody.MsgBodyBuilder()
                .txId(txId)
                .sysTxId(sysTxId)
                .body(body)
                .bizType(TxType.nameToEnum(bizType))
                .build();
    }

    private void invoke(FlowOrder flowOrder, TxType bizType, String txId, String body) throws Exception {
        Triple<String, Method, Method> txHandleMetaDate = txContext.get(bizType);
        Object bean = SpringUtil.getBean(txHandleMetaDate.getLeft());
        Method method = flowOrder.equals(FlowOrder.FIRST) ? txHandleMetaDate.getMiddle() : txHandleMetaDate.getRight();
        method.invoke(bean, txId, bizType, body);
    }

    @Data
    @Builder
    public static class MsgBody{
        private String body;
        private String txId;
        private String sysTxId;
        private TxType bizType;
    }
}
