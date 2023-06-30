package com.cxk.fuckxixi.util;
import android.util.Log;
import java.io.*;

public class Tools {

    /**
     * 输出目录下的所有子目录
     * @param path:目录路径
     * @return 所有子目录的集合
     */
    public static File[] ListAllDir(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        files = dir.listFiles(fileFilter);
        if (files==null) {
            return null;
        }

        if (files.length == 0) {
            //目录不存在
            return null;
        }
        else {
            return files;
        }

    }

    /**
     * 读取文件流并转为字符串输出
     * @param fis:文件流
     * @return 文件的字符串
     */

    public static String readStringFromInputStream(FileInputStream fis) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toString();
    }


    /**
     * 读取文件
     * @param file:文件
     * @return 文件的字符串
     */
    public static String ReadFile(File file)
    {
        if (file.exists()) {
            FileInputStream fis;
            //从输入流中读取内容
            try {
                fis = new FileInputStream(file);
                //从输入流中读取内容
                String content = Tools.readStringFromInputStream(fis);
                return content;
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Log.e("程序.[Tools.读取文件]发现异常","文件目录： "+file.toString()+" ，异常信息： ",e);
                return e.toString();
            }
        }
        Log.e("程序.[Tools.读取文件]文件不存在","文件目录： "+file.toString());
        return "JSON文件不存在"+" 期望文件目录： "+file.toString();
    }
}
