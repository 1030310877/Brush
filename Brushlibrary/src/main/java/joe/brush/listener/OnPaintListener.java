package joe.brush.listener;

import android.widget.ImageView;

/**
 * Description
 * Created by chenqiao on 2015/10/30.
 */
public interface OnPaintListener {
    void onPaintStart(ImageView imageView);

    void onPaintSucceed(ImageView imageView);

    void onPaintFailed(ImageView imageView);
}
