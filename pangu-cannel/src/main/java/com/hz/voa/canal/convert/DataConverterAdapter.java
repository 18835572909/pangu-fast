package com.hz.voa.canal.convert;

/**
 *
 * @author rhb
 * @date 2025/12/18 12:30
 **/
public abstract class DataConverterAdapter<K,V> implements DataConverter<K,V>{

    /**
     * 扩展build方法
     */
    protected abstract V buildT(K k);

}
