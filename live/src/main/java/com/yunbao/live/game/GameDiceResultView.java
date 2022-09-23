package com.yunbao.live.game;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.button.MaterialButton;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.LogUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceChip;
import com.yunbao.live.bean.DiceGame;

import java.util.List;

public class GameDiceResultView extends AbsDialogFragment implements View.OnClickListener,DiceGameDelegate{

    @Override
    protected int getLayoutId() {
        return R.layout.game_touzi_result;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    public DiceCoreDelegate core;

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    RecyclerView listView;
    RecyclerView listView2;
    List<DiceGame> datalist;
    GameDiceResultAdapter adapter;

    TextView yueV;
    TextView allnumV;
    TextView allMoneyV;
    TextView timeV;
    MaterialButton confirmBtn;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        yueV = findViewById(R.id.yue);
        allMoneyV = findViewById(R.id.allmoney);
        allnumV = findViewById(R.id.heji);
        timeV = findViewById(R.id.timev);

        listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new GameDiceResultAdapter(datalist);
        listView.setAdapter(adapter);
        adapter.money = core.getCore().multiplelist.get(core.getCore().selectmulti) * core.getCore().selectedChip.value;

//        adapter.addChildClickViewIds(R.id.updatebtn);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                if(view.getId() == R.id.delete){
                    datalist.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        allnumV.setText(String.valueOf(datalist.size()));

        listView2 = findViewById(R.id.list2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        listView2.setLayoutManager(layoutManager);

        GameDiceMutiAdapter mutiAdapter = new GameDiceMutiAdapter(core.getCore().multiplelist,core.getCore().selectmulti);
        listView2.setAdapter(mutiAdapter);
        mutiAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                core.getCore().selectmulti = i;
                LogUtil.eN(i+",,,,");
                ((GameDiceMutiAdapter)baseQuickAdapter).selectmultiple = i;
                baseQuickAdapter.notifyDataSetChanged();

                adapter.money = core.getCore().multiplelist.get(core.getCore().selectmulti) * core.getCore().selectedChip.value;
                adapter.notifyDataSetChanged();

//                LogUtil.eN(adapter.money+",,,adapter.money");
            }
        });

        confirmBtn = findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                core.getCore().startPay(datalist);
            }
        });


        core.getCore().getBalance();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }

    OnCfmClickListener btnClickListener;
    public void setBtnClickListener(OnCfmClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public interface OnCfmClickListener{
        public void onConform(List<DiceGame> datalist);
    }

    public void setChip(DiceChip chip){}
    public void setDataAfterActivityCreated(){}
    public void setBalance(String coin){
        yueV.setText(coin);
    }
    public void setTimeString(String time){
        timeV.setText(time);
    }
    public void setLastResult(String one,String two,String three,String sum,String ds,String dx){}
    public void dismissSelf(){
        dismiss();
    }
}
