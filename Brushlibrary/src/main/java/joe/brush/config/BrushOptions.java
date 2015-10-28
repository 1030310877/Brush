package joe.brush.config;

/**
 * Description  加载配置类
 * Created by chenqiao on 2015/10/26.
 */
public class BrushOptions {

    public int maxthreadCount = 8;

    //同时加载的线程数量
    public int threadCount = 5;

    //是否开启存储缓存
    public boolean diskCache = true;

    //存储缓存的路径
    public String cachePath = "default";

    public int maxImageWidth = 800;
    public int maxImageHeight = 480;

    private int loading_pic = 0;

    private int error_pic = 0;

    public BrushOptions() {

    }

    /**
     * 设置能够同时执行的线程数量
     *
     * @param num
     * @param maxNum
     * @return
     */
    public BrushOptions setThreadCount(int num, int maxNum) {
        threadCount = num;
        maxthreadCount = maxNum;
        return this;
    }

    /**
     * 是否开启磁盘缓存
     *
     * @param tf
     * @return
     */
    public BrushOptions setDiskCache(boolean tf) {
        diskCache = tf;
        return this;
    }

    /**
     * 设置磁盘缓存目录
     *
     * @param path
     * @return
     */
    public BrushOptions setDiskCachePath(String path) {
        cachePath = path;
        return this;
    }

    /**
     * 设置正在加载图片时显示的图片
     *
     * @param resId
     * @return
     */
    public BrushOptions setLoadingShowPic(int resId) {
        loading_pic = resId;
        return this;
    }

    public int getLoadingShowpic() {
        return loading_pic;
    }

    /**
     * 设置加载错误时显示的图片
     *
     * @param resId
     * @return
     */
    public BrushOptions setErrorShowPic(int resId) {
        error_pic = resId;
        return this;
    }

    public int getErrorShowPic() {
        return error_pic;
    }
}