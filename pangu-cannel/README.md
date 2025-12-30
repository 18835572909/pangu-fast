# 通用Mysql数据同步方案（CDC-Canal）:

> Canal 是是通过解析数据库的 binlog 日志，捕获数据库的增删改操作，并将这些操作转化为可读的数据格式，比如 json，以便其他应用程序进行消费和处理。

---

## 版本

- Canal-admin: 1.1.5
- Canal-Server: 1.1.5
- Mysql: 5.6 (阿里云-docker部署)
- RocketMq: 5.2.0

---

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
---

## 项目简介

- 直连Canal： com.hz.voa.canal.consumer.DirectCanalClient
- 使用RocketMq： com.hz.voa.canal.consumer.CanalMessageListener

---

## 重点功能

#### Cannel消费

1. 如何保证消息的顺序性？
   1. 存储cannel推送的ts数据库中发生的实际时间，校验当前推送消息在ts之后的数据才处理
   2. 使用Rocketmq的顺序消息sendOrderly()和单线程消费保证顺序性【不建议】
2. 如果保证处理binlog文件突发的高峰问题？
   1. 使用MQ等消息中间件，削峰
   2. 异步多线程消费提升消费速率

#### RocketMq事务

1. 定义全局事务监听处理事务
   - 定义业务类型枚举类：TxType
   - 定义处理业务标识的注释： @TransactionHandler
   - 定义全局事务接收器：GlobalMqTransactionListener
   - 适配不同业务处理： 根据@TransactionHandler中的txType属性，标识处理业务类型
   
2. 使用不同的RocketMQTemplate处理事务
   - 定义不同的RocketMQTemplate: @ExtRocketMQTemplateConfiguration
   - 创建工厂类，用于创建使用不同Template发送事务消息： MqTxFactory

#### RocketMq的延迟消费

1. 定义延迟等待时间： MqDelayLevel
2. broker中先发送消息到Topic中： Schedule_TOPIC_XXX
3. 同时commitLog原本存放tag的位置，存放到期时间

---

## 可优化项 

#### 2025.12.17
1. 抽象转换器Converter 
   2. 提供默认方式一： Mysql的Table字段不动，直接存储
   3. 提供默认方式二： 多个Table复合查询to宽维度索引
2. 整合Kafka
3. 整合RabbitMq
4. 封装新的starter根据配置，装载相关的转换




