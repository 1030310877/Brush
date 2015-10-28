package joe.brush.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
        return computeSampleSize(op, -1, reqWidth * reqHeight);
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
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
            is.mark(is.available() + 1);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap temp = BitmapFactory.decodeStream(is, null, opts);

            ImageSize imageSize = ViewUtil.getImageViewSize(imageView);
            opts.inSampleSize = getBitmapScaleInSampleSize(opts, imageSize.width, imageSize.height);

            opts.inJustDecodeBounds = false;
            is.reset();
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
            conn.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Brush", "error occured:" + e.getMessage());
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
        Log.d("Brush", "image path:" + filePah);
        InputStream is = null;
        try {
            File file = new File(filePah);
            is = new FileInputStream(file);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap temp = BitmapFactory.decodeStream(is, null, opts);
            is.close();

            ImageSize imageSize = ViewUtil.getImageViewSize(imageView);
            Log.d("Brush", "imageveiw width:" + imageSize.width + "  height:" + imageSize.height);
            opts.inSampleSize = getBitmapScaleInSampleSize(opts, imageSize.width, imageSize.height);
            Log.d("Brush", "inSampleSize:" + opts.inSampleSize);
            opts.inJustDecodeBounds = false;

            is = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
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
