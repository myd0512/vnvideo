package com.yunbao.mall.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class SearchGoodsBean {

    private String mId;
    private String mName;
    private String mSaleNum;
    private String mThumb;
    private String mPrice;
    private boolean mChecked;


    @JSONField(name = "id")
    public String getId() {
        return mId;
    }
    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }
    @JSONField(name = "name")
    public String getName() {
        return mName;
    }
    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }
    @JSONField(name = "sale_nums")
    public String getSaleNum() {
        return mSaleNum;
    }
    @JSONField(name = "sale_nums")
    public void setSaleNum(String saleNum) {
        mSaleNum = saleNum;
    }
    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }
    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }
    @JSONField(name = "price")
    public String getPrice() {
        return mPrice;
    }
    @JSONField(name = "price")
    public void setPrice(String price) {
        mPrice = price;
    }
    @JSONField(serialize = false)
    public boolean isChecked() {
        return mChecked;
    }
    @JSONField(serialize = false)
    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
