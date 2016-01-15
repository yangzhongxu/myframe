package com.yzx.frames.tool.func;

import com.yzx.frames.tool.Tool;

public class ThreadTool extends Tool {

	public static Thread executeHigh(Runnable run) {
		Thread thread = new Thread(run);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
		return thread;
	}

	public static Thread executeLow(Runnable run) {
		Thread thread = new Thread(run);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		return thread;
	}

	public static Thread execute(Runnable run) {
		Thread thread = new Thread(run);
		thread.start();
		return thread;
	}

}
