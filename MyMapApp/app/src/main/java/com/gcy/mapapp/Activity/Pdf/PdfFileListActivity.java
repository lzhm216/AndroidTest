package com.gcy.mapapp.Activity.Pdf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gcy.mapapp.R;

public class PdfFileListActivity extends AppCompatActivity {

    private static final String TAG = "PdfFileListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_file_list);

//        List<PdfFileEntity> pdfFileEntities = new ArrayList<PdfFileEntity>();
//        pdfFileEntities.add(new PdfFileEntity("1.pdf",""));
//        pdfFileEntities.add(new PdfFileEntity("2.pdf",""));
//
//        ListView pdfListView = findViewById(R.id.pdfListView);
//        PdfFileAdapter adapter = new PdfFileAdapter(PdfFileListActivity.this, pdfFileEntities, );
//        pdfListView.setAdapter(adapter);

//        pdfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PdfFileEntity entity = pdfFileEntities.get(position);
//                Log.d(TAG, "onItemClick: "+ entity.getName());
//            }
//        });
    }

}
