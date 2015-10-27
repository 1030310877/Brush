package joe.brush;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

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

    // 任务链
    private LinkedList<Runnable> taskQueue;
    // PV池
    private Semaphore semaphoreThreadPool;

    // UI线程
    private Handler mUIHandler;

    private Brush() {
        brushOptions = new BrushOptions();
        engine = new LoadEngine(brushOptions);
        cacheManager = CacheManager.getInstance(brushOptions);
        taskQueue = new LinkedList<>();
        semaphoreThreadPool = new Semaphore(brushOptions.threadCount);

        mUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LOAD_IMAGE) {
                    ImageObject imageObject = (ImageObject) msg.obj;
                    Bitmap bm = imageObject.bm;
                    ImageView imgView = imageObject.imageView;
                    String path = imageObject.path;
                    imgView.setImageBitmap(bm);
                    engine.removeImageView(imgView);    //移除ImageView的记录
                    semaphoreThreadPool.release();
                }
            }
        };
    }

    public void paintImage(String path, final ImageView imageView) {
        engine.recordImageView(imageView, path);
        imageView.setImageResource(R.mipmap.pic_loading);
        addTask(new LoadTask(engine, path, imageView, brushOptions, cacheManager, mUIHandler));
    }

    /**
     * 添加读取任务
     */
    private synchronized void addTask(LoadTask loadTask) {
        taskQueue.add(loadTask);
        engine.execute(getTask());
    }

    private Runnable getTask() {
        return taskQueue.removeFirst();
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

}