package com.cxk.fuckxixi.user;

import android.util.Log;

import org.json.JSONObject;

public class Json {
    /**
     * 检查是否成功登录
     * @param loginJo json
     * @return tf
     */
    public boolean isSuccessfullyLogin(JSONObject loginJo)
    {
        try {
            return loginJo.getInt("errcode") == 0;
        }
        catch (Exception e)
        {
            Log.e("程序.[解析JSON]无法解析",e.toString());
            return false;
        }
    }

    /**
     * 获取令牌或者错误详情
     * @param json json
     * @return 令牌或者错误详情
     */
    public String GetTokenOrErrDetail(JSONObject json)
    {
        try {
            if(json.has("accessToken"))
            {
                return json.getString("accessToken");
            }
            else
            {
                return json.getString("msg");
            }
        }
        catch(Exception e)
        {
            return "无法解析返回值。"+e;
        }
    }


}
