package com.yunbao.live.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DiceType {
    public String title;
    public String tag;
    public List<DiceGame> gamelist;
    public int index;

    public void setData(JSONObject obj){
        title = obj.getString("title");
        tag = obj.getString("tag");
        JSONObject item = obj.getJSONObject("list");
        JSONArray tagV = item.getJSONArray("tagV");
        JSONArray tag = item.getJSONArray("tag");
        JSONArray rate = item.getJSONArray("rate");
        JSONArray rateV = item.getJSONArray("rateV");

        gamelist = new ArrayList<>();
        for(int j = 0;j<tag.size();j++){
            DiceGame diceGame = new DiceGame();
            diceGame.tag = tag.getString(j);
            diceGame.tagV = tagV.getString(j);
            diceGame.rate = rate.getString(j);
            diceGame.rateV = rateV.getString(j);
            diceGame.select = false;
            diceGame.supTag = this.tag;
            diceGame.supTitle = this.title;
            gamelist.add(diceGame);
        }
    }
}
