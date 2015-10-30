package joe.brush.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

import joe.brush.listener.OnPaintListener;

/**
 * Description
 * Created by chenqiao on 2015/10/26.
 */
public class ImageObject {

    public Bitmap bm;

    public String path;

    public ImageView imageView;

    public OnPaintListener listener;

    public ImageObject(Bitmap bitmap, String path, ImageView imageView, OnPaintListener listener) {
        this.bm = bitmap;
        this.path = path;
        this.imageView = imageView;
        this.listener = listener;
    }
}
