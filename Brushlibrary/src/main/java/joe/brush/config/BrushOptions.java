package joe.brush.config;

/**
 * Description  加载配置类
 * Created by chenqiao on 2015/10/26.
 */
public class BrushOptions {

    //同时加载的线程数量
    public int threadCount = 5;

    //是否开启存储缓存
    public boolean diskCache = true;

    //存储缓存的路径
    public String cachePath = "default";

    public int maxImageWidth = 800;
    public int maxImageHeight = 480;
}
