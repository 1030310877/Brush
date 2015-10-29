package joe.brush.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import joe.brush.bean.ImageSize;
import joe.brush.util.ImageUtil;
import joe.brush.util.ViewUtil;

/**
 * Description  下载图片的任务类
 * Created by chenqiao on 2015/10/28.
 */
public class DownLoadPicTask {

    private String urlStr;
    private ImageView imageView;
    private File file;

    public DownLoadPicTask(String urlStr, ImageView imageView, File file) {
        this.urlStr = urlStr;
        this.imageView = imageView;
        this.file = file;
    }

    /**
     * 从网络获取Bitmap
     */
    public Bitmap downloadImgByUrl() {
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            is.mark(is.available() + 1);  //需要+1，不然会报mark is invalid错误

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap temp = BitmapFactory.decodeStream(is, null, opts);

            ImageSize imageSize = ViewUtil.getImageViewSize(imageView);
            opts.inSampleSize = ImageUtil.getBitmapScaleInSampleSize(opts, imageSize.width, imageSize.height);

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
    public boolean downloadImageToFile() {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Brush", "error occured:" + e.getMessage());
            file.delete();
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
}
