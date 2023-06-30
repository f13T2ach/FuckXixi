package com.cxk.fuckxixi.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.cxk.fuckxixi.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 更新类 只能通过线程调用
 */
public class Networks extends Thread {

    private Context context; //上下文
    public  Networks(Context context){this.context = context;}

    @Override
    public void run(){
        Looper.prepare();
        GetUpdateInfo(context);
        int flag = IsUpdate(context);
        MainActivity.isUpdate = flag;
        Log.i("程序.[更新线程]是否更新", "返回值" + flag);
        interrupt();
        Looper.loop();
    }


    /**
     * 确定是否有更新
    * @return: 0：没有更新 1：有更新 2：无法连接服务器 3：无法断开服务器 4：无法比较
    *  */
    public int IsUpdate(Context context)
    {
        //获取更新信息
        String info;
        InputStreamReader isr=null;
        try {
            URL urlObj = new URL(MainActivity.updateUrl);
            URLConnection uc = urlObj.openConnection();
            /*
             * io 流
             * 从服务器下载源码到本地
             * */
            isr =new InputStreamReader(uc.getInputStream(), Xml.Encoding.UTF_8.name());
            BufferedReader reader =new BufferedReader(isr);//缓冲

            info = reader.readLine();
            Log.i("程序.[查看是否有更新]服务器请求结果",info);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("程序.[查看是否有更新]无法连接服务器",e.toString());

            return 2;
        }
        finally{
            try{
                if(null!=isr)isr.close();}
            catch(IOException e){
                e.printStackTrace();
                Log.e("程序.[查看是否有更新]无法关闭请求",e.toString());
                return 3;
            }
        }

        //版本号验证
        int code = MainActivity.vid; //版本号
        try {
            MainActivity.downloadUrl = info.substring(info.indexOf("|")+1); //给下载链接赋值
            if (Integer.parseInt(info.substring(0, info.indexOf("|"))) > code) {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        catch (Exception e)
        {
            Log.e("程序.[查看是否有更新]无法比较版本号，错误代码：",e.toString());
            Log.e("程序.[查看是否有更新]无法比较版本号","info: "+info+" code: "+code);
            return 4;
        }

    }

    /**
     * 获取更新
     * 参数直接传递到MainActivity
     *  */
    public void GetUpdateInfo(Context context)
    {
        //获取更新信息
        String info;
        InputStreamReader isr=null;
        try {
            URL urlObj = new URL(MainActivity.updateInfo);
            URLConnection uc = urlObj.openConnection();
            /*
             * io 流
             * 从服务器下载源码到本地
             * */
            isr =new InputStreamReader(uc.getInputStream(), Xml.Encoding.UTF_8.name());
            BufferedReader reader =new BufferedReader(isr);//缓冲

            info = reader.readLine()+"\n"; //所有行
            String thisLine = null; //当前行
            //逐行读取
            while (Boolean.TRUE) {
                thisLine = reader.readLine();
                if(thisLine!=null) {
                    info += thisLine + "\n";
                }
                else
                {
                    break;
                }
            }

            Log.i("程序.[更新信息检索]服务器请求结果",info);
            MainActivity.updateInfoUtil = info;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("程序.[更新信息检索]无法连接服务器",e.toString());
            MainActivity.updateInfoUtil = null;
        }
        finally{
            try{
                if(null!=isr)isr.close();}
            catch(IOException e){
                e.printStackTrace();
                Log.e("程序.[更新信息检索]无法关闭请求",e.toString());
                MainActivity.updateInfoUtil = null;
            }
        }


    }
}
