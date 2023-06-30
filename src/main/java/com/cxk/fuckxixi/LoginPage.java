package com.cxk.fuckxixi;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxk.fuckxixi.MainActivity;
import com.cxk.fuckxixi.R;
import com.cxk.fuckxixi.databinding.LoginPageBinding;
import com.cxk.fuckxixi.user.Consts;
import com.cxk.fuckxixi.user.Dao;
import com.cxk.fuckxixi.user.HttpCallbackListener;
import com.cxk.fuckxixi.user.Json;
import com.cxk.fuckxixi.user.NetworkTools;
import com.cxk.fuckxixi.user.UserProfileUnit;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class LoginPage extends Activity {

    private LoginPageBinding binding;

    //登录api返回信息（失败原因）的存储
    //登录成功的话不允许给_cause赋值
    public static String _msg = "";

    public static String _cause = "";




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }
            //校验是否允许点开按钮
            @Override
            public void afterTextChanged(Editable s) {
                if(usernameEditText.getText()!=null&&passwordEditText.getText()!=null)
                {
                    if(usernameEditText.getText().toString()!=""&&passwordEditText.getText().toString()!="") {
                        binding.login.setEnabled(true);
                    }
                    else
                    {
                        binding.login.setEnabled(false);
                    }
                }
                else
                {
                    binding.login.setEnabled(false);
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        //点击登录按钮 录入用户信息
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("程序.[登录事件]","登录按钮被点击"+usernameEditText.getText().toString()+" "+passwordEditText.getText().toString());
                UserProfileUnit.setUsername(usernameEditText.getText().toString());
                UserProfileUnit.setPassword(passwordEditText.getText().toString());
                loadingProgressBar.setVisibility(View.VISIBLE);
                Login();
                finish();
            }
        });

        //设置暗黑模式下进行反色
        TextView loginTitle = findViewById(R.id.loginTitle);
        if (MainActivity.isDarkMode) {
            loginTitle.setTextColor(Color.WHITE);
        }
        else
        {
            loginTitle.setTextColor(Color.BLACK);
        }
    }

    UserProfileUnit user = new UserProfileUnit();

    public void Login()
    {
        //定义请求参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Consts._userName, user.getUsername());
        params.put(Consts._passWord, user.getPassword());
        params.put(Consts._clientId, Consts.ClientId);
        params.put(Consts._brandId, Consts.BrandId);
        try {
            //构造完整URL
            String completedURL = NetworkTools.getURLWithParams(Consts.GetTokenUrl, params);
            Consts.requestTokenUrl = completedURL;
            //发送请求
            NetworkTools.sendHttpRequest(completedURL, new HttpCallbackListener() {

                //由于java的特性 以下的对外赋值都是无效的
                @Override
                public void onFinish(String response) {
                    Looper.prepare();
                    Log.i("程序.[登录]登录结果",response);
                    JSONObject loginJo;
                    try {
                        loginJo = new JSONObject(response);

                    }
                    //一般是网络问题
                    catch (Exception e){
                        LoginPage._msg="无法连接服务器";
                        LoginPage._cause="错误代码：\n"+e+"\n\n请检查你的网络";
                        return;
                    }

                    Json json = new Json();
                    if(!json.isSuccessfullyLogin(loginJo))
                    {
                        LoginPage._msg="登录失败";
                        LoginPage._cause=json.GetTokenOrErrDetail(loginJo);
                    }
                    else
                    {
                        //设置昵称
                        try {
                            UserProfileUnit.setNick(loginJo.getJSONObject("data").getString("nickName"));
                            LoginPage._msg="登录成功";
                            LoginPage._cause="用户名为 "+UserProfileUnit.getNick();
                            //设置到账号密码
                            SharedPreferences data = getSharedPreferences("user",0);
                            SharedPreferences.Editor editor = data.edit();
                            editor.putString("username",UserProfileUnit.getUsername());
                            editor.putString("password",UserProfileUnit.getPassword());
                            editor.apply();
                        }
                        catch (Exception e)
                        {
                            LoginPage._msg="无法获取你的个人信息";
                            LoginPage._cause="请发送以下信息给开发者。\n错误代码"+e+"\n请求结果"+loginJo;
                        }
                    }

                    Log.i("程序.[登录]返回值",LoginPage._msg+" "+LoginPage._cause);
                    Toast.makeText(LoginPage.this, _msg+"\n"+_cause, Toast.LENGTH_LONG).show();
                    Looper.loop();
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





}