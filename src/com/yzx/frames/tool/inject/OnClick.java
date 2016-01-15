package com.yzx.frames.tool.inject;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.CLASS)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
public @interface OnClick {
	public abstract int[] value();
}
