package com.brush;

import android.app.Application;

import joe.brush.Brush;
import joe.brush.config.BrushOptions;

/**
 * Description
 * Created by chenqiao on 2015/10/27.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Brush.setOptions(new BrushOptions().setDiskCache(true));
    }
}
