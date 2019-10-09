package com.gcy.mapapp.config;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.gcy.mapapp.util.SDCardUtils;

import static com.gcy.mapapp.util.FileUtils.createChildFilesDir;

public class SystemDirPath {
    private static final String TAG = "SystemDirPath";
    private static String MainWorkSpace = "/RuntimeViewer";//工作空间地址

    private static String Projects = "/Projects"; //系统工程文件夹

    private static String PhotoFilePath = "/PhotoFiles";
    private static String PdfFilePath = "/PdfFiles";
    private static String PrintScreenPath = "/PrintScreen";
    private static String SystemConf = "/System"; //系统模板
    private static String lockViewConf = "/lockscreen.conf"; //锁屏配置文件信息

    public static String SDPath = SDCardUtils.getSDCardPath();//系统SD卡路径

    /**
     * 获取SD卡工具路径
     * @return
     */
    public static String getSDPath(){
        return SDPath;
    }

    /**
     * 获取系统工作空间文件夹路径（主目录）
     * 主目录以系统内部存储为主
     * @return
     */
    public static String getMainWorkSpacePath(){
        String path = "/RuntimeViewer";

        if(SDPath == null){
            String dataPath = Environment.getDataDirectory().getPath();
            Log.d(TAG, "getMainWorkSpacePath: dataPath:" + dataPath);
            if (path!=null||(!path.equals(""))){
                return dataPath + path;
            }
            return  dataPath + MainWorkSpace;
        }else{
            if (path!=null||(!path.equals(""))){
                return SDPath + path;
            }
            return  SDPath + MainWorkSpace;
        }

    }

    /**
     * 获取系统配置文件夹路径（系统配置目录仅内部存储）
     * @return
     */
    public static String getSystemConfPath(){
        return getMainWorkSpacePath() + SystemConf;
    }


    /**
     * 获取工程文件夹路径
     * @return
     */
    public static String getProjectPath(){
        return  getMainWorkSpacePath() + Projects;
    }


    /**
     * 获取系统锁屏配置文件路径
     * @return
     */
    public static String getLockViewConfPath(Context context){
        return  getMainWorkSpacePath()+ SystemConf + lockViewConf;
    }



    public static String getPdfFilePath() {
        return getMainWorkSpacePath() + PdfFilePath;
    }

    /**
     * 获取截图路径
     * @return
     */
    public static String getPrintScreenPath(){
        String screenPath = getProjectPath() + PrintScreenPath;
        createChildFilesDir(screenPath);
        return  screenPath;
    }

    /**
     * 拍照路径
     * @return
     */
    public static String getPhotoFilePath(){
        String photoFullPath = getProjectPath() + PhotoFilePath;
        createChildFilesDir(photoFullPath);
        return photoFullPath;
    }
}
