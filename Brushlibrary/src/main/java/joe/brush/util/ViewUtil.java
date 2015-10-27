package joe.brush.util;

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import joe.brush.bean.ImageSize;

/**
 * Description
 * Created by chenqiao on 2015/10/26.
 */
public class ViewUtil {

    /**
     * 获取ImageView的宽和高
     *
     * @param imageView
     * @return
     */
    public static ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics display = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();

        int width = imageView.getWidth();
        if (width <= 0) {
            width = imageView.getMeasuredWidth();
        }
        if (width <= 0 && lp != null) {
            width = lp.width;
        }
        if (width <= 0) {
            width = display.widthPixels;
        }

        int height = imageView.getHeight();
        if (height <= 0) {
            height = imageView.getMeasuredHeight();
        }
        if (height <= 0 && lp != null) {
            height = lp.height;
        }
        if (height <= 0) {
            height = display.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }
}
