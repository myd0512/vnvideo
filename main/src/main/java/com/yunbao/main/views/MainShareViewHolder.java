package com.yunbao.main.views;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;

import java.net.URLDecoder;

public class MainShareViewHolder extends AbsMainViewHolder {

    public MainShareViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_share;
    }

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private ImageButton refreshbtn;

    @Override
    public void init() {
//        final String url = CommonAppConfig.HOST + "/Appapi/Agent/index" + "&uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken();
//        final String url

//        LogUtil.eN(url);
        LinearLayout rootView = (LinearLayout) findViewById(com.yunbao.common.R.id.rootView);
        mProgressBar = (ProgressBar) findViewById(com.yunbao.common.R.id.progressbar);

        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = DpUtil.dp2px(1);
        mWebView.setLayoutParams(params);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rootView.addView(mWebView);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith(Constants.COPY_PREFIX)) {
                    String content = url.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        try {
                            // 因为用的是 copy:// 这种形式，需要自行做一次URL解码
                            content = URLDecoder.decode(content, "UTF-8");
                        } catch (Exception e) { }
                        copy(content);
                    }
                } else {
                    WebViewActivity.forward(mContext,url);
                    //view.loadUrl(url);
                }
                return true;
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }



            // For Android >= 5.0
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public boolean onShowFileChooser(WebView webView,
//                                             ValueCallback<Uri[]> filePathCallback,
//                                             FileChooserParams fileChooserParams) {
//                mValueCallback2 = filePathCallback;
//                Intent intent = fileChooserParams.createIntent();
//                startActivityForResult(intent, CHOOSE_ANDROID_5);
//                return true;
//            }

        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        mWebView.loadUrl(url);

        refreshbtn = findViewById(R.id.refreshbtn);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mWebView.loadUrl(url);
                mWebView.reload();
//                LogUtil.elong("reload--->","reload");
//                mWebView.loadUrl("javascript:window.location.reload(true)");
            }
        });
        htmlstring = htmlTempString;
        htmlstring = htmlstring.replace("$agent_bg$",CommonAppConfig.HOST+"/static/appapi/images/agent/agent_bg.png");
        htmlstring = htmlstring.replace("$right_img$",CommonAppConfig.HOST+"/static/appapi/images/right.png");

        htmlstring = htmlstring.replace("$HOST$",CommonAppConfig.HOST);
        htmlstring = htmlstring.replace("$withdraw$",CommonAppConfig.HOST+"/Appapi/Agent/withdraw?"+ "uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken());
        MainHttpUtil.getShareCode(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                String qrcode = obj.getString("code");
                String href = obj.getString("href");
                String qr = obj.getString("qr");
                htmlstring = htmlstring.replace("$qr$",qr);
                htmlstring = htmlstring.replace("$qrcode$",qrcode);

                getagentdownsum();
//                loadUrl();
            }
        });
        loading = DialogUitl.loadingDialog(mContext);
        loading.show();

    }
    String htmlstring;
    Dialog loading;

    void getagentdownsum(){
        MainHttpUtil.getShare(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
//                loadUrl();
//                LogUtil.eN(info[0]);
                JSONObject obj = JSON.parseObject(info[0]);
                String qrcode = obj.getString("code");
                String agentdownsum = obj.getString("agentdownsum");
                String sharebalance = obj.getString("sharebalance");
                String fx_word = obj.getString("fx_word");
                String fx_url = obj.getString("fx_url");
                htmlstring = htmlstring.replace("$fx_url$",fx_url);
                htmlstring = htmlstring.replace("$fx_word$",fx_word);

                htmlstring = htmlstring.replace("$agentdownsum$",agentdownsum);
                htmlstring = htmlstring.replace("$sharebalance$",sharebalance);

                loadUrl();
            }
        });
    }

    private void loadUrl(){
        loading.dismiss();
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        mWebView.loadDataWithBaseURL(null, htmlstring, "text/html", "utf-8", null);
    }

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(com.yunbao.common.R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }
    /**
     * 跳转外部浏览器
     * @param webUrl 外部链接
     */
    private void openBrowser(String webUrl) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webUrl));
        mContext.startActivity(intent);
    }
    /**
     * 复制到剪贴板
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(com.yunbao.common.R.string.copy_success);
    }

    /**
     * 拨打电话
     */
    private void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        mContext.startActivity(intent);
    }

    private static String htmlTempString = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"referrer\" content=\"origin\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" />\n" +
            "    <meta content=\"telephone=no\" name=\"format-detection\" />\n" +
            "    <title>Phần thưởng lời mời</title>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <div class=\"home\">\n" +
            "        <div class=\"top_bg\">\n" +
            "            <img src=\"$agent_bg$\">\n" +
            "        </div>\n" +
            "        <div class=\"top_code\">\n" +
            "            <div class=\"mycode_title\">\n" +
            "                截屏保存二维码\n" +
            "            </div>\n" +
            "            <div style=\"display: flex;justify-content: center;\">\n" +
            "                <img class=\"qrcode\" src=\"$qr$\" />\n" +
            "            </div>\n" +
            "            <div class=\"mycode\">\n" +
            "                <span class=\"code\" style=\"font-size:0.2rem\">\n" +
            "                    $fx_word$ </span>\n" +
            "                <div class=\"copy\"  id=\"biao1\" onClick=\"copyUrl()\"  \n" +
            "                    data-code=\"$fx_word$\">\n" +
            "                    <a href=\"copy://$fx_word$\">点击复制去分享</a>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"bar\">\n" +
            "            <div>\n" +
            "                <span class=\"li_l\">分享收益</span>\n" +
            "                <span>$sharebalance$</span>\n" +
            "            </div>\n" +
            "            <div>\n" +
            "                <a class=\"agent_add\"\n" +
            "                    href=\"$withdraw$\">\n" +
            "                    <span class=\"li_r\">去提现</span>\n" +
            "                </a>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"bar\">\n" +
            "            <div>\n" +
            "                <span class=\"li_l\">我的邀请人数</span>\n" +
            "            </div>\n" +
            "            <div class=\"barright\">\n" +
            "                <span class=\"li_r2\">$agentdownsum$</span>\n" +
            "                <img class=\"arrow\" src=\"$right_img$\">\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"tips\">\n" +
            "            邀请须知：<br>\n" +
            "            每个用户都有自己的邀请码，只要您邀请的用户输入您的邀请码，对方充值时，您将获得一定的分成奖励 <br>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "<script>\n" +
            "            function copyUrl()\n" +
            "    {\n" +
            "            const Url2=document.getElementById(\"biao1\");\n" +
            "        Url2.select(); // 选择对象\n" +
            "        document.execCommand(\"Copy\"); // 执行浏览器复制命令\n" +
            "    }\n" +
            "    </script>\n" +
            "    <style>\n" +
            "    body{background:#fff;margin:0;color:#323232;}.home{background:#f5f5f5;}.home .top_bg img{width:100%;}.top_code{position:relative;margin:-55px 10px 30px 10px;padding:5px 10px 15px 10px;border-radius:14px;text-align:center;background:#fff;}.qrcode{height:128px;display:block;text-align:center;}.mycode{font-size:13px;font-weight:bold;}.copy{color:#969696;font-weight:normal;}.bar{padding:15px 15px;background:white;border-bottom:#ededed solid 1px;display:flex;justify-content:space-between;font-size:14px;}.li_r{font-weight:bold;color:#000000;}.li_r2{font-weight:bold;color:#FF6131;}.tips{background:#fff;padding:0.2rem 0.4rem;color:#9a9a9a;font-size:14px;}a{outline:none;background:none;text-decoration:none;}.barright{display:flex;align-items:center;}.arrow{margin-left:10px;width:12px;height:15px;}\n" +
            "    </style>\n" +
            "</body></html>";
}
