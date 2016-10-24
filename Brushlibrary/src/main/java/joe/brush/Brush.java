package joe.brush;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import joe.brush.bean.ImageObject;
import joe.brush.config.BrushOptions;
import joe.brush.engine.LoadEngine;
import joe.brush.listener.OnPaintListener;
import joe.brush.task.LoadTask;
import joe.brush.util.CacheManager;

/**
 * Description 图像异步加载类
 * Created by chenqiao on 2015/10/26.
 */
public class Brush {

    public static final int LOAD_IMAGE = 0xaa;

    private static Brush instance;
    private static boolean isSetOption = false;

    public static Brush getInstance() {
        if (isSetOption && instance != null) {
            return instance;
        } else {
            isSetOption = true;
            return new Brush(new BrushOptions());
        }
    }

    public static Brush setOptions(BrushOptions options) {
        if (instance == null) {
            synchronized (Brush.class) {
                if (instance == null) {
                    instance = new Brush(options);
                    isSetOption = true;
                }
            }
        }
        return instance;
    }

    // 加载引擎类
    private LoadEngine engine;

    // 配置类
    private BrushOptions brushOptions;

    //  缓存管理类
    private CacheManager cacheManager;

    // UI线程
    private Handler mUIHandler;

    private Brush(BrushOptions options) {
        brushOptions = options;
        cacheManager = CacheManager.getInstance(brushOptions);

        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LOAD_IMAGE) {
                    ImageObject imageObject = (ImageObject) msg.obj;
                    Bitmap bm = imageObject.bm;
                    ImageView imgView = imageObject.imageView;
                    String path = imageObject.path;
                    if (engine.generateKey(path).equals(engine.getImageViewString(imgView))) {
                        imgView.setImageBitmap(bm);
                        engine.removeImageView(imgView);    //移除ImageView的记录
                        if (msg.arg1 == 1) {
                            if (imageObject.listener != null) {
                                imageObject.listener.onPaintSucceed(imgView);
                            }
                        } else {
                            if (imageObject.listener != null) {
                                imageObject.listener.onPaintFailed(imgView);
                            }
                        }
                    } else {
                        if (bm != null) {
                            bm.recycle();
                            bm = null;
                        }
                    }
                }
            }
        };
        engine = new LoadEngine(brushOptions);
    }

    public void paintImage(String path, ImageView imageView) {
        paintImage(path, imageView, null);
    }

    public void paintImage(String path, ImageView imageView, OnPaintListener listener) {
        engine.recordImageView(imageView, path);
        if (brushOptions.getLoadingShowpic() == 0) {
            imageView.setImageResource(R.drawable.pic_loading);
        } else {
            imageView.setImageResource(brushOptions.getLoadingShowpic());
        }

        if (listener != null) {
            listener.onPaintStart(imageView);
        }
        //  从缓存中读取Bitmap
        Bitmap bm = cacheManager.getBitmapFromLruCache(path);
        if (bm != null) {
            if (!bm.isRecycled()) {
                ImageObject imageBean = new ImageObject(bm, path, imageView, listener);
                Message msg = mUIHandler.obtainMessage(Brush.LOAD_IMAGE);
                msg.obj = imageBean;
                msg.arg1 = 1;
                mUIHandler.sendMessage(msg);
            } else {
                cacheManager.removeBitmapFromLruCache(path);
                LoadTask task = new LoadTask(engine, path, imageView, listener, brushOptions, cacheManager, mUIHandler);
                engine.execute(task);
            }
        } else {
            LoadTask task = new LoadTask(engine, path, imageView, listener, brushOptions, cacheManager, mUIHandler);
            engine.execute(task);
        }
    }

    public BrushOptions getBrushOptions() {
        return brushOptions;
    }

    public void pauseLoad() {
        engine.pause();
    }

    public void resumeLoad() {
        engine.resume();
    }

    public void stopLoad() {
        engine.stop();
    }

    public void resetSelf() {
        instance = null;
        isSetOption = false;
    }
}