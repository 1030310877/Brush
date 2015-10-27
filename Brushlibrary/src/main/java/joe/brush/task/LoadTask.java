package joe.brush.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import joe.brush.Brush;
import joe.brush.R;
import joe.brush.bean.ImageObject;
import joe.brush.config.BrushOptions;
import joe.brush.engine.LoadEngine;
import joe.brush.util.CacheManager;
import joe.brush.util.ImageUtil;

/**
 * Description
 * Created by chenqiao on 2015/10/26.
 */
public class LoadTask implements Runnable {

    private String imagePath;
    private ImageView mImageView;
    private CacheManager mCacheManager;
    private Handler handler;
    private BrushOptions options;
    private LoadEngine engine;

    public LoadTask(LoadEngine engine, String path, ImageView imageView, BrushOptions brushOptions, CacheManager cacheManager, Handler uiHandler) {
        this.engine = engine;
        imagePath = path;
        mImageView = imageView;
        options = brushOptions;
        mCacheManager = cacheManager;
        handler = uiHandler;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ImageView getmImageView() {
        return mImageView;
    }

    @Override
    public void run() {
        if (waitIfPaused()) {
            return;
        }
        if (mImageView == null || isViewReused(imagePath, mImageView)) {
            return;
        }

        Bitmap bm;

        if (!URLUtil.isNetworkUrl(imagePath)) {
            // 不是网络图片，从尝试从本地加载
            bm = ImageUtil.getBitmapFromLocal(imagePath, mImageView);
            if (bm == null) {
                bm = BitmapFactory.decodeResource(mImageView.getResources(), R.mipmap.load_failed);
            }
        } else {
            //从存储中读取Bitmap
            File file = mCacheManager.getBitmapFromDiskCache(mImageView.getContext(), imagePath);
            if (file.exists()) {
                bm = ImageUtil.getBitmapFromLocal(file.getAbsolutePath(), mImageView);
            } else {
                //缓存文件不存在
                if (options.diskCache) {
                    //保存到存储
                    boolean downloadresult = ImageUtil.downloadImageToFile(imagePath, file);
                    if (downloadresult) {
                        //下载成功
                        bm = ImageUtil.getBitmapFromLocal(file.getAbsolutePath(), mImageView);
                    } else {
                        //下载失败，加载默认失败图片
                        bm = BitmapFactory.decodeResource(mImageView.getResources(), R.mipmap.load_failed);
                    }
                } else {
                    //直接从网络加载
                    bm = ImageUtil.downloadImgByUrl(imagePath, mImageView);
                }
                // 添加图像到内存缓存
                mCacheManager.addBitmapToLruCache(imagePath, bm);
            }
        }
        ImageObject imageBean = new ImageObject(bm, imagePath, mImageView);
        Message msg = handler.obtainMessage(Brush.LOAD_IMAGE);
        msg.obj = imageBean;
        handler.sendMessage(msg);
    }

    private boolean waitIfPaused() {
        AtomicBoolean pause = engine.getPaused();
        if (pause.get()) {
            synchronized (engine.getPauseLock()) {
                if (pause.get()) {
                    Log.d("Brush", "task is paused");
                    try {
                        engine.getPauseLock().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isViewReused(String path, ImageView imageView) {
        String cacehString = engine.getImageViewString(imageView);
        return !engine.generateKey(path).equals(cacehString);
    }
}