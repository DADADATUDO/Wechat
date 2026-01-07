package com.winter.utils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 扩展BeanUtils。如：copyProperties、copyToList
 */
public class BeanUtil extends BeanUtils {

    /**
     * 复制属性。与BeanUtils.copyProperties相同，但返回新对象。
     * @param source 源对象
     * @param clazz 目标对象类
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return 目标对象
     */
    public static <S, T> T copyProperties(S source, Class<T> clazz) {
        if (null == source || clazz == null) {
            return null;
        }
        try {
            T target = clazz.newInstance();
            copyProperties(source, target);
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    /**
     * 复制属性列表。与BeanUtils.copyProperties相同，但返回新对象列表。
     * @param sources 源对象列表
     * @param clazz 目标对象类
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return 目标对象列表
     */
    public static <S, T> List<T> copyToList(List<S> sources, Class<T> clazz) {
        if (sources == null || clazz == null) {
            return null;
        }
        List<T> list = new ArrayList<>(sources.size());
        try {
            for (S source : sources) {
                T target = clazz.newInstance();
                copyProperties(source, target);
                list.add(target);
            }
            return list;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }
}
