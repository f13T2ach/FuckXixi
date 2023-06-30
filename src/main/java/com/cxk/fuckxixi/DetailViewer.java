package com.cxk.fuckxixi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cxk.fuckxixi.databinding.FragmentSecondBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.namespace.QName;

public class DetailViewer extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String answer=null;


        //绑定控件
        TextView name = view.findViewById(R.id.name);
        name.setText(FirstFragment.name);
        TextView body = view.findViewById(R.id.body);

        int j=0;
        //输出答案
        for (int i=0;i<FirstFragment.titles.length;i++){

            //防止输出null
            if(i==0)
            {
                answer=FirstFragment.titles[i];
            }
            else
            {
                answer+=FirstFragment.titles[i];
            }
            //若题目已带换行符 则不换行
            if(answer.substring(answer.length()-1)!="\n")
            {
                answer+="\n";
            }

            //遍历答案列表
            while (true)
            {
                if(FirstFragment.answers.get(j).toString()=="false") //分隔符
                {
                    //answer+="\n";
                    j++;
                    break;
                }
                answer+=FirstFragment.answers.get(j);
                answer+="\n";
                j++;
            }
        }
        body.setText(answer);


        //绑定按钮部分
        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //复制按钮
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText(name.getText().toString(), body.getText().toString());
                // 将ClipData内容放到系统剪贴板里。
                clipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(getActivity(), "复制成功", Toast.LENGTH_SHORT).show();

            }
        });

        binding.screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //截图按钮
                takeScreenShot();

            }
        });


        //设置暗黑模式下进行反色
        RelativeLayout background = view.findViewById(R.id.relativeLayout);
        if (MainActivity.isDarkMode) {
            background.setBackgroundColor(Color.BLACK);
            name.setTextColor(Color.WHITE);
            body.setTextColor(Color.WHITE);
        }
        else
        {
            background.setBackgroundColor(Color.WHITE);
            name.setTextColor(Color.BLACK);
            body.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 代码来自https://www.coder.work/article/590575 有改动。
    /**
     * 截取答案长截图
     */
    private void takeScreenShot()
    {
        View u = ((Activity) getActivity()).findViewById(R.id.scroll);

        ScrollView z = (ScrollView) ((Activity) getActivity()).findViewById(R.id.scroll);
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();

        Bitmap b = getBitmapFromView(u,totalHeight,totalWidth);

        //Save bitmap
        String extr = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DCIM;
        String fileName = FirstFragment.name+" 的答案截图.jpg";
        File myPath = new File(extr, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, "Screen", "screen");
        }catch (FileNotFoundException e) {
            Log.e("程序.[截图]无法截图，因为输出图片失败",e.toString());
            Dialog("截图失败。","无法找到输出的图片。\n"+"图片原路径："+extr+fileName+"\n错误代码："+e);
        } catch (Exception e) {
            Log.e("程序.[截图]无法截图，发生未知错误。",e.toString());
            Dialog("截图失败。","位置错误。\n"+"图片原路径："+extr+fileName+"\n错误代码"+e);
        }
        Toast.makeText(getActivity(), "截图保存在相册。\n"+extr+"/"+fileName, Toast.LENGTH_LONG).show();


    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    /**
     * 弹窗
     * @param title 标题
     * @param info 文字
     */
    public void Dialog(String title,String info){
        AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity())
                .setTitle(title)//标题
                .setMessage(info)//内容
                .setIcon(R.mipmap.ic_launcher_round)//图标
                .create();
        alertDialog1.show();
    }

}