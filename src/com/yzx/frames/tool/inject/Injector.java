package com.yzx.frames.tool.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class Injector {

	public boolean set_bind_field = true;
	public boolean set_bind_method = true;

	/**
	 * inject for field and method that be annotated
	 * 
	 * @param obj
	 *            Bind Object
	 * @param view
	 *            Content View
	 */
	public void inject(final Object obj, View view) {
		if (set_bind_field) {
			Field[] privatefields = obj.getClass().getDeclaredFields();
			for (Field f : privatefields)
				bindField(obj, f, view);
		}

		if (set_bind_method) {
			Method[] privatemethods = obj.getClass().getDeclaredMethods();
			for (final Method m : privatemethods)
				bindMethod(obj, m, view);
		}
	}

	public void inject(final Activity activity) {
		inject(activity, activity.getWindow().getDecorView());
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////

	/*
	 * bind field
	 */
	private static void bindField(Object obj, Field f, View view) {
		f.setAccessible(true);
		FindView anno = f.getAnnotation(FindView.class);
		if (anno != null)
			try {
				f.set(obj, view.findViewById(anno.value()));
			} catch (Exception e) {
			}
	}

	/*
	 * bind method to view by id
	 */
	private static void bindMethod(final Object obj, final Method m, View view) {
		m.setAccessible(true);
		OnClick oc = m.getAnnotation(OnClick.class);
		if (oc != null)
			try {
				OnClickListener clickListener = new View.OnClickListener() {
					public void onClick(View v) {
						try {
							m.invoke(obj, new Object[] { v });
						} catch (Exception e) {
							try {
								m.invoke(obj, new Object[] {});
							} catch (Exception e2) {
							}
						}
					}
				};
				for (int id : oc.value())
					try {
						view.findViewById(id).setOnClickListener(clickListener);
					} catch (Exception e) {
					}
			} catch (Exception e) {
			}
	}

}
