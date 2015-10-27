package joe.brush.engine;

import android.os.Handler;
import android.widget.ImageView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import joe.brush.config.BrushOptions;
import joe.brush.task.LoadTask;

/**
 * Description 线程加载控制类
 * Created by chenqiao on 2015/10/27.
 */
public class LoadEngine {
    private BrushOptions brushOptions;
    private Handler handler;
    private final Map<Integer, String> imageViewManager = Collections.synchronizedMap(new HashMap<Integer, String>());
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final Object pauseLock = new Object();

    // 线程池
    private ExecutorService localThreadPool;
    private Executor downloadThreadPool;

    public LoadEngine(BrushOptions options, Handler mUIHandler) {
        this.brushOptions = options;
        this.handler = mUIHandler;
        localThreadPool = Executors.newCachedThreadPool();

        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        downloadThreadPool = new ThreadPoolExecutor(brushOptions.threadCount, brushOptions.maxthreadCount, 0L, TimeUnit.MILLISECONDS, taskQueue);
    }

    public void recordImageView(ImageView imageView, String imagePath) {
        imageViewManager.put(imageView.hashCode(), generateKey(imagePath));
    }

    public void removeImageView(ImageView imageView) {
        imageViewManager.remove(imageView.hashCode());
    }

    public String getImageViewString(ImageView imageView) {
        return imageViewManager.get(imageView.hashCode());
    }

    public void execute(final LoadTask task) {
        localThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                downloadThreadPool.execute(task);
            }
        });
    }

    public AtomicBoolean getPaused() {
        return paused;
    }

    public void pause() {
        paused.set(true);
    }

    public Object getPauseLock() {
        return pauseLock;
    }

    public void resume() {
        paused.set(false);
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    public void stop() {
        if (localThreadPool != null) {
            localThreadPool.shutdownNow();
        }
        imageViewManager.clear();
    }

    public void setBrushOptions(BrushOptions brushOptions) {
        this.brushOptions = brushOptions;
    }

    public String generateKey(String imageUri) {
        return imageUri + "_" + brushOptions.maxImageWidth + "x" + brushOptions.maxImageHeight;
    }
}
