package joe.brush.listener;

import android.support.v7.widget.RecyclerView;

import joe.brush.Brush;

/**
 * Description
 * Created by chenqiao on 2015/10/27.
 */
public class PauseRecycleViewOnScrollListener extends RecyclerView.OnScrollListener {

    private RecyclerView.OnScrollListener normalOnScrollListener;
    private Brush brush;
    private boolean pauseOnScrolling;

    public PauseRecycleViewOnScrollListener(Brush brush, boolean pauseOnScrolling) {
        this(brush, pauseOnScrolling, null);
    }

    public PauseRecycleViewOnScrollListener(Brush brush, boolean pauseOnScrolling, RecyclerView.OnScrollListener listener) {
        this.brush = brush;
        this.pauseOnScrolling = pauseOnScrolling;
        this.normalOnScrollListener = listener;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                brush.resumeLoad();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                brush.pauseLoad();
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                if (pauseOnScrolling) {
                    brush.pauseLoad();
                }
                break;
        }
        if (normalOnScrollListener != null) {
            normalOnScrollListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (normalOnScrollListener != null) {
            normalOnScrollListener.onScrolled(recyclerView, dx, dy);
        }
    }
}
