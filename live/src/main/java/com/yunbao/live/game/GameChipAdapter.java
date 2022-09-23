package com.yunbao.live.game;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceChip;
import com.yunbao.live.bean.DiceType;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameChipAdapter extends BaseQuickAdapter<DiceChip, BaseViewHolder> {

    public int selected = -1;

    public GameChipAdapter(List<DiceChip> list) {
        super(R.layout.item_gamechip, list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull DiceChip item) {
        if(helper.getLayoutPosition() == selected){
            helper.setBackgroundRes(R.id.content,R.drawable.game_dicechip_selected);
        }else{
            helper.setBackgroundRes(R.id.content,R.color.white);
        }
        ImageView img = helper.getView(R.id.chip);
        Glide.with(mContext).load(item.img).into(img);


        helper.setText(R.id.text,item.text);
    }
}
