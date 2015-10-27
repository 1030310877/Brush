package joe.brush.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import joe.brush.bean.ImageSize;

/**
 * Description
 * Created by chenqiao on 2015/10/26.
 */
public class ImageUtil {

    //通过文件路径获取到bitmap
    public static Bitmap getBitmapFromPath(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    /**
     * 获取缩放比例
     */
    public static int getBitmapScaleInSampleSize(BitmapFactory.Options op, int reqWidth, int reqHeight) {
        int width = op.outWidth;
        int height = op.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 从网络获取Bitmap
     */
    public static Bitmap downloadImgByUrl(String urlStr, ImageView imageView) {
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            is.mark(is.available());

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap;

            ImageSize imageSize = ViewUtil.getImageViewSize(imageView);
            opts.inSampleSize = getBitmapScaleInSampleSize(opts, imageSize.width, imageSize.height);

            opts.inJustDecodeBounds = false;
            is.reset();
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            conn.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 下载图片到文件
     */
    public static boolean downloadImageToFile(String urlStr, File file) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            fos = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            while (is.read(bytes) != -1) {
                fos.write(bytes);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 从本地获取图片
     */
    public static Bitmap getBitmapFromLocal(String filePah, ImageView imageView) {
        InputStream is = null;
        try {
            File file = new File(filePah);
            is = new FileInputStream(file);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap;

            ImageSize imageSize = ViewUtil.getImageViewSize(imageView);
            opts.inSampleSize = getBitmapScaleInSampleSize(opts, imageSize.width, imageSize.height);

            opts.inJustDecodeBounds = false;
            is.reset();
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
