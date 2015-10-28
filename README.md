# Brush
##轻量级图片异步加载框架
参考[鸿洋大神](http://blog.csdn.net/lmj623565791/article/details/41874561)CSDN上文章以及著名开源图像异步加载框架Android-Universal-Image-Loader编写。
可加载本地和网络图片。
使用图片压缩机制，尽可能避免OOM。拥有两级缓存机制，内存缓存和磁盘缓存。

![](https://github.com/1030310877/Brush/blob/master/Gif/demo.gif)
##使用说明
推荐在Application中使用Brush.setOptions(new BrushOption())进行初始化。
初始化后，一行代码实现图像异步加载。
```
 Brush.getInstance().paintImage(imgUrl, imageView);
```
如果需要重新设置BrushOption，使用Brush.resetSelf()后，重新使用setOption方法。
##Wiki
###Brush
```
public static Brush setOptions(BrushOptions options)
```
初始化Brush类，使用Brush前务必调用此方法。

```
public void paintImage(String path, final ImageView imageView)
```
异步加载图像方法
path：图像地址，可以是网络地址，也可以是存储中文件的绝对地址。
imageView：需要加载图像的View

```
public void pauseLoad()
```
停止当前的加载任务,不再接受新任务加入

```
public void resumeLoad()
```
恢复接受新任务的加入

```
public void stopLoad()
```
终止当前加载任务，不能恢复

```
public void resetSelf()
```
重置Brush类

###BrushOptions（框架配置类）
```
public BrushOptions setThreadCount(int num, int maxNum)
```
设置同时工作的线程数量，以及最大值

```
public BrushOptions setDiskCache(boolean tf)
```
设置是否开启磁盘缓存，默认开启。

```
public BrushOptions setDiskCachePath(String path)
```
设置磁盘缓存的路径，默认为内部存储（sd卡）/Android/data/应用包名/cache中

```
public BrushOptions setLoadingShowPic(int resId)
```
设置加载时候显示的图片资源

```
public BrushOptions setErrorShowPic(int resId)
```
设置加载失败时显示的图片资源

###PauseOnScrollListener  PauseRecycleViewOnScrollListener
分别继承AbsListView.OnScrollListener和RecyclerView.OnScrollListener。
实现可以滑动的view，滑动时停止加载，滑动完成恢复加载。
