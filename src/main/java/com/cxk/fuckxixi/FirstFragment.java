package com.cxk.fuckxixi;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cxk.fuckxixi.databinding.FragmentFirstBinding;
import com.cxk.fuckxixi.handle.ExceptionHandle;
import com.cxk.fuckxixi.util.Cracker;
import com.cxk.fuckxixi.util.Tools;
import com.cxk.fuckxixi.util.Works;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    View mainView = null;
    public static ArrayList ERRInfos = new ArrayList();
    public static String[] paths;
    public static String resPath;   //作业路径

    public static ArrayList answers=null;  //答案集合
    public static String[] titles=null;   //题目
    public static String name=null;   //作业名
    public static int id=0;
    public static int lsid = 0;//长按点击的作业id

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        mainView = binding.getRoot();

        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandle(getActivity()));

        answers = null;//重置
        SubmitToList(Boolean.TRUE);//自动加载

        //监听刷新按钮是否被点击
        binding.refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SubmitToList(Boolean.FALSE);

            }
        });

        //监听查看排除依据是否被点击
        binding.showerr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String err = "当你有些口语练习作业不能打开来获取答案，请把这些信息发给开发者。\n一共有 "+ERRInfos.size()+" 项\n";
                for (int i=0;i<ERRInfos.size();i++ ){
                    err+=ERRInfos.get(i)+"\n";
                }
                Dialog("排除依据",err);

            }
        });

        //监听删除全部按钮是否被点击
        binding.clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Works.DeleteAllFiles(new File(resPath));
                Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();
                SubmitToList(Boolean.FALSE);
            }
        });

        //设置标题的颜色
        TextView xntm = mainView.findViewById(R.id.xntm);
        if (MainActivity.isDarkMode) {
            xntm.setTextColor(Color.WHITE);
        } else {
            xntm.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 把作业名上传到列表
     * @param isAuto: 是否为打开时的自动刷新
     */
    public void SubmitToList(Boolean isAuto) {
        Log.i("程序.[列表对象]", "-----重载列表-----");
        ERRInfos = new ArrayList();

        //获取所有作业的目录
        resPath = Environment.getExternalStorageDirectory() + "/ciwong/newspaper/packages/";
        paths = Works.GetWorksArray(resPath);
        if (paths == null) {
            Toast toast = Toast.makeText(getActivity(), "你没有安装习习向上，或者说你没有打开过任何一项作业。", Toast.LENGTH_LONG);
            toast.show();
            ListView listView = mainView.findViewById(R.id.worklist);
            listView.setAdapter(null);
            return;
        }

        //获取作业类型
        Boolean[] workTypes = Works.IsOralPracticeUtil(paths);
        //获取作业名
        String[] worksNames = Works.GetWorksNamesUtil(paths);
        //输出作业名到list
        ListView listView = mainView.findViewById(R.id.worklist); //这里的view要用全局的 否则会使用该按钮的函数 导致出错
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, worksNames);
        listView.setAdapter(arrayAdapter);    //这里的参数一不能直接使用this
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GotoAnswer(workTypes, paths, worksNames, i);
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                lsid = (int) listView.getItemIdAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);
                menu.setHeaderTitle("管理 " + worksNames[id]);
                menu.add(0, 0, 0, "抹去作业");
            }
        });
        if (!isAuto) {
            Toast toast = Toast.makeText(getActivity(), "成功获取到 " + paths.length + " 份作业。", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //引进选择的id
        switch (item.getItemId()) {
            case 0:
                String path = paths[lsid].substring(0, paths[lsid].indexOf("/resource/"));
                Log.i("程序.[抹去作业]", "准备抹去 "+id+" ，路径 "+path);
                Works.DeleteAllFiles(new File(path));
                //删除文件夹
                File folder = new File(path);
                folder.delete();
                //刷新
                SubmitToList(Boolean.FALSE);
                Toast toast = Toast.makeText(getActivity(), "已抹去", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
        return FirstFragment.super.onContextItemSelected(item);
    }


    /**
     * 处理点击作业项目事件
     * @param workTypes 作业类型数组
     * @param paths 作业路径数组
     * @param names 作业名数组
     * @param i 作业下标
     */
    private void GotoAnswer(Boolean[] workTypes,String[] paths,String[] names,int i){
        Log.i("程序.[作业列表]","用户点击项目： "+i);
        if(workTypes[i]==Boolean.FALSE)
        {
            Snackbar.make(mainView, "该作业不是听说练习。", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        //读取题目和答案 以及作业名称以显示
        File file = new File(paths[i]+"/resource.json");
        titles = Cracker.Main(Tools.ReadFile(file));
        answers = Cracker.Answers;
        name = names[i];
        id=i;

        //异常处理 这里的titles原本是作业题目 现在用于返回错误代码
        if (titles[0].equals("ERR"))
        {
            Dialog("无法解析 "+names[i],"程序返回"+titles[1]+"\n作业路径： "+paths[i]+"\n作业是否为听说考试： "+workTypes[i]+"\n\n请把错误信息发给我。");
            return;
        }

        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
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