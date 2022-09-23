package com.yunbao.live.dialog;

import static java.lang.Math.max;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.LogUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceChip;
import com.yunbao.live.bean.DiceGame;
import com.yunbao.live.bean.DiceRecord;
import com.yunbao.live.bean.DiceType;
import com.yunbao.live.game.DiceCoreDelegate;
import com.yunbao.live.game.DiceGameDelegate;
import com.yunbao.live.game.GameDiceAdapter;
import com.yunbao.live.game.GameDiceRecordAdapter;
import com.yunbao.live.game.GameDiceTypeAdapter;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LiveGameDialogFragment extends AbsDialogFragment implements View.OnClickListener, DiceGameDelegate {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_touzigame;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    public List<DiceType> typelist;

    public DiceCoreDelegate core;

    RecyclerView myrecorelistView;
    RecyclerView recorelistView;


    View dice_rs;
    ImageView oneimg;
    ImageView twoimg;
    ImageView threeimg;
    TextView rs_1;
    TextView rs_2;
    TextView rs_3;

    boolean ready = false;

    Button record1;
    Button record2;

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
//        params.y = DpUtil.dp2px(30);
        window.setAttributes(params);
    }

    RecyclerView btnlist;
    GameDiceTypeAdapter adapter;
    RecyclerView contentlist;

    ImageButton selectchipBtn;
    TextView chipText;


    TextView yueeView;
    TextView chronometer;

    List<DiceRecord> records1;
    List<DiceRecord> records2;
    GameDiceRecordAdapter recordAdapter1;
    GameDiceRecordAdapter recordAdapter2;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setDataAfterActivityCreated() {
        dice_rs = findViewById(R.id.dice_rs);
        btnlist = findViewById(R.id.btnlist);
        record2 = findViewById(R.id.record2);
        record1 = findViewById(R.id.record1);

        record1.setOnClickListener(this);
        record2.setOnClickListener(this);

        btnlist.setLayoutManager(new GridLayoutManager(mContext,5));
        adapter = new GameDiceTypeAdapter(typelist);
        btnlist.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                adapter.selected = i;
                adapter.notifyDataSetChanged();
                setGameList(typelist.get(i).gamelist);
            }
        });
        contentlist = findViewById(R.id.contentlist);

        if(typelist.size() > 0)
            setGameList(typelist.get(0).gamelist);


        findViewById(R.id.cfm).setOnClickListener(this);

        selectchipBtn = findViewById(R.id.selectchip);
        selectchipBtn.setOnClickListener(this);
        chipText = findViewById(R.id.chiptext);

        yueeView = findViewById(R.id.yuee);//余额
        findViewById(R.id.refresh).setOnClickListener(this);
        findViewById(R.id.tocharge).setOnClickListener(this);

        oneimg = findViewById(R.id.oneimg);
        twoimg = findViewById(R.id.twoimg);
        threeimg = findViewById(R.id.threeimg);
        rs_1 = findViewById(R.id.rs_1);
        rs_2 = findViewById(R.id.rs_2);
        rs_3 = findViewById(R.id.rs_3);


        chronometer = findViewById(R.id.chronometer);

        myrecorelistView = findViewById(R.id.myrecorelist);
        recorelistView = findViewById(R.id.recorelist);

        records1 = new ArrayList<>();
        records2 = new ArrayList<>();

        myrecorelistView.setLayoutManager(new LinearLayoutManager(mContext));
        recordAdapter1 = new GameDiceRecordAdapter(records1);
        myrecorelistView.setAdapter(recordAdapter1);

        recorelistView.setLayoutManager(new LinearLayoutManager(mContext));
        recordAdapter2 = new GameDiceRecordAdapter(records2);
        recorelistView.setAdapter(recordAdapter2);

        ready = true;
    }

    public void setBalance(String balance){
        if(balance.length() > 6){
            Integer bb = Integer.parseInt(balance);

            yueeView.setText(bb/1000+"k");
        }else{
            yueeView.setText(balance);
        }

    }

    void setGameList(final List<DiceGame> list){

        int numberOfitemLine = list.size()/2;
        if(list.size()%2>0){
            numberOfitemLine += 1;
        }
        if(list.size() <= 4){
            numberOfitemLine = list.size();
        }
        if(numberOfitemLine < 4 && list.size() != 6){
            numberOfitemLine = 4;
        }

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        int itemHeight = list.size() <= 4 ? DpUtil.dp2px(120) : DpUtil.dp2px(80);

        int rateVW = Math.min(dm.widthPixels/numberOfitemLine ,DpUtil.dp2px(80));
        rateVW = rateVW - DpUtil.dp2px(18);

        if(numberOfitemLine == 4 && list.size() > 4 &&list.size() < 10){
            rateVW = rateVW - DpUtil.dp2px(10);

        }
        if(numberOfitemLine == 3){
            rateVW = rateVW - DpUtil.dp2px(10);
        }
        contentlist.setLayoutManager(new GridLayoutManager(mContext,numberOfitemLine));
        GameDiceAdapter gameDiceAdapter = new GameDiceAdapter(list,numberOfitemLine,dm.widthPixels/numberOfitemLine,itemHeight,rateVW);
        contentlist.setAdapter(gameDiceAdapter);

        gameDiceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                list.get(i).select = !list.get(i).select;
                baseQuickAdapter.notifyItemChanged(i);
            }
        });

        core.getCore().getBalance();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.cfm){
            core.getCore().checkPay(getSelectedType());
        }else if(id == R.id.selectchip){
            if(core != null){
                core.getCore().showChipView();
            }

        }else if(id == R.id.refresh){
            //刷新余额
            core.getCore().getBalance();
        }else if(id == R.id.tocharge){
            core.getCore().toRecharge();
        }else if(id == R.id.record1){
            setRecord1Close(myrecorelistView.getVisibility() == View.VISIBLE);
            setRecord2Close(true);
        }
        else if(id == R.id.record2){
            setRecord2Close(recorelistView.getVisibility() == View.VISIBLE);
            setRecord1Close(true);
        }
    }

    void setRecord1Close(boolean close){
        if(close){
            myrecorelistView.setVisibility(View.GONE);
            Drawable down = mContext.getDrawable(R.mipmap.arrow_down);
            down.setBounds(0,0,down.getMinimumWidth(),down.getMinimumHeight());
            record1.setCompoundDrawables(null,null,down,null);

        }else{
            getMyorder();
            myrecorelistView.setVisibility(View.VISIBLE);
            Drawable top = mContext.getDrawable(R.mipmap.arrow_up);
            top.setBounds(0,0,top.getMinimumWidth(),top.getMinimumHeight());
            record1.setCompoundDrawables(null,null,top,null);
        }
    }
    void setRecord2Close(boolean close){
        if(close){
            recorelistView.setVisibility(View.GONE);
            Drawable down = mContext.getDrawable(R.mipmap.arrow_down);
            down.setBounds(0,0,down.getMinimumWidth(),down.getMinimumHeight());
            record2.setCompoundDrawables(null,null,down,null);
        }else{
            getDrawlist();
            recorelistView.setVisibility(View.VISIBLE);
            Drawable top = mContext.getDrawable(R.mipmap.arrow_up);
            top.setBounds(0,0,top.getMinimumWidth(),top.getMinimumHeight());
            record2.setCompoundDrawables(null,null,top,null);
        }
    }

    @Override
    public void setChip(DiceChip chip) {
        if(mContext != null){

            Glide.with(mContext).load(chip.img).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                    selectchipBtn.setBackground(drawable);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable drawable) {

                }
            });
            chipText.setText(chip.text);
        }
    }

    List<DiceGame> getSelectedType() {
        if(typelist.size() <= adapter.selected){
            return null;
        }

        List<DiceGame> param = new ArrayList<>();

        for(DiceType diceType : typelist){
            for(DiceGame diceGame:diceType.gamelist){
                if(diceGame.select){
                    param.add(diceGame);
                }
            }
        }

        return param;

    }



    @Override
    public void setTimeString(String time) {
        if(chronometer != null)
            chronometer.setText(time);
    }

    Map<String,Integer> rsimg = new HashMap<String,Integer>(){{
        put("1",R.mipmap.touzi_1);
        put("2",R.mipmap.touzi_2);
        put("3",R.mipmap.touzi_3);
        put("4",R.mipmap.touzi_4);
        put("5",R.mipmap.touzi_5);
        put("6",R.mipmap.touzi_6);
    }};
    Drawable[] mDrawables;
    @Override
    public void setLastResult(final String one, final String two, final String three, String sum, String ds, String dx) {
        if(!ready){
            return;
        }
//        if(rsimg.get(one) != null){
//            oneimg.setBackgroundResource(rsimg.get(one));
//        }
//        if(rsimg.get(two) != null){
//            twoimg.setBackgroundResource(rsimg.get(two));
//        }
//        if(rsimg.get(three) != null){
//            threeimg.setBackgroundResource(rsimg.get(three));
//        }

        rs_1.setText(sum);


        if(ds.equals("dai")){
            rs_2.setText("单");
        }else if(ds.equals("shuang")){
            rs_2.setText("双");
        }else{
            rs_2.setText(ds);
        }

        if(dx.equals("da")){
            rs_3.setText("大");
        }else if(dx.equals("xiao")){
            rs_3.setText("小");
        }else{
            rs_3.setText(dx);
        }


        dice_rs.setVisibility(View.VISIBLE);

        final ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 5);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
//                LogUtil.eN(v+" AnimatedValue");
                if(v < 5){
                    oneimg.setBackground(getmDrawables()[new Random().nextInt(5)]);
                    twoimg.setBackground(getmDrawables()[new Random().nextInt(5)]);
                    threeimg.setBackground(getmDrawables()[new Random().nextInt(5)]);
                }else{
                    if(rsimg.get(one) != null){
                        oneimg.setBackgroundResource(rsimg.get(one));

//                        LogUtil.eN(one+" -"+two+"-"+three);
                    }
                    if(rsimg.get(two) != null){
                        twoimg.setBackgroundResource(rsimg.get(two));
                    }
                    if(rsimg.get(three) != null){
                        threeimg.setBackgroundResource(rsimg.get(three));
                    }
                }
            }

        });

        mAnimator.setDuration(1500);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
    }

    Drawable[] getmDrawables(){
        if(mDrawables == null){
            mDrawables = new Drawable[]{
                    ContextCompat.getDrawable(mContext,R.mipmap.touzi_2),ContextCompat.getDrawable(mContext,R.mipmap.touzi_6),
                    ContextCompat.getDrawable(mContext,R.mipmap.touzi_3),ContextCompat.getDrawable(mContext,R.mipmap.touzi_5),
                    ContextCompat.getDrawable(mContext,R.mipmap.touzi_4),ContextCompat.getDrawable(mContext,R.mipmap.touzi_1)
            };
        }
        return mDrawables;
    }
    public void dismissSelf(){
        dismiss();
    }

    void getDrawlist(){
        LiveHttpUtil.fastk3Drawlist(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                try{
                    records2.clear();
                    records2.addAll(JSONObject.parseArray(info[0], DiceRecord.class));
//                    LogUtil.eN("records2="+records2.size());
                    recordAdapter2.notifyDataSetChanged();
                }catch (Exception e){
                    ToastUtil.show(e.getMessage());
                }
//                LogUtil.elong("getDrawlist",info[0]);
//                {"batch":"202204140100386","dated":"20220414","drawno":"0","etime":"1649943481","id":"555","one":"0","opentime":"0","subtype":"0","sum":"0","three":"0","two":"0"}

            }
        });
    }

    void getMyorder(){
        LiveHttpUtil.fastk3Myorder(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
//                LogUtil.elong("getMyorder",info[0]);
                try{
                    records1.clear();
                    records1.addAll(JSONObject.parseArray(info[0], DiceRecord.class));
                    recordAdapter1.notifyDataSetChanged();
                }catch (Exception e){
                    ToastUtil.show(e.getMessage());
                }

            }
        });
    }
}
