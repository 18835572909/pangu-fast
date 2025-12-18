package com.hz.voa.canal.convert;


import java.util.List;

/**
 * 数据转换顶级父类接口
 *
 * @author rhb
 * @date 2025/12/17 14:53
**/
public interface DataConverter<K,V> {

    V singleConvert(K k);

    List<V> batchConvert(List<K> kList);

}
