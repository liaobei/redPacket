package com.liaobei.redpacket.common.utils;

import java.math.BigDecimal;

/**
 * @Author: liaobei
 */
public class BigDecimalUtils {

    public static Double doubleAdd(Double d1, Double d2) {
        return new BigDecimal(d1.toString()).add(new BigDecimal(d2.toString())).doubleValue();
    }

    public static Double doubleMinus(Double d1, Double d2) {
        return new BigDecimal(d1.toString()).subtract(new BigDecimal(d2.toString())).doubleValue();
    }
}
