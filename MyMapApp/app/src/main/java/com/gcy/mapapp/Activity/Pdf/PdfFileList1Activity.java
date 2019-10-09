package com.gcy.mapapp.Activity.Pdf;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.gcy.mapapp.R;
import com.gcy.mapapp.adapter.PdfFileAdapter;
import com.gcy.mapapp.entity.PdfFileEntity;
import com.gcy.mapapp.manager.PdfManager;
import com.gcy.mapapp.thread.GetPdfFileThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static br.com.zbra.androidlinq.Linq.stream;

public class PdfFileList1Activity extends AppCompatActivity {

    private static final String TAG = "PdfFileList1Activity";
    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;

    PdfManager pdfManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_file_list1);

        List<PdfFileEntity> webPdfFileEntities = getPdfFileEntities();
        if(webPdfFileEntities == null){
            Log.d(TAG, "onCreate: 获取网络pdf文件列表失败");
            return;
        }

        //判断是否有权限
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        Log.d(TAG, "onCreate: " + permissionCheck);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                    PERMISSION_CODE
            );
            return;
        }

        pdfManager  = new PdfManager(this);
        pdfManager.init();
        List<PdfFileEntity> pdfFiles = pdfManager.getPdfFiles();

        for (PdfFileEntity entity: webPdfFileEntities
             ) {
            PdfFileEntity temp = stream(pdfFiles).firstOrDefault(t -> t.getName().equalsIgnoreCase(entity.getName()), null);
            if (temp != null) {
                entity.setExist(true);
                entity.setFilePath(temp.getFilePath());
            }
        }

        ListView pdfListView = findViewById(R.id.pdfListView);
        PdfFileAdapter adapter = new PdfFileAdapter(PdfFileList1Activity.this, webPdfFileEntities );
        pdfListView.setAdapter(adapter);
    }

    private List<PdfFileEntity>  getPdfFileEntities(){
        List<PdfFileEntity> temp = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        GetPdfFileThread getPdfFileThread = new GetPdfFileThread(temp);
        Future<List<PdfFileEntity>> future  = pool.submit(getPdfFileThread);

        while(true){
            if(future.isDone()){
                try{
                    temp= future.get();
                }catch (Exception e){
                    Log.e(TAG, "getPdfFileEntities: " + e.getMessage());
                }
                pool.shutdown();
                break;
            }
        }

        return temp;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0
                    && grantResults[0] == getPackageManager().PERMISSION_GRANTED){
                Toast.makeText(this,"授权成功",Toast.LENGTH_LONG).show();
                pdfManager.init();
            }
        }
    }
}
