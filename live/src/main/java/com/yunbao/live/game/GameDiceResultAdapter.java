package com.yunbao.live.game;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceGame;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameDiceResultAdapter extends BaseQuickAdapter<DiceGame, BaseViewHolder> {


    public float money;
    public GameDiceResultAdapter(List<DiceGame> list) {
        super(R.layout.item_dice_rs_list, list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull DiceGame item) {
        helper.setText(R.id.title1,"一分快三-"+item.supTitle+"|"+item.tag)
                .setText(R.id.title2,item.rateV)
                .setText(R.id.title3,String.valueOf(money));
//        helper.setOnItemClickListener()

    }
}
