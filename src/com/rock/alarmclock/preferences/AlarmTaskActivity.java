package com.rock.alarmclock.preferences;

import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.rock.alarmclock.main.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rock.alarmclock.main.Alarm;
import com.rock.alarmclock.utils.ChannelConfig;
import com.umeng.analytics.MobclickAgent;

public class AlarmTaskActivity extends Activity {
	private Alarm alarm = null;
	ImageButton saveButton = null;
	private String taskTypes[] = { "²¥·Å¹ã²¥", "·¢ËÍÏûÏ¢", "CALLÄ³ÈË", "Â¼Òô60Ãë", "Â¼Ïñ30Ãë",
			"ÅÄÕÕ" };
	private ListView channelList;
	private LazyAdapter lazyAdapter;
	private String taskType = "";
	private String d_channel_name;
	private String d_play_url;
	private String d_phone;
	private String d_sms_msg;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("alarm")) {
			alarm = (Alarm) bundle.getSerializable("alarm");
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarm_task);

		channelList = (ListView) this.findViewById(R.id.channellist);
		channelList.setBackgroundResource(R.color.black);
		lazyAdapter = new LazyAdapter(this);
		channelList.setAdapter(lazyAdapter);
		channelList.setOnItemClickListener(new OnItemClickListener() {

			AlertDialog.Builder alert;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String catName = (String) lazyAdapter.getItem(arg2);
				// Toast.makeText(AlarmTaskActivity.this, catName,
				// Toast.LENGTH_LONG).show();
				final Map<String, String> channels = ChannelConfig.getInstance(
						AlarmTaskActivity.this).getChannels(catName);

				if (channels != null) {
					Set<String> keys = channels.keySet();
					alert = new AlertDialog.Builder(AlarmTaskActivity.this);

					alert.setTitle(catName);

					final CharSequence[] items = new CharSequence[keys.size()];
					int i = 0;
					for (String k : keys) {
						items[i++] = k;
					}

					alert.setItems(items, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String channelName = items[which].toString();
							Toast.makeText(AlarmTaskActivity.this,
									items[which].toString() + " ÒÑÉèÖÃ",
									Toast.LENGTH_LONG).show();
							d_channel_name = channelName;
							d_play_url = (String) channels.get(channelName);
						}

					});

					alert.show();

				}
			}

		});

		Spinner spinner = (Spinner) this.findViewById(R.id.tasktype);
		ArrayAdapter<String> accountTypesAdapter = new ArrayAdapter<String>(
				this, R.layout.spinner, taskTypes);
		accountTypesAdapter.setDropDownViewResource(R.layout.drop_down_item);
		spinner.setAdapter(accountTypesAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			private void hideAll() {
				AlarmTaskActivity.this.findViewById(R.id.broadcast)
						.setVisibility(View.INVISIBLE);

				AlarmTaskActivity.this.findViewById(R.id.sms).setVisibility(
						View.INVISIBLE);

				AlarmTaskActivity.this.findViewById(R.id.dial).setVisibility(
						View.INVISIBLE);
				AlarmTaskActivity.this.findViewById(R.id.audio).setVisibility(
						View.INVISIBLE);

				AlarmTaskActivity.this.findViewById(R.id.video).setVisibility(
						View.INVISIBLE);

				AlarmTaskActivity.this.findViewById(R.id.picture)
						.setVisibility(View.INVISIBLE);
			}

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String type = taskTypes[position];
				taskType = type;
				hideAll();
				TextView tv = (TextView) view;
				// Toast.makeText(AlarmTaskActivity.this,type,
				// Toast.LENGTH_LONG).show();
				if ("²¥·Å¹ã²¥".equalsIgnoreCase(type)) {

					AlarmTaskActivity.this.findViewById(R.id.broadcast)
							.setVisibility(View.VISIBLE);

				} else if ("·¢ËÍÏûÏ¢".equalsIgnoreCase(type)) {

					AlarmTaskActivity.this.findViewById(R.id.sms)
							.setVisibility(View.VISIBLE);
				} else if ("CALLÄ³ÈË".equalsIgnoreCase(type)) {

					AlarmTaskActivity.this.findViewById(R.id.dial)
							.setVisibility(View.VISIBLE);

				} else if ("Â¼Òô60Ãë".equalsIgnoreCase(type)) {

					AlarmTaskActivity.this.findViewById(R.id.audio)
							.setVisibility(View.VISIBLE);

				} else if ("Â¼Ïñ30Ãë".equalsIgnoreCase(type)) {

					AlarmTaskActivity.this.findViewById(R.id.video)
							.setVisibility(View.VISIBLE);
				} else if ("ÅÄÕÕ".equalsIgnoreCase(type)) {

					AlarmTaskActivity.this.findViewById(R.id.picture)
							.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		if(alarm != null && alarm.getExtra() != null && !"".equals(alarm.getExtra())) {
			String extra = alarm.getExtra();
			try {
				JSONObject extraObj = new JSONObject(extra);
				if(extraObj.has("type")) {
					String t = extraObj.getString("type");
					if(t.equals(AlarmTaskType.BROADCAST)) {
						spinner.setSelection(0);
					} else if(t.equals(AlarmTaskType.AUDIO_RECORD)) {

						spinner.setSelection(3);
					} else if(t.equals(AlarmTaskType.CAMERA)) {

						spinner.setSelection(5);
					} else if(t.equals(AlarmTaskType.DIAL)) {

						spinner.setSelection(2);
					} else if(t.equals(AlarmTaskType.SMS)) {

						spinner.setSelection(1);
					} else if(t.equals(AlarmTaskType.VIDEO_RECORD)) {

						spinner.setSelection(4);
					} 
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		final ImageButton saveButton = (ImageButton) findViewById(R.id.save_task);
		saveButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					saveButton.setBackgroundColor(getResources().getColor(
							R.color.holo_blue_light));
					break;
				// TODO save task, show broadcast channel
				case MotionEvent.ACTION_UP:
					v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

					saveAlarm();
					Intent intent = new Intent();
					intent.putExtra("alarm", alarm);
					AlarmTaskActivity.this.setResult(RESULT_OK, intent);
					finish();
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_CANCEL:
					saveButton.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));
					break;
				}
				return true;
			}
		});
	}

	private void saveAlarm() {
		if (alarm != null && taskType != null) {
			JSONObject extraObj = new JSONObject();
			JSONObject data = new JSONObject();
			try {
				if ("²¥·Å¹ã²¥".equalsIgnoreCase(taskType)) {
					extraObj.put("type", AlarmTaskType.BROADCAST);
					extraObj.put("handler",
							"com.rock.alarmclock.task.PlayMusicAlarmHandler");

					data.put("url", d_play_url);
					data.put("channel", d_channel_name);

				} else if ("·¢ËÍÏûÏ¢".equalsIgnoreCase(taskType)) {
					extraObj.put("type", AlarmTaskType.SMS);
					EditText textview = (EditText) this
							.findViewById(R.id.text_sms_to);
					this.d_phone = (String) textview.getText().toString();
					data.put("phone", d_phone);

					EditText textview1 = (EditText) this
							.findViewById(R.id.text_sms_msg);
					this.d_sms_msg = (String) textview1.getText().toString();
					data.put("msg", d_sms_msg);
					extraObj.put("handler",
							"com.rock.alarmclock.task.SendSMSAlarmHandler");
				} else if ("CALLÄ³ÈË".equalsIgnoreCase(taskType)) {
					extraObj.put("type", AlarmTaskType.DIAL);

					extraObj.put("handler", "com.rock.alarmclock.task.DialAlarmHandler");
					EditText textview = (EditText) this
							.findViewById(R.id.text_dial_to);
					this.d_phone = (String) textview.getText().toString();
					data.put("phone", this.d_phone);
				} else if ("Â¼Òô60Ãë".equalsIgnoreCase(taskType)) {
					extraObj.put("type", AlarmTaskType.AUDIO_RECORD);

					extraObj.put("handler",
							"com.rock.alarmclock.task.AudioRecorderAlarmHandler");
				} else if ("Â¼Ïñ30Ãë".equalsIgnoreCase(taskType)) {
					extraObj.put("type", AlarmTaskType.VIDEO_RECORD);

					data.put("mediatype", 2);
					extraObj.put("handler", "com.rock.alarmclock.task.CameraAlarmHandler");
				} else if ("ÅÄÕÕ".equalsIgnoreCase(taskType)) {
					extraObj.put("type", AlarmTaskType.CAMERA);

					extraObj.put("handler", "com.rock.alarmclock.task.CameraAlarmHandler");
				}
				extraObj.put("data", data);
				alarm.setExtra(extraObj.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}

	protected void onResume() {
		super.onResume();

		MobclickAgent.onResume(this);
	}

}
