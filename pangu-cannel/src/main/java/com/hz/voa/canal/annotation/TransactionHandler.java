package com.hz.voa.canal.annotation;


import com.hz.voa.canal.support.mqtx.TxType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 
 * @author rhb
 * @date 2025/12/18 14:46
**/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface TransactionHandler {

    TxType type();

    String exeTx() default "exeTx";

    String checkTx() default "checkTx";

}
