package joe.brush;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import joe.brush.bean.ImageObject;
import joe.brush.config.BrushOptions;
import joe.brush.engine.LoadEngine;
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
            return null;
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

        mUIHandler = new Handler() {
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
                    } else {
                        if (bm != null) {
                            bm.recycle();
                            bm = null;
                        }
                    }
                }
            }
        };
        engine = new LoadEngine(brushOptions, mUIHandler);
    }

    public void paintImage(String path, final ImageView imageView) {
        engine.recordImageView(imageView, path);
        if (brushOptions.getLoadingShowpic() == 0) {
            imageView.setImageResource(R.mipmap.pic_loading);
        } else {
            imageView.setImageResource(brushOptions.getLoadingShowpic());
        }

        //  从缓存中读取Bitmap
        Bitmap bm = cacheManager.getBitmapFromLruCache(path);
        if (bm != null && !bm.isRecycled()) {
            ImageObject imageBean = new ImageObject(bm, path, imageView);
            Message msg = mUIHandler.obtainMessage(Brush.LOAD_IMAGE);
            msg.obj = imageBean;
            mUIHandler.sendMessage(msg);
        } else {
            LoadTask task = new LoadTask(engine, path, imageView, brushOptions, cacheManager, mUIHandler);
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