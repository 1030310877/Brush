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

    private static final int DO_TASK = 0x11;
    public static final int LOAD_IMAGE = 0xaa;

    private static Brush instance;

    public static Brush getInstance() {
        if (instance == null) {
            synchronized (Brush.class) {
                if (instance == null) {
                    instance = new Brush();
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

    private Brush() {
        brushOptions = new BrushOptions();
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
        imageView.setImageResource(R.mipmap.pic_loading);

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

    public synchronized Brush setBrushOptions(BrushOptions brushOptions) {
        this.brushOptions = brushOptions;
        cacheManager.setOptions(brushOptions);
        return instance;
    }

    public void pauseLoad() {
    }
//
//    private boolean waitIfPaused() {
//        AtomicBoolean pause = engine.getPaused();
//        if (pause.get()) {
//            synchronized (engine.getPauseLock()) {
//                if (pause.get()) {
//                    Log.d("Brush", "task is paused");
//                    try {
//                        engine.getPauseLock().wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean isViewReused(String path, ImageView imageView) {
//        String cacehString = engine.getImageViewString(imageView);
//        return !engine.generateKey(path).equals(cacehString);
//    }
}