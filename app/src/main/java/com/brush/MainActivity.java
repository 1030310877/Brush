package com.brush;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import joe.brush.Brush;
import joe.brush.listener.PauseRecycleViewOnScrollListener;

public class MainActivity extends AppCompatActivity {

    private ImageService imageService;
    private List<HashMap<String, String>> images = new ArrayList<>();

    private RecyclerView recyclerView;

    private ArrayList<String> imagesPaths = new ArrayList<>();

    private String[] imgs = new String[]{"http://img.daimg.com/uploads/allimg/151027/3-15102F020550-L.jpg",
            "http://img.daimg.com/uploads/allimg/151027/3-15102F014480-L.jpg",
            "http://img.daimg.com/uploads/allimg/151027/3-15102F004490-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510262351090-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510262345400-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510262336190-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510262324130-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-151026230R20-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510262235500-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-151026222Z30-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-151026221S90-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510262200370-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261J0390-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261I5550-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261HP00-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261GI30-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261FR50-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261626460-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261615100-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261556420-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261550190-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261531140-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261525520-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261521250-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510261513310-L.jpg",
            "http://img.daimg.com/uploads/allimg/151026/3-1510260002470-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-151025235T50-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-1510252352260-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-151025234F00-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-151025233H70-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-1510252333000-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-151025232U70-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-1510252301180-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-151025224R00-L.jpg",
            "http://img.daimg.com/uploads/allimg/151025/3-151025223U50-L.jpg",
            "http://img.daimg.com/uploads/allimg/151023/3-1510232121220-L.jpg",
            "http://img.daimg.com/uploads/allimg/151023/3-1510231K4560-L.jpg",
            "http://img.daimg.com/uploads/allimg/151023/3-1510231JP90-L.jpg",
            "http://img.daimg.com/uploads/allimg/151023/3-1510231J3350-L.jpg",
            "http://img.daimg.com/uploads/allimg/151023/3-1510231I9390-L.jpg",
            "http://img.daimg.com/uploads/allimg/150918/3-15091P01R50-L.jpg",
            "http://img.daimg.com/uploads/allimg/150918/3-15091P013450-L.jpg",
            "http://img.daimg.com/uploads/allimg/150918/3-15091P009480-L.jpg",
            "http://img.daimg.com/uploads/allimg/150918/3-15091P004520-L.jpg",
            "http://img.daimg.com/uploads/allimg/150917/3-15091H354520-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-1509101533210-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-1509101525010-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-150910151I50-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-1509101511320-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-1509101506010-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-1509101455420-L.jpg",
            "http://img.daimg.com/uploads/allimg/150910/3-1509101451100-L.jpg",
            "http://img.daimg.com/uploads/allimg/150625/3-1506252251370-L.jpg",
            "http://img.daimg.com/uploads/allimg/150625/3-150625224H10-L.jpg",
            "http://img.daimg.com/uploads/allimg/150625/3-1506252243430-L.jpg",
            "http://img.daimg.com/uploads/allimg/150625/3-1506252239510-L.jpg",
            "http://img.daimg.com/uploads/allimg/150625/3-1506252235540-L.jpg",
            "http://img.daimg.com/uploads/allimg/150430/3-1504302355070-L.jpg",
            "http://img.daimg.com/uploads/allimg/150430/3-1504302344440-L.jpg",
            "http://img.daimg.com/uploads/allimg/150430/3-1504302336070-L.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);

        //加载本地图片，如果要测试加载本地图片，将下列注释以及adapter中的注释恢复，并注释网络图片部分
        imageService = new ImageService(this);

        allScan();
        // 获得所有的图片
        images = imageService.getImages();
        if (images.size() > 0) {
            // 将所有的图片显示在listview中
            for (HashMap<String, String> temp : images) {
                imagesPaths.add(temp.get("data"));
            }
        }
        GridLayoutManager glm = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(new MyAdapter());
        //滑动时停止加载
        recyclerView.addOnScrollListener(new PauseRecycleViewOnScrollListener(Brush.getInstance(), true));

        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                finish();//finish后，查看log，不再有get task的log输出，说明停止接受任务
            }
        });
    }

    // 必须在查找前进行全盘的扫描，否则新加入的图片是无法得到显示的(加入对sd卡操作的权限)
    public void allScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView img;

            public ViewHolder(View itemView) {
                super(itemView);
                img = (ImageView) itemView;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imgview = new ImageView(MainActivity.this);
            imgview.setLayoutParams(new ViewGroup.LayoutParams(400, 250));
            return new ViewHolder(imgview);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 加载网络图片
//            Brush.getInstance().paintImage(imgs[position], holder.img, new OnPaintListener() {
//                @Override
//                public void onPaintStart(ImageView imageView) {
//                    Log.d("Demo", "paint start:" + imageView.hashCode());
//                }
//
//                @Override
//                public void onPaintSucceed(ImageView imageView) {
//                    Log.d("Demo", "paint success:" + imageView.hashCode());
//                }
//
//                @Override
//                public void onPaintFailed(ImageView imageView) {
//                    Log.d("Demo", "paint failed:" + imageView.hashCode());
//                }
//            });
            // 加载本地图片
            Brush.getInstance().paintImage(imagesPaths.get(position), holder.img);
        }

        @Override
        public int getItemCount() {
            // 加载网络图片
//            return imgs.length;
            // 加载本地图片
            return imagesPaths.size();
        }
    }
}