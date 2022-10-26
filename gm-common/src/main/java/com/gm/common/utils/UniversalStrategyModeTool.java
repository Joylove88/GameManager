package com.gm.common.utils;

import java.util.Map;

/**
 * 万能策略模式
 * Created by Administrator on 2022/10/20 0020.
 */
public class UniversalStrategyModeTool<K> {
    public Map<K, Function> map;

    /**
     * 通过map类型来保存对应的条件key和方法
     * @param map
     */
    public UniversalStrategyModeTool(Map<K, Function> map) {
        this.map = map;
    }

    /**
     * 添加条件
     *
     * @param key 需要验证的条件
     * @param function 要执行的方法
     * @return
     */
    public UniversalStrategyModeTool<K> add(K key, Function function){
        this.map.put(key, function);
        return this;
    }

    public interface Function {
        /**
         * 要做的事情
         */
        void invoke();
    }

    /**
     * 确定key是否存在，如果存在，则执行相应的方法
     * @param key
     */
    public void doIf(K key){
        if (this.map.containsKey(key)){
            map.get(key).invoke();
        }
    }

    /**
     * 确定key是否存在，如果存在，则执行相应的方法
     * @param key 需要验证的条件(key)
     * @param defaultFunction 要执行的方法
     */
    public void doIfWithDefault(K key, Function defaultFunction){
        if (this.map.containsKey(key)){
            map.get(key).invoke();
        } else {
            defaultFunction.invoke();
        }
    }
}
