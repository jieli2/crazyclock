package com.rock.alarmclock.task;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import io.vov.vitamio.utils.Log;

public class AlarmTask {

	private String taskData = "";

	private String taskHandler = "";

	private AlarmTaskHandler handler = null;
	
	private Handler finishHandler = null;
	
	private TaskStatus status = TaskStatus.NEW;
	
	
	public enum TaskStatus{		
		NEW,
		RUNNING,
		PAUSE,
		FINISH
	}
	
	public Handler getFinishHandler() {
		return finishHandler;
	}

	public void setFinishHandler(Handler finishHandler) {
		this.finishHandler = finishHandler;
	}

	public String getTaskHandler() {
		return taskHandler;
	}

	public void setTaskHandler(String taskHandler) {
		this.taskHandler = taskHandler;
	}

	public AlarmTask(String data, String handler) {
		this.taskData = data;
		this.taskHandler = handler;
	}

	public TaskStatus getStatus(){
		return status;
	}
	
	public void execute(Activity ctx) {

		if ("".equals(taskHandler))
			return;
		try {
			if (handler == null) {
				handler = (AlarmTaskHandler) Class.forName(taskHandler).newInstance();
			}
			status = TaskStatus.RUNNING;
			handler.execute(ctx, taskData, finishHandler);
		} catch (Exception e) {
			Log.e("catch exception when doing task!", e.getMessage());
		}
	}
	public void pause(Activity ctx) {
		status = TaskStatus.PAUSE;
		if(handler != null) handler.pause();
	}
	
	public void resume(Activity ctx) {

		status = TaskStatus.RUNNING;
		if(handler != null) handler.resume();
	}
	
	public void finish (Activity ctx) {
		status = TaskStatus.FINISH;
		if ("".equals(taskHandler))
			return;
		try {
			if(handler != null) handler.finish(ctx, taskData, finishHandler);
		} catch (Exception e) {
			Log.e("catch exception when doing task!", e.getMessage());
		}
	}
}
