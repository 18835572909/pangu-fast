package com.hz.voa.canal.support.mqtx.sample;

import com.hz.voa.canal.annotation.TransactionHandler;
import com.hz.voa.canal.support.mqtx.MqTransactionHandler;
import com.hz.voa.canal.support.mqtx.TxType;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author rhb
 * @date 2025/12/18 15:57
 **/
@Slf4j
@TransactionHandler(type = TxType.ORDER_PAYMENT)
public class SampleWithOrderPaymentTransactionListener implements MqTransactionHandler {

    @Override
    public boolean exeTx(String txId, TxType bizId, String body) {
        log.info("exeTx: {}", TxType.ORDER_PAYMENT);
        return true;
    }

    @Override
    public boolean checkTx(String txId, TxType bizId, String body) {
        log.info("checkTx: {}", TxType.ORDER_PAYMENT);
        return true;
    }
}
