package com.cxk.fuckxixi.user;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;

import com.cxk.fuckxixi.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Dao extends Thread {

    private JSONObject json;

    //异常信息
    Exception exception;

    @Override
    public void run()
    {
        Looper.prepare();
        MainActivity.profileErrDetail =null;
        json = null;
        Log.i("程序.[写入个人资料]","线程已启动！请求地址："+Consts.requestTokenUrl);
        //个人信息的重置
        //MainActivity.ProfileErrDetail = "班级："+UserProfileUnit.getClassname()+"\n学校："+UserProfileUnit.getSchool()+"\n账号："+UserProfileUnit.getUsername()+"\n访问Token："+UserProfileUnit.getToken();

        //获取基本信息
        json = GetJson(Consts.requestTokenUrl);
        if(json==null)
        {
            MainActivity.profileErrDetail = "无法获取登录信息。请重试或者检查更新。\n"+exception.toString();
            return;
        }

        //获取令牌并写入昵称
        try {
            UserProfileUnit.setToken(json.getJSONObject("data").getString("accessToken"));
            UserProfileUnit.setNick(json.getJSONObject("data").getString("nickName"));
        }
        catch (Exception e)
        {
            exception =  e;
            MainActivity.profileErrDetail = "连接服务器成功了，但是无法解析登录信息。请重试或者检查更新。\n"+exception.toString()+"\n"+json.toString();
            return;
        }

        //获取资料json
        json = GetJson(Consts.GetProfileUrl + UserProfileUnit.getToken()+"&userRole=1&brandId="+Consts.BrandId);
        if(json==null)
        {
            MainActivity.profileErrDetail = "无法获取你的资料。请重试或者检查更新。\n"+exception.toString();
            return;
        }

        //写入其它资料
        try {
            UserProfileUnit.setClassname(json.getJSONArray("data").getJSONObject(0).getString("className"));
            UserProfileUnit.setSchool(json.getJSONArray("data").getJSONObject(0).getString("SchoolName"));
        }
        catch (Exception e)
        {
            exception =  e;
            MainActivity.profileErrDetail = "连接服务器成功了，但是无法解析资料。请重试或者检查更新。\n"+exception.toString()+"\n"+json.toString()+"\n"+Consts.GetProfileUrl+UserProfileUnit.getToken()+"&userRole=1&brandId="+Consts.BrandId;
            return;
        }

        Looper.loop();
    }

    public JSONObject GetJson(String Url)
    {
        String jsonStr;
        InputStreamReader isr=null;
        try {
            URL urlObj = new URL(Url);
            HttpsURLConnection uc = (HttpsURLConnection) urlObj.openConnection();
            isr =new InputStreamReader(uc.getInputStream(), Xml.Encoding.UTF_8.name());
            BufferedReader reader =new BufferedReader(isr);//缓冲

            jsonStr = reader.readLine();
            Log.i("程序.[写入个人资料]服务器请求结果",jsonStr);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("程序.[写入个人资料]无法连接服务器",e.toString());
            exception = e;
            return null;
        }
        finally{
            try{
                if(null!=isr)isr.close();}
            catch(IOException e){
                e.printStackTrace();
                Log.e("程序.[写入个人资料]无法关闭请求",e.toString());
                exception = e;
                return null;
            }
        }

        try {
            return new JSONObject(jsonStr);
        }
        catch (Exception e)
        {
            exception = e;
            Log.e("程序.[写入个人资料]无法转换",e.toString());
            return null;
        }
    }


}
