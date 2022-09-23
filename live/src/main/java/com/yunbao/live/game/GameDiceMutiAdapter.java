package com.yunbao.live.game;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.button.MaterialButton;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceGame;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameDiceMutiAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {


    public int selectmultiple;
    public GameDiceMutiAdapter(List<Integer> list,int selectmulti) {
        super(R.layout.item_dice_muti, list);
        this.selectmultiple = selectmulti;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull Integer item) {
//        helper.setText(R.id.title,item);
        MaterialButton btn = helper.getView(R.id.title);
        btn.setText(String.valueOf(item));
        if(helper.getLayoutPosition() == selectmultiple){
            Drawable background = btn.getBackground();
            background.setTint(Color.RED);
            btn.setBackgroundDrawable(background);
            btn.setTextColor(Color.WHITE);
        }else{
            Drawable background = btn.getBackground();
            background.setTint(Color.WHITE);
            btn.setBackgroundDrawable(background);
            btn.setTextColor(Color.BLACK);
        }
        helper.addOnClickListener(R.id.title);
    }
}
