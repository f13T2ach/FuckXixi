package com.cxk.fuckxixi.user;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * 发送http的工具类
 */

public class NetworkTools {

    /**
     * 发送请求
     * @param address 请求的链接
     */

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    URL reqURL = new URL(address); //创建URL对象
                    HttpsURLConnection httpsConn = (HttpsURLConnection) reqURL.openConnection();


                    //取得该连接的输入流，以读取响应内容
                    InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());

                    //读取服务器的响应内容并显示
                    if(insr!=null) {
                        int respInt = insr.read();
                        while (respInt != -1) {
                            result += (char) respInt;
                            respInt = insr.read();
                        }
                    }

                    if (listener!=null)
                    {
                        listener.onFinish(result);
                    }
                }
                catch(Exception e)
                {
                    Log.e("程序.[Https请求]无法请求",e.toString());
                    listener.onError(e);
                }
            }
        }).start();
    }

    //组装出带参数的完整URL
    public static String getURLWithParams(String address,HashMap<String,String> params) throws UnsupportedEncodingException {
        //设置编码

        final String encode = "UTF-8";

        StringBuilder url = new StringBuilder(address);
        url.append("?");
        //将map中的key，value构造进入URL中
        for(Map.Entry<String, String> entry:params.entrySet())
        {
            url.append(entry.getKey()).append("=");
            url.append(URLEncoder.encode(entry.getValue(), encode));
            url.append("&");
        }
        //删掉最后一个&
        url.deleteCharAt(url.length() - 1);
        Log.i("程序.[组成链接]结果",url.toString());
        return url.toString();
    }

    //判断当前网络是否可用
    public static boolean isNetworkAvailable(){
        //这里检查网络，后续再添加

        return true;
    }
}
