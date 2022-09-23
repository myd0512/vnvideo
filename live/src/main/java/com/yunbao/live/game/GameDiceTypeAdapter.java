package com.yunbao.live.game;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceType;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class GameDiceTypeAdapter extends BaseQuickAdapter<DiceType, BaseViewHolder> {

    public int selected = 0;

    public GameDiceTypeAdapter(List<DiceType> list) {
        super(R.layout.item_gametypebtn, list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull DiceType item) {
        helper.setText(R.id.title,item.title);
        if(selected == item.index){
            helper.setBackgroundRes(R.id.title,R.drawable.game_touzitype_selected);
        }else{
            helper.setBackgroundRes(R.id.title,R.drawable.game_touzitype_unselected);
        }
    }
}
