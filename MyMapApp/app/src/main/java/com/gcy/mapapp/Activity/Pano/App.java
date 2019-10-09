package com.gcy.mapapp.Activity.Pano;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.gcy.mapapp.greendao.DaoMaster;
import com.gcy.mapapp.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;

public class App extends Application {
    private static final String TAG = "App";
    private static App mInstance = null;
    public BMapManager mBMapManager = null;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initEngineManager(this);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mapapp-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public void initEngineManager(Context context) {
        Log.i(TAG, "initEngineManager: start");
        if (mBMapManager == null) {
            Log.i(TAG, "initEngineManager: is null");
            mBMapManager = new BMapManager(context);
            Log.i(TAG, "initEngineManager: init end");
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(App.getInstance().getApplicationContext(), "BMapManager  初始化错误!",
                    Toast.LENGTH_LONG).show();
        }
        Log.d("ljx", "initEngineManager");
    }

    public static App getInstance() {
        return mInstance;
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int iError) {
            // 非零值表示key验证未通过
            if (iError != 0) {
                // 授权Key错误：
                Toast.makeText(App.getInstance().getApplicationContext(),
                        "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(App.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}