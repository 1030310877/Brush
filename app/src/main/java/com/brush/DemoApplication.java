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
        BrushOptions options = new BrushOptions();
        options.setDiskCache(true);
        Brush.setOptions(options);
    }
}
