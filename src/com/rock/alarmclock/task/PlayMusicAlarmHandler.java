package com.rock.alarmclock.task;

import org.json.JSONObject;

import com.rock.alarmclock.alert.AlarmAlertActivity;


import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class PlayMusicAlarmHandler implements AlarmTaskHandler {

	private MediaPlayer mediaPlayer = null;
	private Activity activity;
	private String channel = "CRI���ɵ�Ƶ";
	private String msg = "";
	private String running_status = "";
	private Handler handler = null;
	public void execute(Activity ctx, String data, Handler h) {
		activity = ctx;
		this.handler = h;
		if (!LibsChecker.checkVitamioLibs(ctx))
			return;

		mediaPlayer = new MediaPlayer(ctx);
		String url = "mmsh://enmms.chinabroadcast.cn/fm91.5";
		try {
			JSONObject obj = new JSONObject(data);
			if (obj.has("url")) url = obj.getString("url");
			if (obj.has("channel")) channel = obj.getString("channel");
			mediaPlayer.setDataSource(url);
			mediaPlayer.setAudioAmplify((float) 2.0);
			mediaPlayer.prepare();
			msg = "���ڲ���: "+channel+", ����ֹͣ���š�";
			running_status = "play";
			finishCallback();
			mediaPlayer.start();

		} catch (Exception e) {

			msg = channel+"��Ƶ����ʱ�޷�ʹ��, ����ֹͣ��";

			running_status = "fail";
			finishCallback();
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
			Log.e("play music failed", e.getMessage());
		}
		
	}
	
	public void pause() {
		if(mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}
	
	public void resume(){
		if(mediaPlayer != null) {
			mediaPlayer.start();
		}
		
	}
	public void finishCallback() {
//		View v = activity.findViewById(R.id.tableRow000);
//			if(v != null) v.setVisibility(View.VISIBLE);
//		TextView tv = (TextView) activity.findViewById(R.id.textView11);
//		if(tv != null) {
//			tv.setText(channel+" ���ڲ��ţ������˳���");
//		}
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("status", running_status);
		b.putString("msg", msg);
		message.setData(b);
		message.what = 0;
		if(handler != null) handler.sendMessage(message);
		
		
	}
	
	public void finish(Activity ctx, String data, Handler handler) {
	
		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}
}
 