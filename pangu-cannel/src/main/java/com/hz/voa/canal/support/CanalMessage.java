package com.hz.voa.canal.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author rhb
 * @date 2025/12/17 15:26
 **/
@Data
public class CanalMessage<T> {
    /**
     * 事件ID: Canal 内部的事件序列号，单调递增
     */
    @JsonProperty("id")
    private int id;
    /**
     * Canal 处理该消息的时间（毫秒时间戳）
     */
    @JsonProperty("es")
    private long es;
    /**
     * 变更在数据库中发生的实际时间（毫秒时间戳）
     */
    @JsonProperty("ts")
    private long ts;
    /**
     * 是否ddl
     */
    @JsonProperty("isDdl")
    private boolean isDdl;
    /**
     * 数据库名
     */
    @JsonProperty("database")
    private String database;
    /**
     * 数据库表名
     */
    @JsonProperty("table")
    private String table;
    /**
     * 主键名
     */
    @JsonProperty("pkNames")
    private List<String> pkNames;
    /**
     * change的类型
     */
    @JsonProperty("type")
    private String type;
    /**
     * sql
     */
    @JsonProperty("sql")
    private String sql;
    /**
     * 修改后的数据
     */
    @JsonProperty("data")
    private List<T> data;
    /**
     * 修改前旧的列数据
     */
    @JsonProperty("old")
    private List<T> old;
    /**
     * 表定义
     */
    @JsonProperty("mysqlType")
    private Map<String, String> mysqlType;
    /**
     * 表列的数据类型：java.sql.Types
     */
    @JsonProperty("sqlType")
    private Map<String, Integer> sqlType;
}
