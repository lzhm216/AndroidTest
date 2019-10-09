package com.gcy.mapapp.thread;

import android.util.Log;

import com.gcy.mapapp.entity.PdfFileEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPdfFileThread implements Callable<List<PdfFileEntity>> {

    private static final String TAG = "GetPdfFileThread";
    private List<PdfFileEntity> webPdfFileEntities = null;

    public GetPdfFileThread(List<PdfFileEntity> webPdfFileEntities) {
        this.webPdfFileEntities = webPdfFileEntities;
    }

    @Override
    public List<PdfFileEntity> call() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.2.116/pdf/pdflist.json")
                .build();
        try {
            Response execute = client.newCall(request).execute();
            String jsonBody = execute.body().string();
            Gson gson = new Gson();
            //PdfFileEntity pdfFileEntity = gson.fromJson("", PdfFileEntity.class);  单个解析
            webPdfFileEntities = gson.fromJson(jsonBody, new TypeToken<List<PdfFileEntity>>() {
            }.getType());

            for (PdfFileEntity entity:webPdfFileEntities
                 ) {
                Log.d(TAG, "call: " + entity.getName() + "," + entity.getUrl());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return webPdfFileEntities;
    }

}
