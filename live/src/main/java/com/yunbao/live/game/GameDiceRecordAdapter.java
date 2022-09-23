package com.yunbao.live.game;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceRecord;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDiceRecordAdapter extends BaseQuickAdapter<DiceRecord, BaseViewHolder> {

    Map<String,Integer> rsimg = new HashMap<String,Integer>(){{
        put("1",R.mipmap.touzi_1);
        put("2",R.mipmap.touzi_2);
        put("3",R.mipmap.touzi_3);
        put("4",R.mipmap.touzi_4);
        put("5",R.mipmap.touzi_5);
        put("6",R.mipmap.touzi_6);
    }};

    public GameDiceRecordAdapter(List<DiceRecord> list) {
        super(R.layout.itemlist_dice_record, list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull DiceRecord item) {
        helper.setText(R.id.title1,item.batch);
        if(rsimg.get(item.one) != null){
            helper.setBackgroundRes(R.id.oneimg,rsimg.get(item.one));
        }
        if(rsimg.get(item.two) != null){
            helper.setBackgroundRes(R.id.twoimg,rsimg.get(item.two));
        }
        if(rsimg.get(item.three) != null){
            helper.setBackgroundRes(R.id.threeimg,rsimg.get(item.three));
        }
        if(item.isdraw != null){
            if(item.isdraw.equals("0")){
                helper.setBackgroundRes(R.id.payrsimgshow,R.mipmap.game_rs_wait);
            }else if(item.isdraw.equals("1")){
                helper.setBackgroundRes(R.id.payrsimgshow,R.mipmap.game_rs_rs1);
            }else{
                helper.setBackgroundRes(R.id.payrsimgshow,R.mipmap.game_rs_rs0);
            }
        }
    }
}
