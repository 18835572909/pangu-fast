package com.hz.voa.canal.sample;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.hz.voa.canal.support.MqTxFactory;
import com.hz.voa.canal.support.mqtx.TxType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author rhb
 * @date 2025/12/18 14:12
 **/
@RestController
@RequestMapping("/test")
public class TestCtrl {


    @Resource
    MqTxFactory mqTxFactory;

    @GetMapping("/ts/{type}/{count}")
    public String transactionSend(@PathVariable Integer type, @PathVariable Integer count){
        for(int i = 0; i< count; i++){
            Map<String,String> tempData = new HashMap<>();
            tempData.put("orderNo", "oo_"+i);

            switch (type){
                case -1:
                    mqTxFactory.sendTransactionMsg("SHOP_ORDER_MAIN_", JSONUtil.toJsonStr(tempData), IdUtil.fastSimpleUUID(), TxType.ORDER_CREATE);
                    break;
                case 0:
                    mqTxFactory.sendTransactionMsg("SHOP_ORDER_MAIN_", JSONUtil.toJsonStr(tempData), IdUtil.fastSimpleUUID(), TxType.ORDER_PAYMENT);
                    break;
                case 1:
                    mqTxFactory.sendTransactionMsg_1("SHOP_ORDER:CREATE_"+count, JSONUtil.toJsonStr(tempData), IdUtil.fastSimpleUUID(), TxType.ORDER_CREATE);
                    break;
                case 2:
                    mqTxFactory.sendTransactionMsg_2("SHOP_ORDER:PAYMENT_"+count, JSONUtil.toJsonStr(tempData), IdUtil.fastSimpleUUID(), TxType.ORDER_PAYMENT);
                    break;
            }
        }
        return "事务消息："+ count;
    }

}
