package joe.brush.task;

import android.app.Activity;
import android.content.Context;
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
import joe.brush.listener.OnPaintListener;
import joe.brush.util.CacheManager;
import joe.brush.util.ImageUtil;

/**
 * Description  加载图片任务类
 * Created by chenqiao on 2015/10/26.
 */
public class LoadTask implements Runnable {

    private String imagePath;
    private ImageView mImageView;
    private CacheManager mCacheManager;
    private Handler handler;
    private BrushOptions options;
    private LoadEngine engine;
    private OnPaintListener listener;

    public LoadTask(LoadEngine engine, String path, ImageView imageView, OnPaintListener listener, BrushOptions brushOptions, CacheManager cacheManager, Handler uiHandler) {
        this.engine = engine;
        imagePath = path;
        mImageView = imageView;
        this.listener = listener;
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
        Context myContext = mImageView.getContext();
        if (myContext == null) {
            return;
        } else {
            if (myContext instanceof Activity) {
                if (((Activity) myContext).isFinishing()) {
                    return;
                }
            }
        }

        Log.d("Brush", "get task:" + imagePath);
        Bitmap bm;
        Message msg = handler.obtainMessage(Brush.LOAD_IMAGE);

        if (!URLUtil.isNetworkUrl(imagePath)) {
            // 不是网络图片，从尝试从本地加载
            Log.d("Brush", "not valid network url, try to read from disk");
            bm = ImageUtil.getBitmapFromLocal(imagePath, mImageView);
            if (bm == null) {
                bm = BitmapFactory.decodeResource(mImageView.getResources(), R.drawable.load_failed);
                msg.arg1 = 0;
            } else {
                msg.arg1 = 1;
            }
        } else {
            //从存储中读取Bitmap
            File file = mCacheManager.getBitmapFromDiskCache(mImageView.getContext(), imagePath);
            if (file.exists()) {
                Log.d("Brush", "diskCache contains bitmap");
                bm = ImageUtil.getBitmapFromLocal(file.getAbsolutePath(), mImageView);
                // 添加图像到内存缓存
                mCacheManager.addBitmapToLruCache(imagePath, bm);
                msg.arg1 = 1;
            } else {
                //缓存文件不存在
                if (options.diskCache) {
                    Log.d("Brush", "load pic from internet to file");
                    //保存到存储
                    DownLoadPicTask downLoadPicTask = new DownLoadPicTask(imagePath, mImageView, file);
                    boolean downloadResult = downLoadPicTask.downloadImageToFile();
                    if (downloadResult) {
                        Log.d("Brush", "download success");
                        //下载成功
                        bm = ImageUtil.getBitmapFromLocal(file.getAbsolutePath(), mImageView);
                        // 添加图像到内存缓存
                        mCacheManager.addBitmapToLruCache(imagePath, bm);
                        msg.arg1 = 1;
                    } else {
                        Log.d("Brush", "download failed");
                        //下载失败，加载默认失败图片
                        if (options.getErrorShowPic() == 0) {
                            bm = BitmapFactory.decodeResource(mImageView.getResources(), R.drawable.load_failed);
                        } else {
                            bm = BitmapFactory.decodeResource(mImageView.getResources(), options.getErrorShowPic());
                        }
                        msg.arg1 = 0;
                    }
                } else {
                    //直接从网络加载
                    Log.d("Brush", "load pic from internet not to file");
                    DownLoadPicTask downLoadPicTask = new DownLoadPicTask(imagePath, mImageView, null);
                    bm = downLoadPicTask.downloadImgByUrl();
                    // 添加图像到内存缓存
                    if (bm != null) {
                        mCacheManager.addBitmapToLruCache(imagePath, bm);
                        msg.arg1 = 1;
                    } else {
                        if (options.getErrorShowPic() == 0) {
                            bm = BitmapFactory.decodeResource(mImageView.getResources(), R.drawable.load_failed);
                        } else {
                            bm = BitmapFactory.decodeResource(mImageView.getResources(), options.getErrorShowPic());
                        }
                        msg.arg1 = 0;
                    }
                }
            }
        }
        msg.obj = new ImageObject(bm, imagePath, mImageView, listener);
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
        String cacheString = engine.getImageViewString(imageView);
        return !engine.generateKey(path).equals(cacheString);
    }
}