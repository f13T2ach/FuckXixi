package com.cxk.fuckxixi.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 主要破解类
 */
public class Cracker {
    /*
    一般来说题型是这几种：
    1、听选信息，共3题，每题含2小题。
    2、回答问题，共1题，每题含4小题。
    3、信息转述，共1题。
    4、翻译句子，共2题。
     */

    public static String[] AnswerTitles = null; //答案的标题数组
    public static ArrayList Answers = new ArrayList();  //答案的集合 当值是false时，程序会输出下一个标题

    /**
     * 分配破解任务
     * @param json:JSON文件
     * @return 所有题目的标题
     */
    public static String[] Main(String json)
    {
        JSONObject jsonUtil; //json模型（临时）
        JSONArray arrayUtil; //临时数组
        JSONArray items;  //作业数组
        //读取json字符串到json模型
        try {
             jsonUtil = new JSONObject(json);
        }
        catch (Exception e){
            Log.e("程序.[分配破解任务]发现异常", "无法读取JSON");
            return new String[]{"ERR","无法读取作业JSON。"};
        }

        //解析出作业数组
        try {
            items = jsonUtil.getJSONArray("items");
        }
        catch (Exception e){
            Log.e("程序.[分配破解任务]发现异常", "无法解析到JSON："+e.toString());
            return new String[]{"ERR","无法解析到JSON。请确保软件是最新版。\n错误代码："+e};
        }

        AnswerTitles = new String[items.length()]; //标题的数量和item的数量相等

        JSONObject _question;
        JSONObject _templateSettings;
        JSONArray _scores;
        Answers = new ArrayList();
        //分配
        try {
            for (int i = 0; i < items.length(); i++) {
                //首先查看题目的类型是否是支持的。只有顶端注释的几个才是支持的。
                //以下是逐步找到type字段的过程，期间还给与json有相同名称的字段赋值（详见文档）
                jsonUtil=items.getJSONObject(i); //item[i]
                _templateSettings = jsonUtil.getJSONObject("templateSettings");
                _scores = jsonUtil.getJSONArray("scores");
                arrayUtil=jsonUtil.getJSONArray("questions"); //item[i].questions
                jsonUtil=arrayUtil.getJSONObject(0);   //item[i].questions[0]
                _question = jsonUtil;    //这里不用arrayUtil赋值是因为这个数组只有一个下标 所以存为object更方便

                //把获取到的题目和答案存到相应的字段里
                AnswerTitles[i]=_templateSettings.getString("content");

                //判断是否是不受支持的题型
                if(_question.getInt("type")!=5||_question.getJSONArray("children").length()==0||(_templateSettings.getInt("infoRetail")!=1&&_templateSettings.getInt("infoRetail")!=0))
                {
                    Answers.add("此题无答案");
                    Answers.add(false);
                    continue;
                }

                //获取答案并添加分割
                Crack(_question);
                Answers.add(false);


            }
        }
        catch (Exception e){
            Log.e("程序.[分配破解任务]发现异常", "无法分析JSON的item数组:"+e);
            return new String[]{"ERR","无法分析JSON的item数组。请确保软件是最新版。\n错误代码"+e};
        }

        return AnswerTitles;

    }


    /**
     * 破解出答案并输出到指定字段
     * @param _questions:指定题目的json元素中的questions集合
     */
    public static void Crack(JSONObject _questions)
    {
        JSONArray _children;  //json对应元素
        String _body;   //答案!
        //临时参数
        JSONArray arrUtil;
        JSONObject objUtil;
        int i=0;

        //获取children
        try {
            _children=_questions.getJSONArray("children");
        }
        catch (Exception e){
            Answers.add("无法获取到答案。因为无法解析到children元素。");
            Answers.add(false);
            return;
        }

        //逐步获取到答案
        while (i<_children.length())
        {
            try {
                objUtil = _children.getJSONObject(i); //item[i].questions[0].children[i] （...）
                arrUtil = objUtil.getJSONArray("options"); //（...）.options
                objUtil = arrUtil.getJSONObject(0); //（...）.options[0]
                arrUtil = objUtil.getJSONArray("value"); //（...）.options[0].value
                objUtil = arrUtil.getJSONObject(0);  //（...）.options[0].value[0]
                _body = objUtil.getString("body");  //（...）.options[0].value[0].body
                _body = Works.FormatAnswer(_body);
                Answers.add(_body);
                Answers.add("\n");
                i++;
            }
            catch(Exception e){
                Answers.add("无法获取到第 "+(i+1)+" 小题答案。因为无法解析到children的下标 "+i);
                i++;
                continue;
            }
        }
    }

}
