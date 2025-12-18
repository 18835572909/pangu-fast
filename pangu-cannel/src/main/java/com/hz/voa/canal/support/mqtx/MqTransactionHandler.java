package com.hz.voa.canal.support.mqtx;


/**
 * 
 * @author rhb
 * @date 2025/12/18 15:11
**/
public interface MqTransactionHandler {

    boolean exeTx(String txId, TxType bizId, String body);

    boolean checkTx(String txId, TxType bizId, String body);

}
