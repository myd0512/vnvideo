package com.yunbao.live.bean;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.HtmlConfig;

public class DiceChip {
    public String img;
    public Integer m;
    public String text;
    public Integer value;

    public DiceChip(JSONObject object){
        super();
        this.img = CommonAppConfig.HOST + object.getString("i");
        this.m = object.getInteger("m");
        this.text = object.getString("t");
        this.value = object.getInteger("v");
    }
}
