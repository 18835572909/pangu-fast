package com.hz.voa.canal.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.hz.voa.canal.property.CanalServerProperties;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author rhb
 * @date 2025/12/16 13:29
 **/
public class DirectCanalClient implements Runnable{

    private CanalConnector connector;

    private String subscribeReg;

    public DirectCanalClient(CanalServerProperties canalConfig){
        connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalConfig.getHost(), canalConfig.getPort()),
                canalConfig.getDestination(),
                canalConfig.getUsername(),
                canalConfig.getPassword());

        subscribeReg = StrUtil.isNotEmpty(canalConfig.getSubscribe()) ? canalConfig.getSubscribe() : "";
    }

    @Override
    public void run() {
        while (true) {
            try {
                connector.connect();
                // 订阅数据库
                connector.subscribe(subscribeReg);
                // 获取数据
                Message message = connector.get(100);
                // 获取Entry集合
                List<CanalEntry.Entry> entries = message.getEntries();
                // 判断集合是否为空,如果为空,则等待一会继续拉取数据
                if (entries.size() <= 0) {
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(DateUtil.now() + ": 数据没有改动");
                } else {
                    // 遍历entries，单条解析
                    for (CanalEntry.Entry entry : entries) {
                        //1.获取表名
                        String tableName = entry.getHeader().getTableName();
                        //2.获取类型
                        CanalEntry.EntryType entryType = entry.getEntryType();
                        //3.获取序列化后的数据
                        ByteString storeValue = entry.getStoreValue();
                        //4.判断当前entryType类型是否为ROWDATA
                        if (CanalEntry.EntryType.ROWDATA.equals(entryType)) {
                            //5.反序列化数据
                            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(storeValue);
                            //6.获取当前事件的操作类型
                            CanalEntry.EventType eventType = rowChange.getEventType();
                            //7.获取数据集
                            List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                            //8.遍历rowDataList，并打印数据集
                            for (CanalEntry.RowData rowData : rowDataList) {
                                JSONObject beforeData = new JSONObject();
                                List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
                                for (CanalEntry.Column column : beforeColumnsList) {
                                    beforeData.put(column.getName(), column.getValue());
                                }
                                JSONObject afterData = new JSONObject();
                                List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
                                for (CanalEntry.Column column : afterColumnsList) {
                                    afterData.put(column.getName(), column.getValue());
                                }
                                //数据打印
                                System.out.println("Table:" + tableName + "\n" +
                                        ",EventType:" + eventType + "\n" +
                                        ",Before:" + beforeData + "\n" +
                                        ",After:" + afterData);
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                connector.disconnect();
            }
        }
    }

}
