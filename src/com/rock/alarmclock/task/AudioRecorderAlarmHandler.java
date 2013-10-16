package com.rock.alarmclock.task;

import java.io.File;
import java.util.Timer;

import org.json.JSONObject;

import com.rock.alarmclock.alert.AlarmAlertActivity;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AudioRecorderAlarmHandler implements AlarmTaskHandler {
	protected Timer imageTakeTimer = null;
	private MediaRecorder mRecorder = null;
	private Handler handler;
	private Activity activity;
	private String publish_msg = "";
	private String running_status = "";
	private static final File DATA_DIR = new File(
			Environment.getExternalStorageDirectory() + "/crazyclock/");

	private File getOutputMediaFile() {

		DATA_DIR.mkdirs();
		return new File(DATA_DIR, System.currentTimeMillis() + ".mp3");
	}

	public void pause() {
	
	}
	public void resume(){
		
		
	}
	private void takeAudio(Activity ctx, JSONObject data) {
		try {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setOutputFile(this.getOutputMediaFile().toString());
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setMaxDuration(60000);
			mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {
					publish_msg = "任务完成，存放路径： "+DATA_DIR+", 解题退出！";
					running_status = "done";
					//mRecorder.stop();
					finishCallback();
				}
			});
			publish_msg = "正在录音，解题退出！";
			running_status = "record";
			mRecorder.prepare();

			finishCallback();
			mRecorder.start();

		} catch (Exception e) {
			if (mRecorder != null) {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}

		}
	}

	public void execute(Activity ctx, String data, Handler h) {
		this.handler = h;
		activity = ctx;
		try {
			JSONObject obj = new JSONObject(data);
			
			this.takeAudio(ctx, obj);
			
		} catch (Exception e) {
			Log.e("error", e.getMessage());
			e.printStackTrace();
		}

	}

	public void finishCallback() {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("msg", publish_msg);
		b.putString("status", running_status);
		message.setData(b);
		message.what = 0;
		if(handler != null) this.handler.sendMessage(message);
		
	}
	public void finish(Activity ctx, String data, Handler handler) {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}

	}
}
