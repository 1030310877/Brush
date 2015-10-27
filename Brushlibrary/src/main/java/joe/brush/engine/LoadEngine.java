package joe.brush.engine;

import android.widget.ImageView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import joe.brush.config.BrushOptions;

/**
 * Description 线程加载控制类
 * Created by chenqiao on 2015/10/27.
 */
public class LoadEngine {

    private BrushOptions brushOptions;
    private final Map<Integer, String> imageViewManager = Collections.synchronizedMap(new HashMap<Integer, String>());
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final Object pauseLock = new Object();

    // 线程池
    private ExecutorService threadPool;

    public LoadEngine(BrushOptions options) {
        this.brushOptions = options;
        threadPool = Executors.newFixedThreadPool(brushOptions.threadCount);
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

    public void execute(Runnable task) {
        threadPool.execute(task);
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
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        imageViewManager.clear();
    }

    public String generateKey(String imageUri) {
        return imageUri + "_" + brushOptions.maxImageWidth + "x" + brushOptions.maxImageHeight;
    }
}
