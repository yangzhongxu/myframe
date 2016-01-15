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
		// TODO ��ӱ��� ��Ӧ�ַ���...
	}

	/** ��ӱ���ƥ���ַ��Ͷ�ӦͼƬ��Դ��map */
	public static void addPatternToMap(String pattern, int faceRes, Map<Pattern, Integer> map) {
		map.put(Pattern.compile(Pattern.quote(pattern)), faceRes);
	}

	/** ������Դid��ȡImageSpan */
	public static ImageSpan getFaceImageSpan(Context context, int res) {
		// TODO �����滻��ImageLoader
		Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), res), 50, 50, true);
		return new ImageSpan(context, bmp);
	}

	/** ��SpannableString�еĶ�Ӧ�ַ��滻�ɱ��� */
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

	/** ����ͨ��Stringת���ɴ������SpannableString */
	public static Spannable getFaceSpannableString(Context context, Map<Pattern, Integer> map, String inputTxt) {
		SpannableString spannable = new SpannableString(inputTxt);
		replaceFace(context, spannable, map);
		return spannable;
	}

}
