package com.yzx.frames.tool.func;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;
import com.yzx.frames.tool.Tool;

public class ImageTool extends Tool {

	public static final String Drawable_Header = "drawable://";

	/**
	 * 初始化ImageLoader
	 * 
	 * @param maxFileCount
	 *            最大文件数
	 * @param threadPoolSize
	 *            线程数
	 * @param memoryPer
	 *            最大内存的几分之1
	 * @param diskCacheSize
	 *            sd卡内存的最大空间
	 * @param writeLog
	 *            是否输出log
	 */
	public static void initImageLoader(int maxFileCount, int threadPoolSize, int memoryPer, int diskCacheSize, boolean writeLog) {
		// 内存图片宽度
		int maxMemWidth = getApplication().getResources().getDisplayMetrics().widthPixels / 2;
		// 内存图片高度
		int maxMemHeight = getApplication().getResources().getDisplayMetrics().heightPixels / 4;
		// 占用的最大内存
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / memoryPer);
		// 图片不用的最长存活时间
		int maxAlive = 1000 * 60 * 60 * 24 * 5;
		// 存放图片的dir
		File cacheDir = Tool.getUsefullExternalDir("image-loader");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplication())
				.memoryCacheExtraOptions(maxMemWidth, maxMemHeight).threadPoolSize(threadPoolSize).threadPriority(Thread.MIN_PRIORITY)
				.memoryCacheSize(memoryCacheSize).diskCacheSize(diskCacheSize).diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(maxFileCount).memoryCache(new WeakMemoryCache())
				.diskCache(new LimitedAgeDiscCache(cacheDir, maxAlive)).defaultDisplayImageOptions(getImageOption(0, 0, false, false, 0))
				.imageDownloader(new BaseImageDownloader(getApplication(), 10 * 1000, 30 * 1000)).build();
		ImageLoader.getInstance().init(config);
		L.writeLogs(writeLog);
	}

	/**
	 * 加载image
	 */
	public static void loadImage(DisplayImageOptions op, String uri, ImageView iv) {
		ImageLoader.getInstance().displayImage(uri, iv, op);
	}

	/**
	 * 获取DisplayImageOptions
	 */
	public static DisplayImageOptions getImageOption(int loadingRes, int otherRes, Config config, boolean cacheOnDisk,
			boolean cacheInMemory, int fadeTime) {
		return new DisplayImageOptions.Builder().bitmapConfig(config).cacheInMemory(cacheInMemory).cacheOnDisk(cacheOnDisk)
				.considerExifParams(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).resetViewBeforeLoading(true)
				.showImageForEmptyUri(otherRes).showImageOnFail(otherRes).showImageOnLoading(loadingRes)
				.displayer(new FadeInBitmapDisplayer(fadeTime)).build();
	}

	/**
	 * 获取DisplayImageOptions
	 */
	public static DisplayImageOptions getImageOption(Drawable loadingRes, Drawable otherRes, Config config, boolean cacheOnDisk,
			boolean cacheInMemory, int fadeTime) {
		return new DisplayImageOptions.Builder().bitmapConfig(config).cacheInMemory(cacheInMemory).cacheOnDisk(cacheOnDisk)
				.considerExifParams(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).resetViewBeforeLoading(true)
				.showImageForEmptyUri(otherRes).showImageOnFail(otherRes).showImageOnLoading(loadingRes)
				.displayer(new FadeInBitmapDisplayer(fadeTime)).build();
	}

	/**
	 * 获取DisplayImageOptions,默认Config.RGB_565
	 */
	public static DisplayImageOptions getImageOption(int loading, int other, boolean onDisk, boolean onMemory, int fadeTime) {
		return getImageOption(loading, other, Config.RGB_565, onDisk, onMemory, fadeTime);
	}

	/**
	 * 获取DisplayImageOptions,默认Config.RGB_565,没有加载前后图片
	 */
	public static DisplayImageOptions getImageOption(boolean onDisk, boolean onMemory, int fadeTime) {
		return getImageOption(0, 0, Config.RGB_565, onDisk, onMemory, fadeTime);
	}

	/**
	 * 使用ImageLoader获取drawable
	 * 
	 * @param res
	 *            资源id
	 * @return bitmap or null
	 */
	public static Bitmap getDrawable(int res, boolean cachaInMem) {
		if (!cachaInMem)
			return ImageLoader.getInstance().loadImageSync(Drawable_Header + res);
		DisplayImageOptions option = getImageOption(false, true, 0);
		return ImageLoader.getInstance().loadImageSync(Drawable_Header + res, option);
	}

	/**
	 * 将bitmap压缩到100k以内,并保存到本地
	 * 
	 * @param image
	 *            目标图片bitmap
	 * @param perCutPercent
	 *            每次计算质量减少的百分比
	 * @return 图片对应的file 如果保存失败,返回null;
	 */
	public static File compressImageToNew(Bitmap image, Context context, File target, int perCutPercent) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while ((baos.toByteArray().length >> 10) > 100) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= perCutPercent;
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
			bos.write(baos.toByteArray());
			bos.flush();
			bos.close();
			return target;
		} catch (Exception e) {
			return null;
		}
	}

}
