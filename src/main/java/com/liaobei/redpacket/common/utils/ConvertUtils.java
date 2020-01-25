package com.liaobei.redpacket.common.utils;

import org.springframework.beans.BeanUtils;

/**
 * @Author: liaobei
 */
public class ConvertUtils {

    public static <T> T convert(Object source, Class<T> targetClass) {
        try {
            T target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
