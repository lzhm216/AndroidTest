package com.gcy.mapapp.manager;

import android.content.Context;
import android.util.Log;

import com.gcy.mapapp.config.SystemDirPath;
import com.gcy.mapapp.entity.PhotoEntity;
import com.gcy.mapapp.util.DateUtils;
import com.gcy.mapapp.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class PhotoManage {
    private static final String TAG = "PhotoManage";
    Context mContext;
    public PhotoManage(Context context) {
        mContext = context;
    }

    public List<PhotoEntity> getPhotoFiles() {
        List<PhotoEntity> photoAlbums = new ArrayList<PhotoEntity>();
        String jpgFilePath = SystemDirPath.getPhotoFilePath();
        Log.d(TAG, "getPhotoFiles: " + jpgFilePath);
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(jpgFilePath, "jpg");
        if(fileInfos == null || fileInfos.size()<=0) return null;

        for (FileUtils.FileInfo fileInfo : fileInfos) {
            photoAlbums.add(new PhotoEntity(null, fileInfo.FileName, fileInfo.FilePath, DateUtils.getTimeNow(),true));
        }
        return photoAlbums;
    }


}
