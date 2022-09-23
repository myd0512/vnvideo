package com.yunbao.live.game;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceGame;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameDiceAdapter extends BaseQuickAdapter<DiceGame, BaseViewHolder> {

    int numberOfitemLine = 4;
    int itemWidth;int itemHeight;
    int rateVW;

    public GameDiceAdapter(List<DiceGame> list, int numberOfitemLine, int widthPixels, int itemHeight, int rateVW) {
        super(R.layout.item_gamebtn, list);
        this.numberOfitemLine = numberOfitemLine;
        itemWidth = widthPixels;
        this.itemHeight = itemHeight;
        this.rateVW = rateVW;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull DiceGame item) {
        helper.setText(R.id.rateV,item.tag).setText(R.id.rate,item.rate);

        LinearLayout relativeLayout = helper.getView(R.id.rl_container);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.width = itemWidth;
        params.height = itemHeight;
        relativeLayout.setLayoutParams(params);



        FrameLayout relativeLayout2 = helper.getView(R.id.rl_container2);
        ViewGroup.LayoutParams params2 = relativeLayout2.getLayoutParams();
        params2.width = rateVW;
        params2.height = rateVW;;
        relativeLayout2.setLayoutParams(params2);


        if(item.select){
            helper.setBackgroundRes(R.id.rateV,R.drawable.game_touzi_select);
            helper.setTextColor(R.id.rateV, Color.WHITE);
        }else{
            helper.setBackgroundRes(R.id.rateV,R.drawable.game_touzi);
            helper.setTextColor(R.id.rateV,Color.BLACK);
        }
//        TextView rateV = helper.getView(R.id.rateV);
//        ViewGroup.LayoutParams params2 = rateV.getLayoutParams();
//        params2.width = itemWidth*2/3;
//        params2.height = itemWidth*2/3;
//        rateV.setLayoutParams(params2);
    }
}
