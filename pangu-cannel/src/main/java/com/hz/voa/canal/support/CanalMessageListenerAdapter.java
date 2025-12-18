package com.hz.voa.canal.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author rhb
 * @date 2025/12/17 16:03
 **/
public abstract class CanalMessageListenerAdapter <K,V> extends DefaultMessageListener{

    private final Class<K> sourceType;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public CanalMessageListenerAdapter() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            this.sourceType = (Class<K>) typeArguments[0];
        } else {
            throw new RuntimeException("泛型类型未指定");
        }

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * 获取CanalMessage的具体类型
     */
    protected Type getCanalMessageType() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { sourceType };
            }

            @Override
            public Type getRawType() {
                return CanalMessage.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    @Override
    protected void handle(String message) {
        //CanalMessage<K> canalMsg = JSONUtil.toBean(message, new TypeReference<CanalMessage<K>>() {
        //}, false);

        try {
            // 使用带具体类型的TypeReference
            Type canalMessageType = getCanalMessageType();

            // 使用Jackson的TypeReference
            com.fasterxml.jackson.core.type.TypeReference<CanalMessage<K>> typeRef =
                    new com.fasterxml.jackson.core.type.TypeReference<CanalMessage<K>>() {
                        @Override
                        public Type getType() {
                            return canalMessageType;
                        }
                    };

            CanalMessage<K> canalMsg = objectMapper.readValue(message, typeRef);

            if (checkOrderly(canalMsg.getPkNames().get(0), canalMsg.getTs())) {
                List<K> sources = canalMsg.getData();
                List<V> targets = sources.stream().map(this::dataConvert).collect(Collectors.toList());
                dataSave(targets);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 保证有序性：可以后续扩展默认实现类：根据uniqueKey查询目标数据，ts判断顺序
     */
    protected abstract boolean checkOrderly(String uniqueKey, long ts);

    /**
     * 数据转换： 旧格式=>新格式
     */
    protected abstract V dataConvert(K source);

    /**
     * 新数据存储： es或者redis
     */
    protected abstract void dataSave(List<V> target);

}
