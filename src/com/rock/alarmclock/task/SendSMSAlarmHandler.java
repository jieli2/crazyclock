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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SendSMSAlarmHandler implements AlarmTaskHandler {
	private Activity activity;
	private Handler handler = null;

	private String phoneNumber = "";
	private String msg = "Good Morning";
	private String finishMsg = "";
	public void execute(Activity ctx, String data, Handler h) {
		handler = h;
		try {
			JSONObject obj = new JSONObject(data);
			if (obj.has("phone")) phoneNumber = obj.getString("phone");
			
			if (obj.has("msg")) msg = obj.getString("msg");
			
			SmsManager smsManager = SmsManager.getDefault();   
			
			finishMsg = "消息已发送给: " + phoneNumber; 
			finishCallback();
			smsManager.sendTextMessage(phoneNumber, null, msg, null, null);    

		} catch (Exception e) {
			Log.e("error", e.getMessage());
		}

	}
	public void pause() {
		
	}
	
	public void resume(){
		
		
	}
	public void finishCallback() {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("msg", finishMsg);

		b.putString("status", "done");
		message.setData(b);
		message.what = 0;
		if(handler != null) handler.sendMessage(message);
		
		
	}
	
	public void finish(Activity ctx, String data, Handler handler) {
		
	}
}
