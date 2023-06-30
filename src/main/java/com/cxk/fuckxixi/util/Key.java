package com.cxk.fuckxixi.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cxk.fuckxixi.MainActivity;
import com.cxk.fuckxixi.R;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

//不要在打包后留下log
public class Key {

    private static String dialogTitle="验证激活码";

    private static String dialogContent="请添加此微信\n"+ MainActivity.wxid+"\n来获得激活码\n\n你的识别码是 "+getSerialNumber();

    private static String DEVICE_CODE = getSerialNumber();

    public void KeyDialog(Context context){
        final EditText keyEdit =  new EditText(context);
        keyEdit.setHint("AAAAA-BBBBB-CCCCC");
        AlertDialog keyDialog = new AlertDialog.Builder(context)
                .setTitle(dialogTitle)//标题
                .setMessage(dialogContent)//内容
                .setView(keyEdit)
                .setIcon(R.mipmap.ic_launcher_round)//图标
                .setCancelable(false)
                .setPositiveButton("验证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //监听已经被拦截，防止点击按钮后对话框消失
                    }
                })

                .setNegativeButton("不提供", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        KeyDialog(context);
                        Toast.makeText(context,"请提供激活码",Toast.LENGTH_SHORT).show();
                    }
                })

                .create();
        keyDialog.show();

        //防止点击后对话框消失
        keyDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 验证
                //KeyDialog(context);
                //Toast.makeText(context,"激活码错误",Toast.LENGTH_LONG).show();
                if(keyEdit.getText().toString().equals(""))
                {
                    keyEdit.setText(generateActivationCode());
                }
                else
                {
                    if(verifyActivationCode(keyEdit.getText().toString()))
                    {
                        keyDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(context,"激活码错误",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    public static String generateActivationCode() {
        String activationCode = "";

        // Generate random number for C part
        Random random = new Random();
        int randomNumber = random.nextInt(99999);

        // Generate MD5 hash for A part
        String md5Hash = getMD5Hash(DEVICE_CODE);

        // Take the first five characters of the MD5 hash
        String aPart = md5Hash.substring(0, 5);

        // Convert the ASCII code of B part to decimal and add to C part
        int bPart = 0;
        for (int i = 0; i < DEVICE_CODE.length(); i++) {
            bPart += DEVICE_CODE.charAt(i);
        }
        int cPart = bPart + randomNumber;

        // Convert C part to hex string
        String hexString = Integer.toHexString(cPart);

        // Pad with leading zeros if necessary
        while (hexString.length() < 5) {
            hexString = "0" + hexString;
        }

        // Combine A, B, and C parts into activation code
        activationCode = String.format("%s-%05d-%s", aPart, randomNumber, hexString.toUpperCase());

        return activationCode;
    }

    public static boolean verifyActivationCode(String activationCode) {
        boolean isValid = false;

        String[] parts = activationCode.split("-");
        if (parts.length == 3) {
            String aPart = parts[0];
            int bPart = Integer.parseInt(parts[1]);
            String cPart = parts[2];

            // Verify C part
            int cPartDecimal = Integer.parseInt(cPart, 16);
            int bPartDecimal = 0;
            for (int i = 0; i < aPart.length(); i++) {
                bPartDecimal += aPart.charAt(i);
            }
            if (cPartDecimal - bPartDecimal == bPart) {
                // Verify A part
                String md5Hash = getMD5Hash(DEVICE_CODE);
                String expectedAPart = md5Hash.substring(0, 5);
                if (expectedAPart.equals(aPart)) {
                    isValid = true;
                }
            }
        }

        return isValid;
    }

    private static String getMD5Hash(String input) {
        String md5Hash = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            md5Hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5Hash;
    }


    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "读取设备序列号异常：" + e.toString());
            return e.toString();
        }
        return serial;
    }

}
