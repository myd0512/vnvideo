package com.yunbao.common.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class Moneyformat {

    public static String getSimpMoney(String str){
        String intString = str;
        if(intString.contains(".")){
            int indexof = intString.indexOf(".");
            intString = intString.substring(0,indexof);
        }
        return intString;
    }
    public static String getMoneyStrng(String str){

//        StringBuilder builder = new StringBuilder(str);
//        str = builder.reverse().toString();
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < str.length(); i++) {
//            if (i%3==0) {
//                //防越界&保留最高位
//                if (i+3>str.length()){
//                    stringBuilder.append(str.substring(i));
//                    break;
//                }
//                stringBuilder.append(str.substring(i, i + 3) + ",");
//            }
//        }
//        str = stringBuilder.reverse().toString();
//        //消除字符串长度为3的倍数时多出的','
//        if (str.charAt(0)==','){
//            str = str.substring(1);
//        }
        return str;
//        Integer inte = Integer.parseInt(str);
//        NumberFormat currency = NumberFormat.getCurrencyInstance();
//
//        String intString = str;
//        if(intString.contains(".")){
//            int indexof = intString.indexOf(".");
//            intString = intString.substring(0,indexof);
//        }
//
//
//
//        return intString;

//        BigDecimal loanAmount = new BigDecimal(BigDecimal.valueOf(Double.parseDouble(str)).stripTrailingZeros().toPlainString());
//        return currency.format(loanAmount.intValue());
    }
}
