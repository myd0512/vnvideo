package com.yunbao.live.game;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.live.bean.DiceChip;

public interface DiceGameDelegate {
    public void setChip(DiceChip chip);
    public void setDataAfterActivityCreated();
    public void setBalance(String coin);
    public void setTimeString(String time);

    public void dismissSelf();
    public void setLastResult(String one,String two,String three,String sum,String ds,String dx);
}
