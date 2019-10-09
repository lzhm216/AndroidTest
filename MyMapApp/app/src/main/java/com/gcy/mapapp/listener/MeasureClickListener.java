package com.gcy.mapapp.listener;


import com.gcy.mapapp.entity.DrawEntity;

public interface MeasureClickListener {
    void prevClick(boolean hasPrev);
    void nextClick(boolean hasNext);
    void lengthClick();
    void areaClick();
    void clearClick(DrawEntity draw);
    void endClick(DrawEntity draw);
}
