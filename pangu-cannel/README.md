# 通用Mysql数据同步方案（CDC-Canal）:

> Canal 是是通过解析数据库的 binlog 日志，捕获数据库的增删改操作，并将这些操作转化为可读的数据格式，比如 json，以便其他应用程序进行消费和处理。

## 版本

- Canal-admin: 1.1.5
- Canal-Server: 1.1.5
- Mysql: 5.6 (阿里云-docker部署)
- RocketMq: 5.2.0

## 部署引导

1. 下载
2. 启动Canal-admin
3. 启动Canal-server
4. canal-admin中配置canal.properties
```java
canal.serverMode = rocketMQ

# RocketMQ
rocketmq.producer.group = canal_group
rocketmq.enable.message.trace = false
rocketmq.customized.trace.topic =
rocketmq.namespace =
rocketmq.namesrv.addr = 127.0.0.1:9876
rocketmq.retry.times.when.send.failed = 0
rocketmq.vip.channel.enabled = false
rocketmq.tag = 
```
5. canal-admin中配置instance.properties
```java
# table regex
canal.instance.filter.regex=db_0\\..*
# table black regex
canal.instance.filter.black.regex=
# table field filter(format: schema1.tableName1:field1/field2,schema2.tableName2:field1/field2)
#canal.instance.filter.field=test1.t_product:id/subject/keywords,test2.t_company:id/name/contact/ch
# table field black filter(format: schema1.tableName1:field1/field2,schema2.tableName2:field1/field2)
#canal.instance.filter.black.field=test1.t_product:subject/product_image,test2.t_company:id/name/contact/ch

# mq config
canal.mq.topic=canal_topic
# dynamic topic route by schema or table regex
#canal.mq.dynamicTopic=mytest1.user,mytest2\\..*,.*\\..*
canal.mq.partition=0
# hash partition config
#canal.mq.partitionsNum=3
#canal.mq.partitionHash=test.table:id^name,.*\\..*
```
## 项目简介

- 直连Canal： com.hz.voa.canal.consumer.DirectCanalClient
- 使用RocketMq： com.hz.voa.canal.consumer.CanalMessageListener

## 可优化项 

### 2025.12.17
1. 抽象转换器Converter 
   2. 提供默认方式一： Mysql的Table字段不动，直接存储
   3. 提供默认方式二： 多个Table复合查询to宽维度索引
2. 整合Kafka
3. 整合RabbitMq
4. 封装新的starter根据配置，装载相关的转换

### 2025.12.30
1. xxx
2. xxx



