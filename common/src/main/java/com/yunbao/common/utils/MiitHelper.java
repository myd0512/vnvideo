package com.yunbao.common.utils;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdk;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;
import com.yunbao.common.CommonAppConfig;

/**
 *
 */
public class MiitHelper implements IIdentifierListener {

    private AppIdsUpdater _listener;

    public MiitHelper(Context cxt, AppIdsUpdater callback) {
        _listener = callback;
        getDeviceIds(cxt);
    }


    public void getDeviceIds(Context cxt) {
        long timeb = System.currentTimeMillis();
        int nres = CallFromReflect(cxt);
        //        int nres=DirectCall(cxt);
        long timee = System.currentTimeMillis();
        long offset = timee - timeb;
        if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {//1008612
            CommonAppConfig.getInstance().setIsSupportOaid(false);
        } else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {//1008613
            CommonAppConfig.getInstance().setIsSupportOaid(false);
        } else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {//1008611
            CommonAppConfig.getInstance().setIsSupportOaid(false);
        } else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) {//1008614
            CommonAppConfig.getInstance().setIsSupportOaid(false);
        } else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) {//1008615
            CommonAppConfig.getInstance().setIsSupportOaid(false);
        }
        Log.d(getClass().getSimpleName(), "return value: " + String.valueOf(nres));

    }

    /*
     *
     * */
    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }

    /*
     *
     * */
    private int DirectCall(Context cxt) {
        MdidSdk sdk = new MdidSdk();
        return sdk.InitSdk(cxt, this);
    }

    @Override
    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
        if (_supplier == null) {
            return;
        }
       /* String oaid=_supplier.getOaid();
        String vaid=_supplier.getVAID();
        String aaid=_supplier.getAAID();
        String udid=_supplier.getUDID();
        StringBuilder builder=new StringBuilder();
        builder.append("support: ").append(isSupport?"true":"false").append("\n");
        builder.append("UDID: ").append(udid).append("\n");
        builder.append("OAID: ").append(oaid).append("\n");
        builder.append("VAID: ").append(vaid).append("\n");
        builder.append("AAID: ").append(aaid).append("\n");
        String idstext=builder.toString();*/
        String oaid = _supplier.getOAID();
        Log.d(getClass().getSimpleName(), "OnSupport: " + String.valueOf(oaid));
        _supplier.shutDown();
        if (_listener != null) {
            _listener.OnIdsAvalid(oaid);
        }
    }

    public interface AppIdsUpdater {
        void OnIdsAvalid(String ids);
    }

}
