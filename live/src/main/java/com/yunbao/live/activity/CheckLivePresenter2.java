package com.yunbao.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.LogUtil;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveBean;
import com.yunbao.live.event.LiveCheckDelegate;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckLivePresenter2 {

    private Context mContext;
    private LiveBean mLiveBean;//选中的直播间信息
    private String mKey;
    private int mPosition;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private int mLiveSdk;
    private HttpCallback mCheckLiveCallback;
    private LiveCheckDelegate delegate;

    public CheckLivePresenter2(Context context) {
        mContext = context;
    }


    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean, LiveCheckDelegate delegate) {
        watchLive(bean, "", 0,delegate);
    }


    /**
     * 观众 观看直播 todo 检查vip限制进入房间
     */
    public void watchLive(LiveBean bean, String key, int position, final LiveCheckDelegate delegate) {
        if(CommonAppConfig.ONLY_VIP && CommonAppConfig.getInstance().getUserBean().getVip().getType()==0){
            ToastUtil.show("成为VIP才能观看直播哦！");
            return;
        }
        this.delegate = delegate;
        mLiveBean = bean;
        mKey = key;
        mPosition = position;
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
//                            LogUtil.elong("mCheckLiveCallback--->",obj.toString());
                            boolean k3open = obj.getString("k3open").equals("1");
                            delegate.doThing(k3open ? LiveCheckDelegate.LiveWaitToastType.Showf3kOpen : LiveCheckDelegate.LiveWaitToastType.Showf3kclose);

                            String type_msg = obj.getString("type_msg");
                            JSONObject vip = JSON.parseObject(obj.getString("vip"));
//                            Log.i("apkinfo", "vip:"+obj.getString("vip"));
//                            Log.i("apkinfo", "isvipopen:"+String.valueOf(vip.getIntValue("is_vip_open")));
//                            Log.i("apkinfo", "isvipday:"+vip.getString("is_vip_day"));
//                            Log.i("apkinfo", "vip_desc:"+vip.getString("vip_desc"));
                            int vipOpen = vip.getIntValue("is_vip_open");
                            if(vipOpen==0){
                                //ToastUtil.show(vip.getString("vip_desc"));
                                if(Integer.valueOf(vip.getString("is_vip_day"))>0){
                                    DialogUitl.showSelectDialog(mContext, WordUtil.getString(R.string.dialog_tip), vip.getString("vip_desc"), "去分享", "去充值", new DialogUitl.SimpleCallback2(){

                                        @Override
                                        public void onCancelClick() {
                                            Log.i("apkinfo", "去分享");
                                            ThreeDistributActivity.forward(mContext, "邀请奖励", HtmlConfig.SHARE);
                                        }

                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            Log.i("apkinfo", "去充值");
                                            WebViewActivity.forward(mContext, HtmlConfig.SHOP);
                                        }
                                    });
                                }else {
                                    DialogUitl.showSelectDialog(mContext, "提示", vip.getString("vip_desc"), "取消", "去充值", new DialogUitl.SimpleCallback2(){

                                        @Override
                                        public void onCancelClick() {
                                            shoudFinishLive();
                                        }

                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            //Log.i("apkinfo", "去充值");
                                            WebViewActivity.forward(mContext, HtmlConfig.SHOP);
                                        }
                                    });
                                }
                                return;
                            }
                            mLiveType = obj.getIntValue("type");
                            mLiveTypeVal = obj.getIntValue("type_val");
                            mLiveTypeMsg = obj.getString("type_msg");
                            if (CommonAppConfig.LIVE_SDK_CHANGED) {
                                mLiveSdk = obj.getIntValue("live_sdk");
                            } else {
                                mLiveSdk = CommonAppConfig.LIVE_SDK_USED;
                            }
                            switch (mLiveType) {
                                case Constants.LIVE_TYPE_NORMAL:
                                    forwardNormalRoom();
                                    break;
                                case Constants.LIVE_TYPE_PWD:
                                    forwardPwdRoom();
                                    break;
                                case Constants.LIVE_TYPE_PAY:
                                case Constants.LIVE_TYPE_TIME:
                                    forwardPayRoom(type_msg);
                                    break;
                            }
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUitl.loadingDialog(mContext);
                }
            };
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }

    void shoudFinishLive(){
        if(delegate != null){
            delegate.doThing(LiveCheckDelegate.LiveWaitToastType.Finish);
        }
    }

    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        forwardLiveAudienceActivity();
    }

    /**
     * 前往密码房间
     */
    private void forwardPwdRoom() {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_input_password), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(WordUtil.getString(R.string.live_input_password));
                    return;
                }
                String password = MD5Util.getMD5(content);
                if (mLiveTypeMsg.equalsIgnoreCase(password)) {
                    dialog.dismiss();
                    forwardLiveAudienceActivity();
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.live_password_error));
                }
            }
        });
    }

    /**
     * 前往付费房间
     */
    private void forwardPayRoom(String mLiveType) {

        delegate.saveThing(mLiveTypeMsg);
        roomCharge(mLiveType);
    }


    public void roomCharge(final String mLiveTypemsg) {

        LiveHttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
//                forwardLiveAudienceActivity();
//                    String liveCostMsg = "";
//                    if(mLiveType == Constants.LIVE_TYPE_TIME){
//                        liveCostMsg = "主播开启了付费直播，**钻/分钟是否付费进入";
//                    }else{
//                        liveCostMsg = "主播开启了付费直播，**钻/场是否付费进入";
//                    }
                    forwardLiveAudienceActivityWithTimeCount("",true,mLiveTypemsg);
                } else {
//                ToastUtil.show(msg);
                    forwardLiveAudienceActivityWithTimeCount(msg,false,null);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }



    public void cancel() {
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 直播间
     */
    private void forwardLiveAudienceActivity() {
        //LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk);
        if(delegate != null){
            delegate.doThing(LiveCheckDelegate.LiveWaitToastType.Noraml);
        }
    }

    /**
     * 跳转到直播间 预览10秒
     */
    private void forwardLiveAudienceActivityWithTimeCount(String msg,boolean isMoneyEnough,String liveCostMsg) {

        //isMoneyEnough 已经有钻石但是还是提示1
        if(isMoneyEnough){
            delegate.showCost(liveCostMsg);
            return;
        }

        SharedPreferences sp = mContext.getSharedPreferences("TimeCount",Context.MODE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String key = sdf.format(date);
        String stringValue = sp.getString(key,null);

        JSONObject obj;
        if(stringValue == null){
            obj = new JSONObject();
        }else{
            try{
                obj = JSON.parseObject(stringValue);
            }catch (Exception e){
                obj = new JSONObject();
            }
        }

        long time = date.getTime();

        String ukey = mLiveBean.getUid()+mLiveBean.getStream();


        if(obj.get(ukey) != null){
            if(isMoneyEnough){
                delegate.showCost(liveCostMsg);
            }else{
                delegate.doThing(LiveCheckDelegate.LiveWaitToastType.ShouldToCharge);
            }

            //ToastUtil.show(msg);
            return;
        }else{
            obj.put(ukey,time);
            stringValue = obj.toString();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key,stringValue);
            editor.commit();
        }
        if(delegate != null){
            if(isMoneyEnough){
                delegate.showCost(liveCostMsg);
            }else{
                delegate.doThing(LiveCheckDelegate.LiveWaitToastType.StartCount);
            }
        }
        //Log.e("--->","startCount"+time);
//        LiveAudienceActivity.forwardWithTimeCount(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk,time);
    }
}
