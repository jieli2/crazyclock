package com.rock.alarmclock.task;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public interface AlarmTaskHandler {

	public void execute(Activity cxt, String data, Handler handler);
	public void finishCallback();
	public void pause();
	public void resume();
	public void finish(Activity cxt, String data, Handler handler);
}
