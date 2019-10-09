package com.gcy.mapapp.Activity.Photo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gcy.mapapp.Activity.Pano.App;
import com.gcy.mapapp.R;
import com.gcy.mapapp.entity.PhotoEntity;
import com.gcy.mapapp.greendao.DaoSession;
import com.gcy.mapapp.greendao.PhotoEntityDao;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<PhotoEntity> photoList = new ArrayList<>();

    private PhotoAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;


    PhotoEntityDao photoDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        photoDao = daoSession.getPhotoEntityDao();

        initFruits1();
//        initFruits();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        Log.d(TAG, "onCreate: recyclerView is" + recyclerView);
        Log.d(TAG, "onCreate: layoutManager is" + layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoAdapter(photoList);
        recyclerView.setAdapter(adapter);
//        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshFruits();
//            }
//        });
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFruits1();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    private void initFruits1(){
        photoList.clear();
        photoList = photoDao.queryBuilder()
                .list();

    }
}
