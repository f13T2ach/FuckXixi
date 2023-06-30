package com.cxk.fuckxixi.util;

import java.io.File;
import java.util.stream.IntStream;
import java.lang.String;

import android.util.Log;

import com.cxk.fuckxixi.FirstFragment;
import com.cxk.fuckxixi.util.Tools;

import org.json.JSONObject;

public class Works {

    /**
     * 获取作业路径
     * @param path 作业package路径
     * @return 作业路径
     */
    public static String[] GetWorksArray(String path)
    {
        Log.i("程序.[获取作业数组]传参（路径） ",path);
        File[] works = Tools.ListAllDir(path);
        //防止空参
        if(works == null)
        {
            return null;
        }
        String[] worksPath = new String[works.length];
        //把file转换为数组
        IntStream.range(0, works.length).forEach(i -> {
            worksPath[i] = works[i].toString()+"/resource/";
        });

        //倒序输出 方便查看
        String tmp;
        for(int start=0,end=worksPath.length-1;start<end;start++,end--) {
            String temp=worksPath[start];
            worksPath[start]=worksPath[end];
            worksPath[end]=temp;
        }
        return worksPath;
    }

    /**
     * 判断作业数组的元素是否是口语练习
     * @param paths 作业路径数组
     * @return 判断结果（作业类型数组）
     */
    public static Boolean[] IsOralPracticeUtil(String[] paths)
    {
        Boolean[] workTypes = new Boolean[paths.length];
        IntStream.range(0, paths.length).forEach(i -> {
            workTypes[i]=IsOralPractice(paths[i],i);
        });
        return workTypes;
    }

    /**
     * 判断是否是口语练习
     * @param path 作业路径
     * @param id 作业在主数组的下标
     * @return 作业类型
     */
    private static Boolean IsOralPractice(String path,int id)
    {
        //定义变量
        path += "resource.json";    //路径
        String jsonData;    //json文件的内容
        String totalScore;      //作业的总分

        //读取JSON文件
        File file = new File(path);
        jsonData = Tools.ReadFile(file);
        //排除不可用文件
        if(jsonData.charAt(0)!='{')
        {
            FirstFragment.ERRInfos.add("第 "+ id+" 项作业不可用。代码 "+jsonData+"\n");
            return Boolean.FALSE;
        }

        //读取总分 其它作业不能这样读取总分
        try
        {
            JSONObject jsonObject = new JSONObject(jsonData);
            totalScore = jsonObject.get("totalScore").toString();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e("程序.[检查是否为口语练习]发现异常", "文件目录： "+path);
            FirstFragment.ERRInfos.add("第 "+ id+" 项作业不可用，无法获取总分。路径: "+path+"\n");
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * 获取作业的名称
     * @param paths:作业路径数组
     * @return 作业名称数组
     */
    public static String[] GetWorksNamesUtil(String[] paths)
    {
        String[] worksNames = new String[paths.length];
        IntStream.range(0, paths.length).forEach(i -> {
            worksNames[i]=GetWorkName(paths[i]);
        });
        return worksNames;
    }

    /**
     * 获取单个作业的名称
     * @param path 作业路径
     * @return 作业名称
     */
    public static String GetWorkName (String path) {
        //定义变量
        path += "resource_h5.json";    //路径
        String jsonData;    //json文件的内容
        String name;      //作业的总分

        //读取JSON文件
        File file = new File(path);
        jsonData = Tools.ReadFile(file);

        //读取总分（口语考试的总分是30，依靠这个判断）
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            name = jsonObject.get("title").toString();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("程序.[获取作业名]发现异常", "文件目录： " + path);
            return "未知项目";
        }
        return name;
    }

    /**
     * 切割出答案
     * @param answer:没有被切割的答案
     * @return 切割并分行后的答案
     */
    public static String FormatAnswer(String answer)
    {
        answer = answer.substring(answer.indexOf("(") + 1, answer.lastIndexOf(")"));
        answer = answer.replace('|','\n');
        return answer;
    }

    /**
     * 删除目录下的所有文件和文件夹
     * @param root 目录
     */
    //网上随便找的 懒得写了
    public static void DeleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    DeleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        DeleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }
}
