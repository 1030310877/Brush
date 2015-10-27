package joe.brush.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;

import java.io.File;

import joe.brush.config.BrushOptions;

/**
 * Description  缓存管理类
 * Created by chenqiao on 2015/10/26.
 */
public class CacheManager {

    // 图片缓存
    private LruCache<String, Bitmap> imageCache;
    private static CacheManager instance;

    public void setOptions(BrushOptions options) {
        this.options = options;
    }

    private BrushOptions options;

    private CacheManager() {

    }

    private CacheManager(BrushOptions brushOptions) {
        options = brushOptions;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        imageCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public static CacheManager getInstance(BrushOptions brushOptions) {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager(brushOptions);
                }
            }
        }
        return instance;
    }

    public Bitmap getBitmapFromLruCache(String path) {
        return imageCache.get(path);
    }

    public void addBitmapToLruCache(String path, Bitmap bm) {
        if (imageCache.get(path) == null) {
            imageCache.put(path, bm);
        }
    }

    public File getBitmapFromDiskCache(Context context, String path) {
        String dirPath = null;
        if ("default".equals(options.cachePath)) {
            dirPath = getDiskCacheDir(context) + File.separator;
        } else {
            dirPath = options.cachePath + File.separator;
        }
        String fileName = MD5Helper.MD5Encode(path, "utf-8");
        return new File(dirPath + fileName);
    }

    private String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}