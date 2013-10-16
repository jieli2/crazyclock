package com.rock.alarmclock.task;

import org.json.JSONObject;

import com.rock.alarmclock.alert.AlarmAlertActivity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DialAlarmHandler implements AlarmTaskHandler{
	private Activity activity;
	private String phoneNumber = "";
	private String finishMsg = "";
	private Handler handler = null;
	public void execute(Activity ctx, String data, Handler h) {
		activity = ctx;
		this.handler = h;
		 
		try {
			JSONObject obj = new JSONObject(data);
			if (obj.has("phone"))
				phoneNumber = obj.getString("phone");
			
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneNumber));
			
			finishMsg = "电话已拨出: "+phoneNumber;
			finishCallback();
			ctx.startActivity(intent);
		} catch (Exception e) {
			Log.e("error", e.getMessage());
		}

	}
	public void finishCallback() {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("msg", finishMsg);
		message.setData(b);
		message.what = 0;
		if(handler != null) handler.sendMessage(message);
		
		
		
	}
	public void pause() {
		
	}
	public void resume(){
		
		
	}
	public void finish(Activity ctx, String data, Handler handler) {
		
	}
}
