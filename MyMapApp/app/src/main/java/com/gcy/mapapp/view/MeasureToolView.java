package com.gcy.mapapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.gcy.mapapp.R;
import com.gcy.mapapp.common.Variable;
import com.gcy.mapapp.entity.DrawEntity;
import com.gcy.mapapp.listener.MeasureClickListener;
import com.gcy.mapapp.util.ArcGisMeasure;
import com.gcy.mapapp.util.Util;


public class MeasureToolView extends LinearLayout {
    private Context context;
    private ArcGisMeasure arcgisMeasure;
    private MapView mMapView;
    private LinearLayout measureBgView,measurePrevlayout,measureNextlayout,measureLengthLayout,measureAreaLayout,measureClearLayout,measureEndLayout;
    private ImageView prevImageView,nextImageView,lengthImageView,areaImageView,clearImageView,endImageView;
    private TextView measurePrevText,measureNextText,measureLengthText,measureAreaText,measureClearText,measureEndText;
    private int bgColor,fontColor,measurePrevImage,measureNextImage,measureLengthImage,measureAreaImage,measureClearImage,measureEndImage;
    private int buttonWidth,buttonHeight,fontSize;
    private boolean isHorizontal,showText=false;
    private String measurePrevStr,measureNextStr,measureLengthStr,measureAreaStr,measureClearStr,measureEndStr;
    private Variable.DrawType drawType=null;
    private Variable.Measure measureLengthType=Variable.Measure.M;
    private Variable.Measure measureAreaType=Variable.Measure.M2;
    private DefaultMapViewOnTouchListener mapListener;
    private MeasureClickListener measureClickListener;
    public MeasureToolView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MeasureToolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewAttr);
        isHorizontal=ta.getBoolean(R.styleable.ViewAttr_is_horizontal,false);
        if(!isHorizontal) {
            LayoutInflater.from(context).inflate(R.layout.measure_tool_vertical_view, this);
        }else{
            LayoutInflater.from(context).inflate(R.layout.measure_tool_horizontal_view, this);
        }

        initView();
        initAttr(ta);
    }
    public void init(MapView mMapView,DefaultMapViewOnTouchListener mapListener){
        this.mapListener=mapListener;
        init(mMapView);
    }
    public void init(MapView mMapView, MeasureClickListener measureClickListener){
        this.measureClickListener=measureClickListener;
        init(mMapView);
    }
    public void init(MapView mMapView,MeasureClickListener measureClickListener,DefaultMapViewOnTouchListener mapListener){
        this.measureClickListener=measureClickListener;
        this.mapListener=mapListener;
        init(mMapView);
    }
    public void init(MapView mMapView){
        this.mMapView=mMapView;
        arcgisMeasure=new ArcGisMeasure(context,mMapView);

         DefaultMapViewOnTouchListener listener=new DefaultMapViewOnTouchListener(context,mMapView){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(drawType==Variable.DrawType.LINE) {
                    arcgisMeasure.startMeasuredLength(e.getX(), e.getY());
                }else if(drawType==Variable.DrawType.POLYGON){
                    arcgisMeasure.startMeasuredArea(e.getX(), e.getY());
                }
                if(mapListener!=null){
                    return   mapListener.onSingleTapUp(e);
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(mapListener!=null) {
                    return  mapListener.onDoubleTap(e);
                }
                return super.onDoubleTap(e);
            }

             @Override
             public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                 if(mapListener!=null) {
                     return  mapListener.onScroll(e1, e2, distanceX, distanceY);
                 }
                 return super.onScroll(e1, e2, distanceX, distanceY);
             }

             @Override
             public boolean onRotate(MotionEvent event, double rotationAngle) {
                 if(mapListener!=null) {
                   return  mapListener.onRotate(event, rotationAngle);
                 }
                 return super.onRotate(event, rotationAngle);
             }

             @Override
             public boolean onScale(ScaleGestureDetector detector) {
                 if(mapListener!=null) {
                     return  mapListener.onScale(detector);
                 }
                 return super.onScale(detector);
             }
         };
        mMapView.setOnTouchListener(listener);
    }

    private void initView(){
        prevImageView=(ImageView)findViewById(R.id.measure_prev);
        nextImageView=(ImageView)findViewById(R.id.measure_next);
        lengthImageView=(ImageView)findViewById(R.id.measure_length);
        areaImageView=(ImageView)findViewById(R.id.measure_area);
        clearImageView=(ImageView)findViewById(R.id.measure_clear);
        endImageView=(ImageView)findViewById(R.id.measure_end);
        measureBgView=(LinearLayout)findViewById(R.id.measure_bg);
        measurePrevlayout=(LinearLayout)findViewById(R.id.measure_prev_layout);
        measureNextlayout=(LinearLayout)findViewById(R.id.measure_next_layout);
        measureLengthLayout=(LinearLayout)findViewById(R.id.measure_length_layout);
        measureAreaLayout=(LinearLayout)findViewById(R.id.measure_area_layout);
        measureClearLayout=(LinearLayout)findViewById(R.id.measure_clear_layout);
        measureEndLayout=(LinearLayout)findViewById(R.id.measure_end_layout);
        measurePrevText=(TextView)findViewById(R.id.measure_prev_text);
        measureNextText=(TextView)findViewById(R.id.measure_next_text);
        measureLengthText=(TextView)findViewById(R.id.measure_length_text);
        measureAreaText=(TextView)findViewById(R.id.measure_area_text);
        measureClearText=(TextView)findViewById(R.id.measure_clear_text);
        measureEndText=(TextView)findViewById(R.id.measure_end_text);

        //measurePrevlayout.setVisibility(GONE);
        //measureNextlayout.setVisibility(GONE);

        measurePrevlayout.setOnClickListener(listener);
        measureNextlayout.setOnClickListener(listener);
        measureLengthLayout.setOnClickListener(listener);
        measureAreaLayout.setOnClickListener(listener);
        measureClearLayout.setOnClickListener(listener);
        measureEndLayout.setOnClickListener(listener);
    }

    private void initAttr(TypedArray ta){
        bgColor=ta.getResourceId(R.styleable.ViewAttr_view_background,R.drawable.map_shadow_bg);
        buttonWidth=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_width, Util.valueToSp(getContext(),35));
        buttonHeight=ta.getDimensionPixelSize(R.styleable.ViewAttr_button_height, Util.valueToSp(getContext(),35));
        showText=ta.getBoolean(R.styleable.ViewAttr_show_text,false);
        measurePrevStr=ta.getString(R.styleable.ViewAttr_measure_prev_text);
        measureNextStr=ta.getString(R.styleable.ViewAttr_measure_next_text);
        measureLengthStr=ta.getString(R.styleable.ViewAttr_measure_length_text);
        measureAreaStr=ta.getString(R.styleable.ViewAttr_measure_area_text);
        measureClearStr=ta.getString(R.styleable.ViewAttr_measure_clear_text);
        measureEndStr=ta.getString(R.styleable.ViewAttr_measure_end_text);
        fontColor=ta.getResourceId(R.styleable.ViewAttr_font_color,R.color.gray);
        fontSize=ta.getInt(R.styleable.ViewAttr_font_size,12);
        measurePrevImage=ta.getResourceId(R.styleable.ViewAttr_measure_prev_image,R.drawable.map_measure_prev);
        measureNextImage=ta.getResourceId(R.styleable.ViewAttr_measure_next_image,R.drawable.map_measure_next);
        measureLengthImage=ta.getResourceId(R.styleable.ViewAttr_measure_length_image,R.drawable.map_measure_length);
        measureAreaImage=ta.getResourceId(R.styleable.ViewAttr_measure_area_image,R.drawable.map_measure_area);
        measureClearImage=ta.getResourceId(R.styleable.ViewAttr_measure_clear_image,R.drawable.map_measure_clear);
        measureEndImage=ta.getResourceId(R.styleable.ViewAttr_measure_end_image,R.drawable.map_measure_end);

        setMeasureBackground(bgColor);
        setDpButtonWidth(buttonWidth);
        setDpButtonHeight(buttonHeight);
        setShowText(showText);
        setMeasurePrevStr(measurePrevStr);
        setMeasureNextStr(measureNextStr);
        setMeasureLengthStr(measureLengthStr);
        setMeasureAreaStr(measureAreaStr);
        setMeasureClearStr(measureClearStr);
        setMeasureEndStr(measureEndStr);
        setFontColor(fontColor);
        setFontSize(fontSize);
        setMeasurePrevImage(measurePrevImage);
        setMeasureNextImage(measureNextImage);
        setMeasureLengthImage(measureLengthImage);
        setMeasureAreaImage(measureAreaImage);
        setMeasureClearImage(measureClearImage);
        setMeasureEndImage(measureEndImage);
    }

    private OnClickListener listener=new OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.measure_prev_layout){
                boolean hasPrev=arcgisMeasure.prevDraw();
                if(measureClickListener!=null){
                    measureClickListener.prevClick(hasPrev);
                }
            }else if (i == R.id.measure_next_layout){
                boolean hasNext=arcgisMeasure.nextDraw();
                if(measureClickListener!=null){
                    measureClickListener.nextClick(hasNext);
                }
            }else if (i == R.id.measure_length_layout){
                drawType=Variable.DrawType.LINE;
                arcgisMeasure.endMeasure();
                measureLengthLayout.setBackgroundColor(getResources().getColor(R.color.black_1a));
                measureAreaLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                if(measureClickListener!=null){
                    measureClickListener.lengthClick();
                }
            }else if (i == R.id.measure_area_layout){
                drawType=Variable.DrawType.POLYGON;
                arcgisMeasure.endMeasure();
                measureLengthLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                measureAreaLayout.setBackgroundColor(getResources().getColor(R.color.black_1a));
                if(measureClickListener!=null){
                    measureClickListener.areaClick();
                }
            }else if (i == R.id.measure_clear_layout){
                drawType=null;
                DrawEntity draw=arcgisMeasure.clearMeasure();
                measureLengthLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                measureAreaLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                if(measureClickListener!=null){
                    measureClickListener.clearClick(draw);
                }
            }else if (i == R.id.measure_end_layout){
                drawType=null;
                DrawEntity draw=arcgisMeasure.endMeasure();
                measureLengthLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                measureAreaLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                if(measureClickListener!=null){
                    measureClickListener.endClick(draw);
                }
            }
        }
    };


    private void setDpButtonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
        prevImageView.getLayoutParams().width=buttonWidth;
        nextImageView.getLayoutParams().width=buttonWidth;
        lengthImageView.getLayoutParams().width=buttonWidth;
        areaImageView.getLayoutParams().width=buttonWidth;
        clearImageView.getLayoutParams().width=buttonWidth;
        endImageView.getLayoutParams().width=buttonWidth;
    }

    private void setDpButtonHeight(int buttonHeight) {
        this.buttonHeight = buttonHeight;
        prevImageView.getLayoutParams().height=buttonHeight;
        nextImageView.getLayoutParams().height=buttonHeight;
        lengthImageView.getLayoutParams().height=buttonHeight;
        areaImageView.getLayoutParams().height=buttonHeight;
        clearImageView.getLayoutParams().height=buttonHeight;
        endImageView.getLayoutParams().height=buttonHeight;
    }
    public void setSpatialReference(SpatialReference spatialReference) {
        arcgisMeasure.setSpatialReference(spatialReference);
    }

    public void setLengthType(Variable.Measure type){
        this.measureLengthType=type;
        arcgisMeasure.setLengthType(type);
    }
    public void setAreaType(Variable.Measure type){
        this.measureAreaType=type;
        arcgisMeasure.setAreaType(type);
    }
    public void setMeasureBackground(int bg) {
        this.bgColor=bgColor;
        measureBgView.setBackground(getResources().getDrawable(bg));
    }

    public void setButtonWidth(int buttonWidth) {
        setDpButtonWidth(Util.valueToSp(getContext(),buttonWidth));
    }

    public void setButtonHeight(int buttonHeight) {
        setDpButtonHeight(Util.valueToSp(getContext(),buttonHeight));
    }

    public void setShowText(boolean showText){
        this.showText=showText;
        int view=showText?View.VISIBLE:View.GONE;
        measurePrevText.setVisibility(view);
        measureNextText.setVisibility(view);
        measureLengthText.setVisibility(view);
        measureAreaText.setVisibility(view);
        measureClearText.setVisibility(view);
        measureEndText.setVisibility(view);
    }

    public void setMeasurePrevStr(String measurePrevStr) {
        if(measurePrevStr==null) return;
        this.measurePrevStr = measurePrevStr;
        measurePrevText.setText(measurePrevStr);
    }

    public void setMeasureNextStr(String measureNextStr) {
        if(measureNextStr==null) return;
        this.measureNextStr = measureNextStr;
        measureNextText.setText(measureNextStr);
    }

    public void setMeasureLengthStr(String measureLengthStr) {
        if(measureLengthStr==null) return;
        this.measureLengthStr = measureLengthStr;
        measureLengthText.setText(measureLengthStr);
    }

    public void setMeasureAreaStr(String measureAreaStr) {
        if(measureAreaStr==null) return;
        this.measureAreaStr = measureAreaStr;
        measureAreaText.setText(measureAreaStr);
    }

    public void setMeasureClearStr(String measureClearStr) {
        if(measureClearStr==null) return;
        this.measureClearStr = measureClearStr;
        measureClearText.setText(measureClearStr);
    }

    public void setMeasureEndStr(String measureEndStr) {
        if(measureEndStr==null) return;
        this.measureEndStr = measureEndStr;
        measureEndText.setText(measureEndStr);
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
        int color = getResources().getColor(fontColor);
        measurePrevText.setTextColor(color);
        measureNextText.setTextColor(color);
        measureLengthText.setTextColor(color);
        measureAreaText.setTextColor(color);
        measureClearText.setTextColor(color);
        measureEndText.setTextColor(color);
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        measurePrevText.setTextSize(fontSize);
        measureNextText.setTextSize(fontSize);
        measureLengthText.setTextSize(fontSize);
        measureAreaText.setTextSize(fontSize);
        measureClearText.setTextSize(fontSize);
        measureEndText.setTextSize(fontSize);
    }

    public void setMeasurePrevImage(int measurePrevImage) {
        this.measurePrevImage = measurePrevImage;
        prevImageView.setImageDrawable(getResources().getDrawable(measurePrevImage));
    }

    public void setMeasureNextImage(int measureNextImage) {
        this.measureNextImage = measureNextImage;
        nextImageView.setImageDrawable(getResources().getDrawable(measureNextImage));
    }

    public void setMeasureLengthImage(int measureLengthImage) {
        this.measureLengthImage = measureLengthImage;
        lengthImageView.setImageDrawable(getResources().getDrawable(measureLengthImage));
    }

    public void setMeasureAreaImage(int measureAreaImage) {
        this.measureAreaImage = measureAreaImage;
        areaImageView.setImageDrawable(getResources().getDrawable(measureAreaImage));
    }

    public void setMeasureClearImage(int measureClearImage) {
        this.measureClearImage = measureClearImage;
        clearImageView.setImageDrawable(getResources().getDrawable(measureClearImage));
    }

    public void setMeasureEndImage(int measureEndImage) {
        this.measureEndImage = measureEndImage;
        endImageView.setImageDrawable(getResources().getDrawable(measureEndImage));
    }

}
