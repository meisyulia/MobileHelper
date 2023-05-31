package com.example.mobilehelper.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataUtil {
    public static BigDecimal getTwoBigDecimal(BigDecimal bigDecimal){
        return bigDecimal.setScale(2, RoundingMode.HALF_UP);//保留两位小数
    }
}
