package com.yzx.frames.tool.func;

import java.io.DataOutputStream;

import android.content.Context;
import android.os.Handler;

public class CommandTool {

	public static void execWithSU(Context context, final String command, final Runnable success, final Runnable error) {
		final Handler handler = new Handler(context.getMainLooper());
		new Thread() {
			public void run() {
				handler.post(commandWithSU(command) ? success : error);
			}
		}.start();
	}

	public static void exec(Context context, final String command, final Runnable success, final Runnable error) {
		final Handler handler = new Handler(context.getMainLooper());
		new Thread() {
			public void run() {
				handler.post(command(command) ? success : error);
			}
		}.start();
	}

	private static boolean command(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(command + "\n");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null)
					os.close();
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	private static boolean commandWithSU(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null)
					os.close();
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

}
