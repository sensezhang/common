package com.zy.common.producer.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by zhangwb on 2017/7/10.
 */
public class SamplePartition implements Partitioner {
    /**
     * constructor
     * author：zxh
     * @param verifiableProperties
     * description： 去除该构造方法后启动producer报错NoSuchMethodException
     */
    public SamplePartition(VerifiableProperties verifiableProperties) {

    }

    public int partition(Object o, int i) {
        // 对partitions数量取模
        return Integer.parseInt(o.toString()) % i;
    }
}
