package com.gcy.mapapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.gcy.mapapp.Activity.Camera2.CameraActivity;
import com.gcy.mapapp.Activity.Pano.PanoDemoActivity;
import com.gcy.mapapp.Activity.Pdf.PdfFileList1Activity;
import com.gcy.mapapp.Activity.Photo.PhotoListActivity;
import com.gcy.mapapp.common.Variable;
import com.gcy.mapapp.config.SystemDirPath;
import com.gcy.mapapp.entity.KeyAndValueBean;
import com.gcy.mapapp.util.ToastUtils;
import com.gcy.mapapp.view.MapNorthView;
import com.gcy.mapapp.view.MapRotateView;
import com.gcy.mapapp.view.MapZoomView;
import com.gcy.mapapp.view.MeasureToolView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity   {
    private MapView mMapView;
    private ImageView img_tdt_yx;
    private ImageView img_tdt_sl;
    private ImageView img_jyg_yx;
    private View view_measure_tool;
    private static final String TAG = "MainActivity";
    private static final int DIALOG_ID = 0;

    ArcGISMapImageLayer arcGISMapImageLayer = null;
    boolean identityFlag = false;
    List<IdentityResult> identityResults = null;
    GraphicsOverlay identifyGraphicOverlay = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.mapView);
        img_tdt_yx = findViewById(R.id.tdt_yx);
        img_tdt_sl = findViewById(R.id.tdt_sl);
        img_jyg_yx = findViewById(R.id.jyg_yx);
        view_measure_tool = findViewById(R.id.measure_tool);

        addBasemap();
        addBasemapJygYx();
        addBasemapTdtSl();
        addBasemapTdtYx();


        MapZoomView zoomBtn=(MapZoomView)findViewById(R.id.map_zoom_btn);
        zoomBtn.init(mMapView);
//        zoomBtn.setZoomHeight(40);
//        zoomBtn.setZoomWidth(60);
//        zoomBtn.setZoomBackground(R.drawable.round_corner);
        zoomBtn.isHorizontal(false);
        zoomBtn.setZoomInNum(2);
        zoomBtn.setZoomOutNum(2);
//        zoomBtn.setZoomOutImage(R.drawable.map_zoomout);
//        zoomBtn.setZoomInImage(R.drawable.map_zoomin);
//        zoomBtn.setShowText(true);
//        zoomBtn.setZoomOutText("缩小");
//        zoomBtn.setZoomInText("放大");
//        zoomBtn.setFontSize(12);
//        zoomBtn.setFontColor(R.color.white);

        MeasureToolView measureToolView=(MeasureToolView)findViewById(R.id.measure_tool);
        measureToolView.init(mMapView);
//        measureToolView.setButtonWidth(55);
//        measureToolView.setButtonHeight(35);
//        measureToolView.setMeasureBackground(R.color.colorAccent);
        measureToolView.setShowText(false);

//        measureToolView.setFontSize(12);
//        measureToolView.setFontColor(R.color.color444);
//        measureToolView.setMeasurePrevStr("撤销");
//        measureToolView.setMeasureNextStr("恢复");
//        measureToolView.setMeasureLengthStr("测距");
//        measureToolView.setMeasureAreaStr("测面积");
//        measureToolView.setMeasureClearStr("清除");
//        measureToolView.setMeasureEndStr("完成");
//        measureToolView.setMeasurePrevImage(R.drawable.map_measure_prev);
//        measureToolView.setMeasureNextImage(R.drawable.map_measure_next);
//        measureToolView.setMeasureLengthImage(R.drawable.map_measure_length);
//        measureToolView.setMeasureAreaImage(R.drawable.map_measure_area);
//        measureToolView.setMeasureClearImage(R.drawable.map_measure_clear);
//        measureToolView.setMeasureEndImage(R.drawable.map_measure_end);
        measureToolView.setSpatialReference(SpatialReference.create(3857));
        measureToolView.setLengthType(Variable.Measure.KM);
        measureToolView.setAreaType(Variable.Measure.KM2);


        MapRotateView mapRotateView=(MapRotateView)findViewById(R.id.map_rotate_view);
        mapRotateView.init(mMapView);

        MapNorthView mapNorthView = (MapNorthView)findViewById(R.id.map_north_view);
        mapNorthView.init(mMapView);

        //加载矢量地图
//        String tdghdl = "http://61.178.152.45:6080/arcgis/rest/services/OneMap/jygtdghdl/MapServer/0";
//        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(tdghdl);
//        serviceFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);
//        serviceFeatureTable.addLoadStatusChangedListener(new LoadStatusChangedListener() {
//            @Override
//            public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
//                if(loadStatusChangedEvent.getNewLoadStatus() == LoadStatus.LOADED){
//                    ServiceFeatureTable.FeatureRequestMode featureRequestMode =
//                            serviceFeatureTable.getFeatureRequestMode();
//                    String featureRequestModeName = featureRequestMode.name();
//                    Log.i("name", featureRequestModeName);
//                }
//            }
//        });
//        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
//        map.getOperationalLayers().add(featureLayer);

        img_tdt_yx.setOnClickListener(listener);
        img_tdt_sl.setOnClickListener(listener);
        img_jyg_yx.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tdt_sl) {
                changeBasemap("tdtsl");
            } else if (v.getId() == R.id.tdt_yx) {
                changeBasemap("tdtyx");
            } else if (v.getId() == R.id.jyg_yx) {
                changeBasemap("dom2018");
            }
        }
    };

    private void changeBasemap(String layerName) {
      LayerList layers =  mMapView.getMap().getBasemap().getBaseLayers();
        for (Layer layer:layers
             ) {
            if(layer.getName().contains(layerName)){
                layer.setVisible(true);
            }else{
                layer.setVisible(false);
            }
        }
    }

    void addBasemap(){
        ArcGISMap map = new ArcGISMap();
        mMapView.setMap(map);
    }
    /**
     * 嘉峪关影像
     */
    void addBasemapJygYx(){
        //加载影像底图
        String theURLString =
                "http://61.178.152.45:6080/arcgis/rest/services/OneMap/JYGDOM2018/MapServer";

        ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(theURLString);
        tiledLayer.setName("dom2018");
        tiledLayer.setVisible(true);
        mMapView.getMap().getBasemap().getBaseLayers().add(tiledLayer);
//        Basemap basemap = new Basemap(tiledLayer);
//        ArcGISMap map = new ArcGISMap(basemap);
//        mMapView.setMap(map);

    }

    /**
     * 天地图影像
     */
    void addBasemapTdtYx(){
//        Polygon visibleArea = null;
//        if(mMapView!=null){
//            visibleArea =mMapView.getVisibleArea();
//            Log.d(TAG, "onClick: " + visibleArea.getExtent().getXMin() +","+ visibleArea.getExtent().getXMax() +","+ visibleArea.getExtent().getYMin() +","+ visibleArea.getExtent().getYMax() );
//        }

        //加载天地图影像
        WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_2000);
        WebTiledLayer webTiledLayer1 = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000);

        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
        webTiledLayer.setRequestConfiguration(requestConfiguration);
        webTiledLayer.loadAsync();
        webTiledLayer.setName("tdtyx");
        webTiledLayer.setVisible(false);

        webTiledLayer1.setRequestConfiguration(requestConfiguration);
        webTiledLayer1.loadAsync();
        webTiledLayer1.setName("tdtyxzj");
        webTiledLayer1.setVisible(false);

        mMapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);
        mMapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer1);

//        if(visibleArea!=null){
//            mMapView.setViewpoint(new Viewpoint(visibleArea.getExtent()));
//        }
    }

    /**
     * 天地图矢量
     */
    void addBasemapTdtSl(){

//        Polygon visibleArea = mMapView.getVisibleArea();
//        Log.d(TAG, "onClick: " + visibleArea.getExtent().getXMin() +","+ visibleArea.getExtent().getXMax() +","+ visibleArea.getExtent().getYMin() +","+ visibleArea.getExtent().getYMax());

        //加载天地图矢量
        WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_2000);
        WebTiledLayer webTiledLayer1 = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000);

        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
        webTiledLayer.setRequestConfiguration(requestConfiguration);
        webTiledLayer.loadAsync();
        webTiledLayer.setName("tdtsl");
        webTiledLayer.setVisible(false);

        webTiledLayer1.setRequestConfiguration(requestConfiguration);
        webTiledLayer1.loadAsync();
        webTiledLayer1.setName("tdtslzj");
        webTiledLayer1.setVisible(false);

        mMapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer);
        mMapView.getMap().getBasemap().getBaseLayers().add(webTiledLayer1);

//        mMapView.setViewpoint(new Viewpoint(visibleArea.getExtent()));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_tdt_sl:
                changeBasemap("tdtsl");
                break;
            case R.id.menu_tdt_yx:
                changeBasemap("tdtyx");
                break;
            case R.id.menu_jygdom:
                changeBasemap("dom2018");
                break;
            case R.id.menu_addfeature:
                String featureUrl ="http://61.178.152.45:6080/arcgis/rest/services/OneMap/jygtdghdl/MapServer";
                ArcGISTiledLayer tiledLayerBaseMap = new ArcGISTiledLayer(featureUrl);
                tiledLayerBaseMap.setOpacity((float) 0.6);
                mMapView.getMap().getOperationalLayers().add(tiledLayerBaseMap);
                break;
            case R.id.menu_addimage:
                String imagelayerUrl ="http://61.178.152.45:6080/arcgis/rest/services/OneMap/jygtdghdl/MapServer";
                arcGISMapImageLayer = new ArcGISMapImageLayer(imagelayerUrl);
//                arcGISMapImageLayer.setOpacity((float) 0.6);
                arcGISMapImageLayer.setVisible(false);
                mMapView.getMap().getOperationalLayers().add(arcGISMapImageLayer);
                break;
            case R.id.menu_measure:
                if(view_measure_tool.getVisibility() == View.GONE){
                    view_measure_tool.setVisibility(View.VISIBLE);
                }else if(view_measure_tool.getVisibility() == View.VISIBLE){
                    view_measure_tool.setVisibility(View.GONE);
                }
                break;
            case R.id.menu_quanjing:
                Intent intent = new Intent(MainActivity.this, PanoDemoActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_pdfviwer:
                Intent intentpdf = new Intent(MainActivity.this, PdfFileList1Activity.class);
                this.startActivity(intentpdf);
                break;
            case R.id.menu_printscreen:
                Bitmap mapViewBitmap = getMapViewBitmap();
                final int i = saveImageToGallery(mapViewBitmap);
                break;
            case R.id.menu_camera:
                Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
                this.startActivity(cameraIntent);
                break;
            case R.id.menu_photo:
                Intent photoIntent = new Intent(MainActivity.this, PhotoListActivity.class);
                this.startActivity(photoIntent);
                break;
            case R.id.menu_identity:
                if(!identityFlag){
                    identifyGraphicOverlay = new GraphicsOverlay();
                    mMapView.getGraphicsOverlays().add(identifyGraphicOverlay);
                    arcGISMapImageLayer = new ArcGISMapImageLayer("http://61.178.152.45:6080/arcgis/rest/services/OneMap/jygtdghdl/MapServer");
                    mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView){
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));

                            //查询mFeatureLayer图层中点击范围内的所有地理要素
                            final ListenableFuture<IdentifyLayerResult> identifyLayerResultListenableFuture =
                                    mMapView.identifyLayerAsync(arcGISMapImageLayer, screenPoint, 12, false, 10);
                            identifyLayerResultListenableFuture.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        IdentifyLayerResult identifyLayerResult = identifyLayerResultListenableFuture.get();
                                        handleIdentifyResult(identifyLayerResult);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            return true;
                        }
                    });
                    identityFlag = true;
                }

                break;
                case R.id.menu_identitys:
                if(!identityFlag){
                    identifyGraphicOverlay = new GraphicsOverlay();
                    mMapView.getGraphicsOverlays().add(identifyGraphicOverlay);
                    mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView){
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));

                            //查询mFeatureLayer图层中点击范围内的所有地理要素
                            final ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultListenableFuture =
                                    mMapView.identifyLayersAsync(screenPoint, 12, false, 10);
                            identifyLayerResultListenableFuture.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d(TAG, "run: start");
                                        List<Feature> selectFeatureList = new ArrayList<>();
                                        List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultListenableFuture.get();
//                                        handleIdentifyResults(identifyLayerResults);
                                        for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
                                            for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                                                identifyLayerResult.getLayerContent();
                                                if (identifiedElement instanceof Feature) {
                                                    Feature identifiedFeature = (Feature) identifiedElement;
                                                    selectFeatureList.add(identifiedFeature);
                                                }
                                            }
                                            if(identifyLayerResult.getSublayerResults().size()>0){
                                                List<IdentifyLayerResult> sublayerResults = identifyLayerResult.getSublayerResults();
                                                for (IdentifyLayerResult subResult:sublayerResults
                                                ) {
                                                    for (GeoElement element: subResult.getElements()
                                                    ) {
                                                        Feature identifiedFeature = (Feature) element;
                                                        selectFeatureList.add(identifiedFeature);
                                                    }
                                                }
                                            }

                                        }
                                        Log.d(TAG, "featurelist size" + selectFeatureList.size());
                                        selectFeature(selectFeatureList);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            return true;
                        }
                    });
                    identityFlag = true;
                }

                break;
            default:
                break;
        }
        return true;
    }

    private void selectFeature(List<Feature> selectFeatureList) {
        clearAllFeatureSelect();//清空选择

        int num = selectFeatureList.size();
        if (num==0){
            ToastUtils.showShort(this,"当前没有选中任何要素");
        }else if(num==1){
            FeatureTable featureTable = selectFeatureList.get(0).getFeatureTable();
//            FeatureLayer layer = selectFeatureList.get(0).getFeatureTable().getFeatureLayer();
//            String layerName = layer.getName();
            String layerName = featureTable.getDisplayName();
            Toast.makeText(this, "选择的图层为：" +layerName , Toast.LENGTH_SHORT).show();
//            txtLayerName.setText(layerName);
            setFeatureSelect(selectFeatureList.get(0));
        }else{
//            //当前选中要素大于1个图层
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("选择哪个图层要素？");
//            //指定下拉列表的显示数据
//            AlertLayerListAdapter layerListAdapter = new AlertLayerListAdapter(this,selectFeatureList);
//            //设置一个下拉的列表选择项
//            builder.setAdapter(layerListAdapter, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    FeatureLayer layer = selectFeatureList.get(which).getFeatureTable().getFeatureLayer();
//                    String layerName = layer.getName();
//                    Toast.makeText(context, "当前选择图层：" +layerName , Toast.LENGTH_SHORT).show();
//                    txtLayerName.setText(layerName);
//                    setFeatureSelect(selectFeatureList.get(which));
//                }
//            });
//            builder.show();
        }
    }

    /**
     * 设置要素选中
     * @param feature
     */
    public void setFeatureSelect(Feature feature) {
        //设置要素选中
//        FeatureTable featureTable = feature.getFeatureTable();
//        FeatureLayer identifiedidLayer=new FeatureLayer(featureTable);
//        identifiedidLayer.setSelectionColor(Color.YELLOW);
//        identifiedidLayer.setSelectionWidth(20);
//        identifiedidLayer.selectFeature(feature);

        identifyGraphicOverlay.getGraphics().clear();
        Graphic graphic = new Graphic(feature.getGeometry(), feature.getAttributes());
        SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,Color.BLUE,(float)2);
        SimpleRenderer simpleRenderer = new SimpleRenderer(new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.RED, simpleLineSymbol));
        identifyGraphicOverlay.setRenderer(simpleRenderer);
        identifyGraphicOverlay.getGraphics().add(graphic);

        //设置要素属性结果
        List<KeyAndValueBean> keyAndValueBeans = new ArrayList<>();
        Map<String,Object> attributes= feature.getAttributes();
        for (Map.Entry<String, Object> entry:attributes.entrySet()){
            String key=entry.getKey();
            Object object = entry.getValue();
            String value ="";
            if (object!=null){
                value = String.valueOf(object);
            }
            Log.d(TAG, "setFeatureSelect: key " + key + "," + value);
            KeyAndValueBean keyAndValueBean = new KeyAndValueBean();
            keyAndValueBean.setKey(key);
            keyAndValueBean.setValue(value);

            keyAndValueBeans.add(keyAndValueBean);
        }

//        AttributeAdapter attributeAdapter = new AttributeAdapter(this, keyAndValueBeans);
//        listViewField.setAdapter(attributeAdapter);
    }

    /**
     * 清空所有要素选择
     */
    public void clearAllFeatureSelect(){
        List<Layer> layers = mMapView.getMap().getOperationalLayers();
        for (int i=0;i<layers.size();i++){
            ArcGISTiledLayer arcGISTiledLayer = (ArcGISTiledLayer)layers.get(i);
            FeatureTable featureTable =new ServiceFeatureTable(arcGISTiledLayer.getUri() + "/0");
            FeatureLayer featureLayer = new FeatureLayer(featureTable);
            featureLayer.clearSelection();
        }
    }

    /**
     * 恢复默认状态
     */
    public void clear(){
        clearAllFeatureSelect();
//        listViewField.setAdapter(null);
//        txtLayerName.setText("未选中图层");
    }







    private void handleIdentifyResult(IdentifyLayerResult identifyLayerResult) {
        StringBuilder message = new StringBuilder();
        int totalCount = 0;
        identityResults = new ArrayList<>();

        geoElementsCountFromResult(identifyLayerResult);
        Log.d(TAG, "identityResults size: " + identityResults.size());
        String layerName = identifyLayerResult.getLayerContent().getName();
        message.append(layerName).append(": ").append(identityResults.size());

        for (IdentityResult result: identityResults
        ) {
            message.append("-----------------\n");
            message.append(result.toString());
        }


        if (identityResults.size() > 0) {
            showAlertDialog(message);
        } else {
            Toast.makeText(this, "No element found", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No element found.");
        }
    }

    private void handleIdentifyResults(List<IdentifyLayerResult> identifyLayerResults) {
        StringBuilder message = new StringBuilder();
        int totalCount = 0;
         identityResults = new ArrayList<>();
        for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
            geoElementsCountFromResult(identifyLayerResult);
            Log.d(TAG, "identityResults size: " + identityResults.size());
            String layerName = identifyLayerResult.getLayerContent().getName();
            message.append(layerName).append(": ").append(identityResults.size());


            // add new line character if not the final element in array
            if (!identifyLayerResult.equals(identifyLayerResults.get(identifyLayerResults.size() - 1))) {
                message.append("\n");
            }
        }

        for (IdentityResult result: identityResults
        ) {
            message.append("-----------------\n");
            message.append(result.toString());
        }


        if (identityResults.size() > 0) {
            showAlertDialog(message);
        } else {
            Toast.makeText(this, "No element found", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No element found.");
        }
    }

    private void geoElementsCountFromResult(IdentifyLayerResult result) {

        List<IdentifyLayerResult> tempResults = new ArrayList<>();
        tempResults.add(result);

        // using Depth First Search approach to handle recursion
        int count = 0;
        int index = 0;

        while (index < tempResults.size()) {
            Log.d(TAG, "Loop: " + index +" of " + tempResults.size());
            // get the result object from the array
            IdentifyLayerResult identifyResult = tempResults.get(index);

            // update count with geoElements from the result
            count += identifyResult.getElements().size();
            Log.d(TAG, "layer type " + identifyResult.getLayerContent() +", parent element size " + count + ", sub result size " +  identifyResult.getSublayerResults().size());
            // if sublayer has any results, add result objects in the tempResults array after the current result
            if (identifyResult.getSublayerResults().size() > 0) {
                List<IdentifyLayerResult> sublayerResults = identifyResult.getSublayerResults();
                for (IdentifyLayerResult subResult:sublayerResults
                     ) {
                    List<GeoElement> elements = subResult.getElements();
                    Log.d(TAG, "sub element size: " + elements.size());
                    for (GeoElement element: elements
                         ) {
                        Map<String, Object> attributes = element.getAttributes();
                        Geometry geometry = element.getGeometry();
                        identityResults.add(new IdentityResult(attributes, geometry));
                    }
                }
//                tempResults.add(identifyResult.getSublayerResults().get(index));

            }

            // update the count and repeat
            index += 1;
        }
        return ;
    }


    private void showAlertDialog(StringBuilder message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Number of elements found");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the alert dialog
        alertDialog.show();
    }

    public class IdentityResult{
        private Map<String, Object> attributes;
        private Geometry geometry;

        public IdentityResult(Map<String, Object> attributes, Geometry geometry) {
            this.attributes = attributes;
            this.geometry = geometry;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder result =  new StringBuilder();
            for (String key:attributes.keySet()
            ) {
                String value = attributes.get(key).toString();
                result.append( key+ ":" +value +"\n");
            }
            return result.toString();
        }
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

    /**
     * 保存bitmap到图库
     * @param bitmap
     */
    private int saveImageToGallery(Bitmap bitmap) {
        //生成路径
//        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String dirName = "erweima16";
//        File appDir = new File(root , dirName);
//        if (!appDir.exists()) {
//            appDir.mkdirs();
//        }

        String appDir =  SystemDirPath.getPrintScreenPath();

        //文件名为时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String sd = sdf.format(System.currentTimeMillis());
//        String sd =  TimeUtils.getCurrentTime();
        String fileName = sd + ".jpg";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"未获取读取、写入文件权限",Toast.LENGTH_LONG).show();

            return  -1;
        }

        //获取文件
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //文件插入到系统图库
//            try{
//                MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                        file.getAbsolutePath(), fileName, null);
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
            //通知系统相册刷新
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(file.getPath()))));
            return 2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 获取读取写入权限
     */
//    private void requestReadExternalStoragePermission() {
//        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            new CameraActivity.ReadExternalStoragePermissionDialog().show(getSupportFragmentManager(), "dialog");
//        } else {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//        }
//    }

    /**
     * 截图
     */
    private Bitmap getMapViewBitmap() {
        MapView v = mMapView;
        v.clearFocus();
        v.setPressed(false);
        //能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while(cacheBitmap == null){
            final ListenableFuture<Bitmap> export = v.exportImageAsync();
            try {
                cacheBitmap =export.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
}
