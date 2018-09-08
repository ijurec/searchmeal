package com.example.searchmeal.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    public static String roundingRank(String rank) {
        double rankDouble = Double.parseDouble(rank);
//        BigDecimal bigDecimal = new BigDecimal(rankDouble);
//        BigDecimal newDecimal = bigDecimal.setScale(2, RoundingMode.DOWN);
//        rankDouble = newDecimal.doubleValue();
        rankDouble = (double) ((long) (rankDouble * 100)) / 100;
        return String.valueOf(rankDouble);
    }
}
