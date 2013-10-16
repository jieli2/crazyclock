package com.rock.alarmclock.alert;

import io.vov.vitamio.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.rock.alarmclock.main.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rock.alarmclock.main.Alarm;
import com.rock.alarmclock.task.AlarmTask;
import com.umeng.analytics.MobclickAgent;

public class AlarmAlertActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {

	private Alarm alarm;
	private MediaPlayer mediaPlayer = null;

	private StringBuilder answerBuilder = new StringBuilder();

	private MathProblem mathProblem;
	private Vibrator vibrator;

	private boolean alarmActive;

	private TextView problemView;
	private TextView answerView;
	private String answerString;
	private Handler handler = null;
	public static SurfaceView surface;
	public static SurfaceHolder surfaceHolder;

	private String taskFinishMsg = "";
	private String taskStatus = "";
	public static Camera camera;
	private AlarmTask task = null;
	private TimerTask timerTask = new TimerTask() {
		public void run() {
			startAlarm();
		}
	};
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.alarm_alert);
	

		Bundle bundle = this.getIntent().getExtras();
		
		alarm = (Alarm) bundle.getSerializable("alarm");

		final ImageButton iv = (ImageButton) AlarmAlertActivity.this.findViewById(R.id.done_image);
		if(iv != null) {
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if("play".equals(taskStatus)) {
						iv.setImageResource(R.drawable.play);
						taskStatus = "pause";
						task.pause(AlarmAlertActivity.this);
						
					} else if("pause".equals(taskStatus)){
						iv.setImageResource(R.drawable.pause);

						taskStatus = "play";
						task.resume(AlarmAlertActivity.this);
					}
					
				}
				
			});
		}
		
		surface = (SurfaceView) this.findViewById(R.id.surfaceview);
		//surface = new SurfaceView(this);
		surfaceHolder = surface.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		this.setTitle(alarm.getAlarmName());

		switch (alarm.getDifficulty()) {
		case EASY:
			mathProblem = new MathProblem(3);
			break;
		case MEDIUM:
			mathProblem = new MathProblem(4);
			break;
		case HARD:
			mathProblem = new MathProblem(5);
			break;
		}

		answerString = String.valueOf(mathProblem.getAnswer());
		if (answerString.endsWith(".0")) {
			answerString = answerString.substring(0, answerString.length() - 2);
		}

		problemView = (TextView) findViewById(R.id.textView1);
		problemView.setText(mathProblem.toString());

		answerView = (TextView) findViewById(R.id.textView2);
		answerView.setText("= ?");

		((Button) findViewById(R.id.Button0)).setOnClickListener(this);
		((Button) findViewById(R.id.Button1)).setOnClickListener(this);
		((Button) findViewById(R.id.Button2)).setOnClickListener(this);
		((Button) findViewById(R.id.Button3)).setOnClickListener(this);
		((Button) findViewById(R.id.Button4)).setOnClickListener(this);
		((Button) findViewById(R.id.Button5)).setOnClickListener(this);
		((Button) findViewById(R.id.Button6)).setOnClickListener(this);
		((Button) findViewById(R.id.Button7)).setOnClickListener(this);
		((Button) findViewById(R.id.Button8)).setOnClickListener(this);
		((Button) findViewById(R.id.Button9)).setOnClickListener(this);
		((Button) findViewById(R.id.Button_clear)).setOnClickListener(this);
		((Button) findViewById(R.id.Button_decimal)).setOnClickListener(this);
		((Button) findViewById(R.id.Button_minus)).setOnClickListener(this);

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.d(getClass().getSimpleName(), "Incoming call: "
							+ incomingNumber);
					try {
						if(mediaPlayer != null) {
							mediaPlayer.pause();
						}
					} catch (IllegalStateException e) {

					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					Log.d(getClass().getSimpleName(), "Call State Idle");
					try {
						if(mediaPlayer != null) {
							mediaPlayer.start();
						}
					} catch (IllegalStateException e) {

					}
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();
		Timer timer = new Timer();
		
		timer.schedule(timerTask, 2000);
		final Runnable runnableUi = new Runnable() {
			@Override
			public void run() {
				View v = AlarmAlertActivity.this.findViewById(R.id.tableRow000);
				if (v != null)
					v.setVisibility(View.VISIBLE);
				TextView tv = (TextView) AlarmAlertActivity.this
						.findViewById(R.id.textView11);
				
				if (tv != null) {
					tv.setText(taskFinishMsg);
				}
				ImageView iv = (ImageView) AlarmAlertActivity.this.findViewById(R.id.done_image);
				if(iv != null) {
					if(taskStatus != null && !"".equalsIgnoreCase(taskStatus)) {
						if("record".equalsIgnoreCase(taskStatus)) {
							iv.setImageResource(R.drawable.record);
						} else if("play".equalsIgnoreCase(taskStatus)) {

							iv.setImageResource(R.drawable.play);
						} else {

							iv.setImageResource(R.drawable.done);
						}
					}
				}
			}
			

		};
		if (handler == null) {
			handler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 0: {
						taskFinishMsg = msg.getData().getString("msg");
						taskStatus = msg.getData().getString("status");
						//post(runnableUi);

						
						updateTaskUI();
						break;
					}
					default:
						break;
					}
				}
			};
		}
		if(alarm.getVibrate()) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(5000);
		}

	}
	
	private synchronized void updateTaskUI() {
		
		View v = AlarmAlertActivity.this.findViewById(R.id.tableRow000);
		if (v != null) {
			
			v.setVisibility(View.VISIBLE);

		} else {

			Log.e("updateui", "updateui v is null");
		}
		TextView tv = (TextView) AlarmAlertActivity.this
				.findViewById(R.id.textView11);
        
        
		if (tv != null) {
			tv.setText(taskFinishMsg);

		} else {

			Log.e("updateui", "updateui tv is null");
		}

		
		//ImageView iv = (ImageView) AlarmAlertActivity.this.findViewById(R.id.done_image);
		ImageButton iv = (ImageButton) AlarmAlertActivity.this.findViewById(R.id.done_image);
		if(iv != null) {
			if(taskStatus != null && !"".equalsIgnoreCase(taskStatus)) {
				if("record".equalsIgnoreCase(taskStatus)) {
					iv.setImageResource(R.drawable.record);
				} else if("play".equalsIgnoreCase(taskStatus)) {

					iv.setImageResource(R.drawable.pause);
				} else {

					iv.setImageResource(R.drawable.done);
				}
			}
		} else {

			Log.e("updateui", "updateui iv is null");
		}
	}
	
	public Handler getHandler() {
		return handler;
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		
	}
	
	public void surfaceCreated(SurfaceHolder arg0) {
	
		
	}
	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;

		MobclickAgent.onResume(this);
	}

	private void startAlarm() {
		
		if(alarm != null) {
			String extra = alarm.getExtra();
			String alarmhandler = "com.rock.alarmclock.task.PlayMusicAlarmHandler";
			JSONObject data = new JSONObject();
			
			try {

				data.put("channel", "CRIÇáËÉµ÷Æµ");
				data.put("url", "mmsh://enmms.chinabroadcast.cn/fm91.5");
				
				if(extra != null && !"".equals(extra)) {
					JSONObject extraObject = new JSONObject(extra);
					if(extraObject.has("handler")) alarmhandler = extraObject.getString("handler"); 
					if(extraObject.has("data")) data = extraObject.getJSONObject("data");
				}
				task = new AlarmTask(data.toString(), alarmhandler);
				task.setFinishHandler(this.handler);
				task.execute(this);
			} catch (JSONException e) {
				Log.e("fail to start alarm", e.getMessage());
			}
			
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);

		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		if(task != null) {
			task.finish(this);
		}
		try {
			if(mediaPlayer != null) mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			if(mediaPlayer != null) mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!alarmActive)
			return;
		String button = (String) v.getTag();
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		if (button.equalsIgnoreCase("clear")) {
			if (answerBuilder.length() > 0) {
				answerBuilder.setLength(answerBuilder.length() - 1);
				answerView.setText(answerBuilder.toString());
			}
		} else if (button.equalsIgnoreCase(".")) {
			if (!answerBuilder.toString().contains(button)) {
				if (answerBuilder.length() == 0)
					answerBuilder.append(0);
				answerBuilder.append(button);
				answerView.setText(answerBuilder.toString());
			}
		} else if (button.equalsIgnoreCase("-")) {
			if (answerBuilder.length() == 0) {
				answerBuilder.append(button);
				answerView.setText(answerBuilder.toString());
			}
		} else {
			answerBuilder.append(button);
			answerView.setText(answerBuilder.toString());
			if (isAnswerCorrect()) {
				
				if(task != null) task.finish(this);
				
				alarmActive = false;
				if (vibrator != null)
					vibrator.cancel();
				try {
					if(mediaPlayer != null) {
						mediaPlayer.stop();
					}
				} catch (IllegalStateException ise) {

				}
				try {

					if(mediaPlayer != null)  mediaPlayer.release();
				} catch (Exception e) {

				}
				timerTask.cancel();
				this.finish();
			}
		}
		if (answerView.getText().length() >= answerString.length()
				&& !isAnswerCorrect()) {
			answerView.setTextColor(Color.RED);
		} else {
			answerView.setTextColor(Color.WHITE);
		}
	}

	public boolean isAnswerCorrect() {
		boolean correct = false;
		try {
			correct = mathProblem.getAnswer() == Float.parseFloat(answerBuilder
					.toString());
		} catch (NumberFormatException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return correct;
	}
	

}
