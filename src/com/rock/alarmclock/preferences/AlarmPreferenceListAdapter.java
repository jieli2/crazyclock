package com.rock.alarmclock.preferences;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rock.alarmclock.main.Alarm;
import com.rock.alarmclock.preferences.AlarmPreference.Type;


import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmPreferenceListAdapter extends BaseAdapter {

	private Context context;
	private Alarm alarm;
	private List<AlarmPreference> preferences = new ArrayList<AlarmPreference>();
	private final String[] repeatDays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};	
	private final String[] alarmDifficulties = {"Easy","Medium","Hard"};
	
	private String[] alarmTones;
	private String[] alarmTonePaths;
	
	public AlarmPreferenceListAdapter(Context context, Alarm alarm) {
		setContext(context);

		Log.d("AlarmPreferenceListAdapter", "Loading Ringtones...");

		RingtoneManager ringtoneMgr = new RingtoneManager(getContext());

		ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);

		Cursor alarmsCursor = ringtoneMgr.getCursor();

		alarmTones = new String[alarmsCursor.getCount() + 1];
		alarmTones[0] = "Silent";
		alarmTonePaths = new String[alarmsCursor.getCount() + 1];
		alarmTonePaths[0] = "";

		if (alarmsCursor.moveToFirst()) {
			do {
				alarmTones[alarmsCursor.getPosition() + 1] = ringtoneMgr
						.getRingtone(alarmsCursor.getPosition()).getTitle(
								getContext());
				alarmTonePaths[alarmsCursor.getPosition() + 1] = ringtoneMgr
						.getRingtoneUri(alarmsCursor.getPosition()).toString();
			} while (alarmsCursor.moveToNext());
		}
		Log.d("AlarmPreferenceListAdapter", "Finished Loading "
				+ alarmTones.length + " Ringtones.");
		alarmsCursor.close();

	    setMathAlarm(alarm);		
	}

	@Override
	public int getCount() {
		return preferences.size();
	}

	@Override
	public Object getItem(int position) {
		return preferences.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public View inflateTaskView(AlarmPreference alarmPreference, View convertView, ViewGroup parent) {
		String extra = (String) alarmPreference.getValue();

		String type = AlarmTaskType.BROADCAST;
		String data = "";
		
		if("".equals(extra)) {
			
			
		} else {
			try {
				JSONObject json = new JSONObject(extra);
				if(json.has("type")) type = json.getString("type");
				if(json.has("data")) data = json.getString("data");
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());

		if (null == convertView
				|| convertView.getId() != android.R.layout.simple_list_item_2)
			convertView = layoutInflater.inflate(
					android.R.layout.simple_list_item_2, null);

		TextView text1 = (TextView) convertView
				.findViewById(android.R.id.text1);
		text1.setTextSize(18);
		text1.setText(alarmPreference.getTitle());

		TextView text2 = (TextView) convertView
				.findViewById(android.R.id.text2);
		text2.setText(type);
		return convertView;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlarmPreference alarmPreference = (AlarmPreference) getItem(position);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		switch (alarmPreference.getType()) {
		case BOOLEAN:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_checked)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null);

			CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
			checkedTextView.setText(alarmPreference.getTitle());
			checkedTextView.setChecked((Boolean) alarmPreference.getValue());
			break;
			
		default:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_2)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
			
			TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
			text1.setTextSize(18);
			text1.setText(alarmPreference.getTitle());
			
			TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
			text2.setText(alarmPreference.getSummary());
			break;
		}

		return convertView;
	}

	public Alarm getMathAlarm() {		
		for(AlarmPreference preference : preferences){
			switch(preference.getKey()){
				case ALARM_ACTIVE:
					alarm.setAlarmActive((Boolean) preference.getValue());
					break;
				case ALARM_NAME:
					alarm.setAlarmName((String) preference.getValue());
					break;
				case ALARM_EXTRA:
					alarm.setExtra((String) preference.getValue());
					break;
				case ALARM_TIME:
					alarm.setAlarmTime((String) preference.getValue());
					break;
				case ALARM_DIFFICULTY:
					alarm.setDifficulty(Alarm.Difficulty.valueOf((String)preference.getValue()));
					break;
				case ALARM_TONE:
					alarm.setAlarmTonePath((String) preference.getValue());
					break;
				case ALARM_VIBRATE:
					alarm.setVibrate((Boolean) preference.getValue());
					break;
				case ALARM_REPEAT:
					alarm.setDays((Alarm.Day[]) preference.getValue());
					break;
			}
		}
				
		return alarm;
	}

	private String getAlarmTaskDesc(String type, String data) {
		JSONObject obj = new JSONObject();
		StringBuffer desc = new StringBuffer();

		try {
			if(data != null && !"".equals(data))
				obj = new JSONObject(data);
			if(AlarmTaskType.BROADCAST.equals(type)) {
				desc.append("播放广播 ");
				if(obj.has("channel")) {
					desc.append(" (").append(obj.getString("channel")).append(")");
				} else {
					desc.append(" (").append("CRI轻松调频").append(")");
				}
			} else if(AlarmTaskType.DIAL.equals(type)) {
				desc.append("CALL ");
				if(obj.has("phone")) {
					desc.append(obj.getString("phone"));
				} else {
					desc.append(obj.getString("还没设置"));
				}
				
			} else if(AlarmTaskType.AUDIO_RECORD.equals(type)) {
				desc.append("录音60秒");
			} else if(AlarmTaskType.VIDEO_RECORD.equals(type)) {

				desc.append("录像30秒");
			} else if(AlarmTaskType.SMS.equals(type)) {
				desc.append("发短信给 ");
				if (obj.has("phone")) {
					desc.append(obj.getString("phone"));
				} else {
					desc.append(obj.getString("还没设置"));
				}
			} else if(AlarmTaskType.CAMERA.equals(type)) {

				desc.append("拍照3张");
			} 
			
		} catch (JSONException e) {
			return "";
		}
		
		
		return desc.toString();
	}
	
	public void setMathAlarm(Alarm alarm) {
		this.alarm = alarm;
		preferences.clear();
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_ACTIVE,"有效", null, null, alarm.getAlarmActive(),Type.BOOLEAN));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_NAME, "描述",alarm.getAlarmName(), null, alarm.getAlarmName(), Type.STRING));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TIME, "设置时间",alarm.getAlarmTimeString(), null, alarm.getAlarmTime(), Type.TIME));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_REPEAT, "设置重复",alarm.getRepeatDaysString(), repeatDays, alarm.getDays(),Type.MULTIPLE_LIST));
		
		String extra = alarm.getExtra();
		String type = AlarmTaskType.BROADCAST;
		String data = "";
		if(extra != null && !"".equals(extra)) {
			try {
				JSONObject json = new JSONObject(extra);
				if (json.has("type"))
					type = json.getString("type");
				if (json.has("data"))
					data = json.getJSONObject("data").toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_EXTRA, "闹钟任务", getAlarmTaskDesc(type, data), null, alarm.getExtra(), Type.JSON));
		
		//preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_DIFFICULTY,"解题难度", alarm.getDifficulty().toString(), alarmDifficulties, alarm.getDifficulty(), Type.LIST));
		
//		Uri alarmToneUri = Uri.parse(alarm.getAlarmTonePath());
//		Ringtone alarmTone = RingtoneManager.getRingtone(getContext(),
//				alarmToneUri);
//
//		if (alarmTone instanceof Ringtone
//				&& !alarm.getAlarmTonePath().equalsIgnoreCase("")) {
//			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE,
//					"Ringtone", alarmTone.getTitle(getContext()), alarmTones,
//					alarm.getAlarmTonePath(), Type.LIST));
//		} else {
//			preferences
//					.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE,
//							"Ringtone", getAlarmTones()[0], alarmTones, null,
//							Type.LIST));
//		}
//		
		 preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_VIBRATE, "振动",null, null, alarm.getVibrate(), Type.BOOLEAN));
	}

	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String[] getRepeatDays() {
		return repeatDays;
	}

	public String[] getAlarmDifficulties() {
		return alarmDifficulties;
	}

	public String[] getAlarmTones() {
		return alarmTones;
	}

	public String[] getAlarmTonePaths() {
		return alarmTonePaths;
	}

}
