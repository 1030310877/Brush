package joe.brush.listener;

import android.widget.AbsListView;

import joe.brush.Brush;

/**
 * Description
 * Created by chenqiao on 2015/10/27.
 */
public class PauseOnScrollListener implements AbsListView.OnScrollListener {

    private AbsListView.OnScrollListener normalOnScrollListener;
    private Brush brush;
    private boolean isPauseOnFling;

    public PauseOnScrollListener(Brush brush, boolean pauseOnFling) {
        this(brush, pauseOnFling, null);
    }

    public PauseOnScrollListener(Brush brush, boolean pauseOnFling, AbsListView.OnScrollListener listener) {
        this.brush = brush;
        this.isPauseOnFling = pauseOnFling;
        this.normalOnScrollListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int newState) {
        switch (newState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                brush.resumeLoad();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                brush.pauseLoad();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                if (isPauseOnFling) {
                    brush.pauseLoad();
                }
                break;
        }
        if (normalOnScrollListener != null) {
            normalOnScrollListener.onScrollStateChanged(absListView, newState);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (normalOnScrollListener != null) {
            normalOnScrollListener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
