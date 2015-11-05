package com.brush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import joe.brush.Brush;

/**
 * Description
 * Created by chenqiao on 2015/11/5.
 */
public class SecondActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        img = (ImageView) findViewById(R.id.myimage);
        Brush.getInstance().paintImage("http://img.bimg.126.net/photo/rl0IM2SIJK8jWXgIgxhJsw==/2871889187379358521.jpg", img);
    }
}
