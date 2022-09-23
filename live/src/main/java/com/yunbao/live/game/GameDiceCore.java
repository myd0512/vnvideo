package com.yunbao.live.game;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ksy.statlibrary.util.Base64Code;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.Data;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.LogUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.DiceChip;
import com.yunbao.live.bean.DiceGame;
import com.yunbao.live.bean.DiceRecord;
import com.yunbao.live.bean.DiceType;
import com.yunbao.live.dialog.LiveGameDialogFragment;
import com.yunbao.live.http.LiveHttpUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameDiceCore implements DiceCoreDelegate, AbsDialogFragment.LifeCycleListener {

    @Override
    public GameDiceCore getCore() {
        return this;
    }

    private long askDrawtime = -1;
    private boolean isRquestDraw = false;

    private GameDiceCore(Context context){
        super();
        mContext = context;

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(askDrawtime > -1){
                    askDrawtime -= 1;
                    if(askDrawtime > 0){
                        if(!isRquestDraw){
                            setCountTimeString("00:"+ (askDrawtime > 9 ? askDrawtime : "0"+askDrawtime));
                        }
                    }else if(askDrawtime == 0){
                        setCountTimeString("00:00");
                        getbatch();
                    }else{
                        setCountTimeString("00:00");
                    }
                }

                handler.sendEmptyMessageDelayed(100,1000);
            }
        };
        handler.sendEmptyMessageDelayed(100,1000);

    }

    public static GameDiceCore getCore(Context context,String stream){
        GameDiceCore core = new GameDiceCore(context);
        LogUtil.eN(stream);
        core.stream = stream;
//        core.zbid = zbid;
        return core;
    }
    String stream;
//    String zbid;
    List<DiceType> typelist;
    List<DiceChip> chiplist;
    List<Integer> multiplelist;
    public int selectmulti;

    String batch;
    DiceGameDelegate delegate;
    DiceGameDelegate rsdelegate;
    Context mContext;
    FragmentManager ft;
//    LiveGameDialogFragment dialogFragment;
    DiceChip selectedChip;
    Long endTime;



    @SuppressLint("HandlerLeak")
    private Handler handler;


    public void openGameBox(final FragmentManager ft){
//        LogUtil.elong("->",CommonAppConfig.getInstance().getUid()+",="+CommonAppConfig.getInstance().getToken());

        this.ft = ft;
        if(typelist == null || typelist.size() == 0){
            LiveHttpUtil.getTouziTypeList(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
//                    LogUtil.elong("getTouziTypeList",info[0]);
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONArray jiang = obj.getJSONArray("jiang");
                    typelist = new ArrayList<>();
                    for(int i=0;i<jiang.size();i++){
                        JSONObject item = jiang.getJSONObject(i);
                        DiceType type = new DiceType();
                        type.index = i;
                        type.setData(item);
                        typelist.add(type);
                    }

                    JSONArray chip = obj.getJSONArray("chip");
                    chiplist = new ArrayList<>();
                    for(int i=0;i<chip.size();i++){
                        DiceChip type = new DiceChip(chip.getJSONObject(i));
                        chiplist.add(type);
                    }
                    if(chiplist.size() > 0){
                        selectedChip = chiplist.get(0);
                    }
                    creatLiveGameDialog();


                    JSONArray multiple = obj.getJSONArray("multiple");
                    multiplelist = new ArrayList<>();
                    for(int i=0;i<multiple.size();i++){
                        Integer type = multiple.getInteger(i);
                        multiplelist.add(type);
                    }
                    selectmulti = 0;
                }

                @Override
                public void onError() {
                    super.onError();
                    if (gameDiceCoreLister != null) {
                        gameDiceCoreLister.onDialogFragmentHide();
                    }
                }
            });
        }else{
            creatLiveGameDialog();
        }
    }

    private void creatLiveGameDialog(){
        LiveGameDialogFragment dialogFragment = new LiveGameDialogFragment();
        dialogFragment.typelist = typelist;
        dialogFragment.core = this;
        dialogFragment.setLifeCycleListener(this);
        this.delegate = dialogFragment;
        dialogFragment.show(ft, "LiveGameDialogFragment");
        if(chiplist.size() > 0){
            setSelectedChip(chiplist.get(0));
        }
    }

    public void setSelectedChip(DiceChip selectedChip) {
        this.selectedChip = selectedChip;
        if(delegate != null){
            delegate.setChip(selectedChip);
        }
    }

    public void showChipView(){
        DiceChipView chipView = new DiceChipView();
        chipView.chiplist = chiplist;
        chipView.setOnItemClickListener(new DiceChipView.OnItemClickListener() {
            @Override
            public void onItemClick(int var2) {
                setSelectedChip(chiplist.get(var2));
            }
        });
        chipView.show(ft, "TouziChipView");
    }

    public void checkPay(List<DiceGame> param){
        if(param == null || selectedChip == null){
            return;
        }

        if(param.size() == 0){
            return;
        }

        GameDiceResultView rsView = new GameDiceResultView();
        rsView.datalist = param;
        rsView.core = this;
        this.rsdelegate = rsView;
        rsView.show(ft, "TouziChipView");
    }

    Dialog loading;
    public void startPay(List<DiceGame> param){

        loading = DialogUitl.loadingDialog(mContext);
        loading.show();


        JSONArray params = new JSONArray();
        for(DiceGame diceGame:param) {
            JSONObject obj = new JSONObject();
            obj.put("money",selectedChip.value*multiplelist.get(selectmulti));
            obj.put("type",diceGame.supTag);
            obj.put("subtype",diceGame.tagV);
            obj.put("rate",diceGame.rateV);
            params.add(obj);
        }

//        String base = Base64Code.decode(params.toString()).toString();
        String base = params.toString();

        rsdelegate.dismissSelf();

//        LogUtil.eN(",params="+params+",Base64Code="+base);
        LiveHttpUtil.buyticket(stream, batch, base, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
//                LogUtil.eN(msg);

                if(code == 0){
                    DialogUitl.showSimpleDialog(mContext, mContext.getString(R.string.mall_216), new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
                }else{
                    DialogUitl.showSimpleDialog(mContext, msg, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
                }
                loading.dismiss();

                getBalance();


            }

            @Override
            public void onError() {
                super.onError();
                loading.dismiss();

                DialogUitl.showSimpleDialog(mContext, mContext.getString(R.string.mall_367), new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void getbatch(){
        isRquestDraw = true;
        LiveHttpUtil.fastk3Getbatch(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
//                LogUtil.elong("getbatch",info[0]);//{"batch":202204140100007,"dated":"20220414"}
                JSONObject obj = JSON.parseObject(info[0]);
                batch = obj.getString("batch");
                Long etime = obj.getLongValue("etime");
                setEndTime(etime);

//                ToastUtil.show(batch+","+etime+"。当前"+System.currentTimeMillis()/1000);
//                getDraw(batch);

//                if(!isRquestDraw){
                    String one = obj.getString("one");
                    String two = obj.getString("two");
                    String three = obj.getString("three");

                    String sum = obj.getString("sum");
                    String ds = obj.getString("ds");
                    String dx = obj.getString("dx");

                    setLastResult(one,two,three,sum,ds,dx);
//                }

            }

            @Override
            public void onError() {
                super.onError();
                askDrawtime = 7;
            }
        });
    }
//    public void getDraw(){
//        isRquestDraw = true;
//        LiveHttpUtil.fastk3Draw(batch,"",new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                LogUtil.elong("fastk3getDraw",info[0]);//{"batch":"202204140100034","dated":"20220414","drawno":"622","ds":"shuang","dx":"xiao","etime":"1649920321","id":"203","one":"6","opentime":"1649920321","subtype":"2","sum":"10","three":"2","two":"2","type":"pair"}
//                JSONObject obj = JSON.parseObject(info[0]);
//                batch = obj.getString("newbacth");
//                Long etime = obj.getLongValue("newetime");
//                setEndTime(etime);
//
//                String one = obj.getString("one");
//                String two = obj.getString("two");
//                String three = obj.getString("three");
//
//                String sum = obj.getString("sum");
//                String ds = obj.getString("ds");
//                String dx = obj.getString("dx");
//
//                setLastResult(one,two,three,sum,ds,dx);
//            }
//
//            @Override
//            public void onError() {
//                super.onError();
//                askDrawtime = 5;
//            }
//        });
//
//
//    }

    void setLastResult(String one,String two,String three,String sum,String ds,String dx){
        if(delegate != null){
            delegate.setLastResult(one,two,three,sum,ds,dx);
        }

    }

    void setCountTimeString(String time){
        if(delegate != null)
            delegate.setTimeString(time);
        if(rsdelegate != null)
            rsdelegate.setTimeString(time);
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;

//        if((new Date()).getTime()/1000 == Instant.now().getEpochSecond()){
//            ToastUtil.show("==");
//        }else{
//            ToastUtil.show("!=");
//        }
        long cur1 = (new Date()).getTime()/1000;
        final long l = endTime-cur1;

//        LogUtil.eN(l+"，，，，");
        if(l <= 0){
            setCountTimeString("00:00");
            askDrawtime = 5;
            return;
        }
        isRquestDraw = false;
        askDrawtime = l;
    }
    public Long getEndTime(){
        return this.endTime;
    }

    public void getBalance(){
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
//                LogUtil.elong("getBalance----->",obj.toString());
                String balance = obj.getString("balance");
                delegate.setBalance(balance);

                if(rsdelegate != null){
                    rsdelegate.setBalance(balance);
                }
            }
        });
    }
    public void toRecharge(){
        if(rsdelegate != null){
            rsdelegate.dismissSelf();
        }
        if(delegate != null){
            delegate.dismissSelf();
        }
//        RouteUtil.forwardMyCoin(mContext);

        WebViewActivity.forward(mContext, HtmlConfig.SHOP);
    }

    @Override
    public void onDialogFragmentShow(AbsDialogFragment fragment) {
        delegate.setDataAfterActivityCreated();
        if(selectedChip != null){
            setSelectedChip(selectedChip);
        }
    }

    public void setGameDiceCoreLister(GameDiceCoreLister gameDiceCoreLister) {
        this.gameDiceCoreLister = gameDiceCoreLister;
    }

    @Override
    public void onDialogFragmentHide(AbsDialogFragment fragment) {
        if (gameDiceCoreLister != null) {
            gameDiceCoreLister.onDialogFragmentHide();
        }
    }
    public GameDiceCoreLister gameDiceCoreLister;

    public interface GameDiceCoreLister{
        public void onDialogFragmentHide();
    }

    public void release(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }



}
