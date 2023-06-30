/*
这些代码就是依托答辩
 */
package com.cxk.fuckxixi;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cxk.fuckxixi.databinding.ActivityMainBinding;
import com.cxk.fuckxixi.handle.ExceptionHandle;
import com.cxk.fuckxixi.user.Consts;
import com.cxk.fuckxixi.user.Dao;
import com.cxk.fuckxixi.user.UserProfileUnit;
import com.cxk.fuckxixi.util.Key;
import com.cxk.fuckxixi.util.Networks;

/**
 * @author 你龙哥
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    //应用程序版本 不要在这里修改版本号 要在app\build.gradle里修改
    public static int vid = 0;
    //权限列表
    private String[] strings=new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    //更新地址
    public static  String updateUrl = "https://gitee.com/Doxfer/invisible/raw/master/XiXiUp/UpdateLink.php";
    //更新公告地址
    public static  String updateInfo = "https://gitee.com/Doxfer/invisible/raw/master/XiXiUp/Bulletin.php";
    //超时时长（200的倍数）
    public static int overTime =10;
    //作者微信
    public static String  wxid = "SokaUvgen";

    //用于存放下载地址
    public static  String downloadUrl = "";
    //用于存放更新公告
     public static  String updateInfoUtil = "";
    //用于存放是否更新
    public static int isUpdate = -1;
    //存放个人信息的格式
    public static String profileErrDetail = null;
    //是否是暗黑模式
    public static Boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandle(this));

        //普通权限获取
        for(int i=0;i<strings.length;i++){
            String permission = strings[i];
            int code = ActivityCompat.checkSelfPermission(this, permission);//判断权限是否赋予过
            if(code!= PackageManager.PERMISSION_GRANTED){//没有授权过，需要动态获取
                requestPermissions(strings,101);//动态获取
                break;
            }
        }

        //安卓11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //判断是否有管理外部存储的权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                } else {
                    //没有权限
                    Toast.makeText(this, "只有同意”习你太美“需要的所有权限\n你才能使用它\n请重新配置权限。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return;
                }
            }
        }

        //获取版本号
        PackageInfo packageinfo = null;
        try {
            PackageManager manager = getBaseContext().getPackageManager();
            packageinfo = manager.getPackageInfo(getBaseContext().getPackageName(), 0);
        }
        catch (Exception e){}
        int code = packageinfo.versionCode;

        vid = code;

        //获取是否是暗黑模式
        if(this.getApplicationContext().getResources().getConfiguration().uiMode == 0x21)
        {
            isDarkMode=true;
        }
        else
        {
            isDarkMode=false;
        }

        //获取更新
        CheckUpdate(Boolean.TRUE);


        //布局启动
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    // 菜单栏的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.About) {
            Key key = new Key();
            //不要使用getBaseContext()
            key.KeyDialog(MainActivity.this);
        }
        if (id == R.id.Update) {
            CheckUpdate(Boolean.FALSE);
        }
        if (id == R.id.Login) {
            //登录了就显示账户状态的dialog 包含登录/换绑 取消 和 详情 三个按钮
            //没有登录就要求登录
            //数据存储
            SharedPreferences data = getSharedPreferences("user",0);
            //查询数据
            if(data.getString("username",null)==null) {
                //要求登录
                Intent intent = new Intent();
                //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                intent.setClass(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
            else
            {
                UserProfileUnit.setUsername(data.getString("username",null));
                UserProfileUnit.setPassword(data.getString("password",null));

                Dao dao = new Dao();
                Thread thread = new Thread(dao);
                thread.start();

                int waitCount = 0;
                while (UserProfileUnit.getClassname()==null)
                {
                    if(waitCount<overTime) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        waitCount++;
                        continue;
                    }
                    Toast.makeText(MainActivity.this,"请求超时。无法获取资料。",Toast.LENGTH_LONG).show();
                    return false;
                }

                Log.i("程序.[账号控件被触发]Token()的值，现在显示弹框！",UserProfileUnit.getToken());

                UserProfileUnit.setToken(UserProfileUnit.getToken());
                ShowProfileDialog();
            }



        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /**
     * 检查更新
     * @param isAuto 是否为程序打开时自动检查更新？
     */
    public void CheckUpdate(boolean isAuto){
        Networks gUpdate = new Networks(MainActivity.this);
        Thread gthread = new Thread(gUpdate);
        gthread.start();
        int waitCount = 0;
        while (isUpdate==-1)
        {
            if(waitCount<overTime) {
                try {
                    Thread.sleep(200);
                    waitCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Toast.makeText(MainActivity.this,"请求超时。请手动获取更新",Toast.LENGTH_LONG).show();
            return;
        }

        switch (isUpdate)
        {
            case 0: if(!isAuto){Toast.makeText(MainActivity.this,"没有更新",Toast.LENGTH_SHORT).show();}
                break;
            case 1: ShowUpdateDialog();
                break;
            case 2: Toast.makeText(MainActivity.this,"无法连接到服务器",Toast.LENGTH_SHORT).show();
                break;
            case 3: Toast.makeText(MainActivity.this,"无法断开服务器 请联系开发者",Toast.LENGTH_LONG).show();
                break;
            case 4: Toast.makeText(MainActivity.this,"无法比较更新 请联系开发者",Toast.LENGTH_SHORT).show();
                break;
        }

    }


    /**
     * 显示更新弹框
     */
    public void ShowUpdateDialog()
    {
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("习你太美 有更新")
                .setMessage(updateInfoUtil)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("跳转到更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse(downloadUrl);
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })

                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "你拒绝了更新\n应用程序的功能可能会出现异常", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        alertDialog2.show();
    }

    /**
     * 弹窗
     * @param title 标题
     * @param info 文字
     */
    public void Dialog(String title,String info){
        AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)//标题
                .setMessage(info)//内容
                .setIcon(R.mipmap.ic_launcher_round)//图标
                .create();
        alertDialog1.show();

    }


    /**
     * 显示个人信息
     */
    public void ShowProfileDialog()
    {
        String showing;

        if(profileErrDetail==null)
        {
            int waitCount = 0;
            showing = String.format(Consts.FormatOfProfile, UserProfileUnit.getClassname(), UserProfileUnit.getSchool(), UserProfileUnit.getUsername(), UserProfileUnit.getToken());
            while (UserProfileUnit.getClassname()==null&&UserProfileUnit.getSchool()==null) {
                showing = String.format(Consts.FormatOfProfile, UserProfileUnit.getClassname(), UserProfileUnit.getSchool(), UserProfileUnit.getUsername(), UserProfileUnit.getToken());
                waitCount++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(waitCount>overTime)
                {
                    Toast.makeText(MainActivity.this,"请求超时",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
        else
        {
            showing = profileErrDetail;
        }

        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle(UserProfileUnit.getNick())
                .setMessage(showing)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("关闭此窗口", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })

                .setNeutralButton("注销", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //清空账号数据
                        SharedPreferences data = getSharedPreferences("user",0);
                        SharedPreferences.Editor editor = data.edit();
                        editor.clear();
                        editor.apply();
                        Toast.makeText(MainActivity.this, "已注销", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        alertDialog2.show();
    }

}

