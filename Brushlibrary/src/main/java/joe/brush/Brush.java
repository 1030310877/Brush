package joe.brush;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.URLUtil;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import joe.brush.bean.ImageObject;
import joe.brush.config.BrushOptions;
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

    // 配置类
    private BrushOptions brushOptions;

    //  缓存管理类
    private CacheManager cacheManager;

    // 线程池
    private ExecutorService threadPool;
    // 任务链
    private LinkedList<Runnable> taskQueue;
    // PV池
    private Semaphore semaphoreThreadPool;
    private Semaphore initHandler = new Semaphore(0);

    // 任务监听线程
    private Thread mPoolThread;
    private Handler threadHandler;

    // UI线程
    private Handler mUIHandler;

    private Brush() {
        brushOptions = new BrushOptions();

        cacheManager.getInstance(brushOptions);
        threadPool = Executors.newFixedThreadPool(brushOptions.threadCount);
        taskQueue = new LinkedList<>();
        semaphoreThreadPool = new Semaphore(brushOptions.threadCount);

        initBackThread();
    }

    private void initBackThread() {
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                threadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == DO_TASK) {
                            try {
                                semaphoreThreadPool.acquire();
                                threadPool.execute(getTask());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                initHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();
    }

    public void paintImage(String path, final ImageView imageView) {
        imageView.setTag(path);

        boolean isNet = false;
        if (URLUtil.isNetworkUrl(path)) {
            isNet = true;
        }

        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == LOAD_IMAGE) {
                        ImageObject imageObject = (ImageObject) msg.obj;
                        Bitmap bm = imageObject.bm;
                        ImageView imgView = imageObject.imageView;
                        String path = imageObject.path;
                        if (imgView.getTag().toString().equals(path)) {
                            imgView.setImageBitmap(bm);
                        }
                    }
                }
            };
        }

        //  从缓存中读取Bitmap
        Bitmap bm = cacheManager.getBitmapFromLruCache(path);

        if (bm == null) {
            //  内存缓存中不存在，从存储获取或下载
            addTask(new LoadTask(path, imageView, brushOptions, cacheManager, mUIHandler));
        } else {
            ImageObject imageBean = new ImageObject(bm, path, imageView);
            Message msg = mUIHandler.obtainMessage(LOAD_IMAGE);
            msg.obj = imageBean;
            mUIHandler.sendMessage(msg);
        }
    }

    /**
     * 添加读取任务
     */
    private synchronized void addTask(LoadTask loadTask) {
        taskQueue.add(loadTask);
        try {
            if (threadHandler == null) {
                initHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadHandler.sendEmptyMessage(DO_TASK);
    }

    private Runnable getTask() {
        return taskQueue.removeLast();
    }

    public BrushOptions getBrushOptions() {
        return brushOptions;
    }

    public Brush setBrushOptions(BrushOptions brushOptions) {
        this.brushOptions = brushOptions;
        cacheManager.setOptions(brushOptions);
        return instance;
    }
}