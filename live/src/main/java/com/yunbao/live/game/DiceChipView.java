package com.yunbao.live.game;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceChip;

import java.util.List;

public class DiceChipView extends AbsDialogFragment{

    @Override
    protected int getLayoutId() {
        return R.layout.game_touzi_chip;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    List<DiceChip> chiplist;
    RecyclerView list;
    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = findViewById(R.id.list);
        final GameChipAdapter adapter = new GameChipAdapter(chiplist);
        list.setLayoutManager(new GridLayoutManager(mContext,4));
        list.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                if(adapter.selected > 0){
                    adapter.notifyItemChanged(adapter.selected);
                }
                adapter.notifyItemChanged(i);
                adapter.selected = i;

            }
        });

        Button cfmBtn = findViewById(R.id.btn);
        cfmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(adapter.selected);
                dismissAllowingStateLoss();
            }
        });

    }

    OnItemClickListener onItemClickListener;
//
//    View.OnClickListener btnClickListener;
//


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
//
//    public void setBtnClickListener(View.OnClickListener btnClickListener) {
//        this.btnClickListener = btnClickListener;
//    }

    public interface OnItemClickListener{
        public void onItemClick(int index);
    }


//    @Override
//    public void onClick(View view) {
//        int vid = view.getId();
////        if(vid == R.id.chip1){
////            onItemClickListener.onItemClick(this,0);
////        }else if(vid == R.id.chip2){
////            onItemClickListener.onItemClick(this,1);
////        }else if(vid == R.id.chip3){
////            onItemClickListener.onItemClick(this,2);
////        }else if(vid == R.id.chip4){
////            onItemClickListener.onItemClick(this,3);
////        }else if(vid == R.id.chip5){
////            onItemClickListener.onItemClick(this,4);
////        }else if(vid == R.id.chip6){
////            onItemClickListener.onItemClick(this,5);
////        }else if(vid == R.id.chip7){
////            onItemClickListener.onItemClick(this,6);
////        }else if(vid == R.id.chip8){
////            onItemClickListener.onItemClick(this,7);
////        }else
//            if(vid == R.id.btn){
//            onItemClickListener.onItemClick(this,-1);
//        }
//    }
}
