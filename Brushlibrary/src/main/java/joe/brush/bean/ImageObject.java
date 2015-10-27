package joe.brush.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Description
 * Created by chenqiao on 2015/10/26.
 */
public class ImageObject {

    public Bitmap bm;

    public String path;

    public ImageView imageView;

    public ImageObject(Bitmap bitmap, String path, ImageView imageView) {
        this.bm = bitmap;
        this.path = path;
        this.imageView = imageView;
    }
}
