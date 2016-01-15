package com.yzx.frames.tool.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

public class ChatFaceUtil {

	public static final HashMap<Pattern, Integer> FACE_MAP = new HashMap<Pattern, Integer>();

	static {
		// TODO 添加表情 对应字符等...
	}

	/** 添加表情匹配字符和对应图片资源到map */
	public static void addPatternToMap(String pattern, int faceRes, Map<Pattern, Integer> map) {
		map.put(Pattern.compile(Pattern.quote(pattern)), faceRes);
	}

	/** 根据资源id获取ImageSpan */
	public static ImageSpan getFaceImageSpan(Context context, int res) {
		// TODO 可以替换成ImageLoader
		Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), res), 50, 50, true);
		return new ImageSpan(context, bmp);
	}

	/** 将SpannableString中的对应字符替换成表情 */
	public static void replaceFace(Context context, Spannable spannable, Map<Pattern, Integer> map) {
		if (spannable == null || spannable.length() < 1)
			return;
		for (Entry<Pattern, Integer> entry : map.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				ImageSpan[] spans = spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class);
				if (spans.length > 0)
					spannable.removeSpan(spans[0]);
				ImageSpan span = getFaceImageSpan(context, entry.getValue());
				spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	/** 将普通的String转换成带表情的SpannableString */
	public static Spannable getFaceSpannableString(Context context, Map<Pattern, Integer> map, String inputTxt) {
		SpannableString spannable = new SpannableString(inputTxt);
		replaceFace(context, spannable, map);
		return spannable;
	}

}
