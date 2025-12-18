package com.hz.voa.canal.support.mqtx;


import java.util.Arrays;
import java.util.Optional;

/**
 * @author rhb
 * @date 2025/12/18 14:49
 **/
public enum TxType {

    ORDER_CREATE,
    ORDER_PAYMENT,
    ORDER_REFUND,
    ;

    public static TxType nameToEnum(String name){
        Optional<TxType> first = Arrays.stream(TxType.values()).filter(x -> x.name().equalsIgnoreCase(name)).findFirst();
        return first.orElse(null);
    }
}
