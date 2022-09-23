package com.yunbao.common.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by cxf on 2019/6/20.
 */

public class LogUtil {

    public static void print(File file, String content) {
        if (file == null || TextUtils.isEmpty(content)) {
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void elong(String tagName, String msg) {

        int strLength = msg.length();
        int start = 0;
        int end = 3000;
        for (int i = 0; i < 100; i++) {
            if (strLength > end) {
                Log.e(tagName, msg.substring(start, end));
                start = end;
                end = end + 3000;
            } else {
                Log.e(tagName, msg.substring(start, strLength));
                break;
            }
        }
    }

    public static void eN(String msg){
        elong("------->",msg);
    }
}
