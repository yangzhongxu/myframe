package com.yzx.frames.tool.fm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Fid {

	public static final int EMPTY_ID = -1;

	int id() default EMPTY_ID;

}
