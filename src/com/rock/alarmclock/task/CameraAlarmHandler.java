package com.rock.alarmclock.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import com.rock.alarmclock.alert.AlarmAlertActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class CameraAlarmHandler implements AlarmTaskHandler,MediaRecorder.OnInfoListener {

	protected Timer imageTakeTimer = null;
	private Camera camera = null;
	private int MEDIA_TYPE_IMAGE = 1;
	private int MEDIA_TYPE_VIDEO = 2;
	private int mediatype = 1;
	private int photoNum = 3;
	private MediaRecorder mediaRecorder = null;
    private SurfaceHolder surfaceHolder = null;
    private SurfaceView surfaceView = null;
	PictureCallback mPicture = null;
	private Activity activity;
	private String publish_msg = "";
	private String running_status = "";
	private Handler handler = null;
	// private static final File PHOTO_DIR = new
	// File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/crazyclock/");

	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();

		} catch (Exception e) {
		}
		return c;
	}

	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private File getOutputMediaFile(int type) {

		PHOTO_DIR.mkdirs();

		if (type == this.MEDIA_TYPE_IMAGE) {
			return new File(PHOTO_DIR, System.currentTimeMillis() + ".jpg");
		} else {
			return new File(PHOTO_DIR, System.currentTimeMillis() + ".mp4");
		}
	}
	

	private void shoot() {
		if(camera != null && mPicture != null) {
			camera.startPreview();
			camera.takePicture(null, null, mPicture);
		}
	}
	
	private void takePicture(Activity ctx, JSONObject data) {
		
		if (data.has("num")) {
			try {
				photoNum = data.getInt("num");
			} catch (JSONException e1) {
				if (camera != null)
					camera.release();
			}
		}

		mPicture = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				
				File pictureFile = getOutputMediaFile(mediatype);
				if (pictureFile == null) {
					return;
				}

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (Exception e) {
					Log.d("fail to take picture",
							"File not found: " + e.getMessage());
				}
				photoNum--;
				if(photoNum > 0) {
					camera.stopPreview();
					shoot();
				} else {
					camera.release();
				}
				running_status = "done";
				publish_msg = "任务完成，存放路径： "+PHOTO_DIR+", 解题退出！";
				finishCallback();
			}
		};
		try {
			camera = this.getCameraInstance();
			Camera.Parameters params = camera.getParameters();
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			camera.setParameters(params);
			//SurfaceView surface = new SurfaceView(ctx);
			camera.setPreviewDisplay(surfaceView.getHolder());
			shoot();
			
		} catch (Exception e) {
			releaseCamera();
		}
	}

	
	private void takeVideo(Activity ctx, JSONObject data) {
		try {
			
			camera = this.getCameraInstance();
			//camera.setDisplayOrientation(90);
			Camera.Parameters params = camera.getParameters();
			camera.setParameters(params);
			Camera.Parameters p = camera.getParameters();
			p.set("orientation", "portrait");

			camera.setParameters(p);


			camera.unlock();

			mediaRecorder = new MediaRecorder();
			mediaRecorder.setCamera(camera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

			//mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO)
					.toString());

			mediaRecorder.setVideoFrameRate(30);
			mediaRecorder.setVideoSize(1920, 1080);
			mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

			mediaRecorder.setMaxDuration(30000);
			mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {

					publish_msg = "任务完成，存放路径： "+PHOTO_DIR+", 解题退出！";
					running_status = "done";
					finishCallback();
					//mediaRecorder.stop();
				}
			});

			running_status = "record";
			publish_msg = "正在拍摄视频，解题退出！";
			//mediaRecorder.setOrientationHint(90);
			mediaRecorder.prepare();

			finishCallback();
			mediaRecorder.start();

		} catch (IllegalStateException e) {
			Log.d("error",
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseCamera();
		} catch (IOException e) {
			releaseCamera();
			Log.d("error", "IOException preparing MediaRecorder: "
							+ e.getMessage());

		} catch (Exception e) {
			releaseCamera();
		}
	}
	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
	    Camera.Size result=null;

	    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
	        if (size.width<=width && size.height<=height) {
	            if (result==null) {
	                result=size;
	            } else {
	                int resultArea=result.width*result.height;
	                int newArea=size.width*size.height;

	                if (newArea>resultArea) {
	                    result=size;
	                }
	            }
	        }
	    }
	    return(result);
	}  
	public void stopRecording() {

	}

	private void releaseCamera() {
		if(mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		if(camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		} 
	}
	
	public void pause() {
		
	}
	public void resume(){
		
		
	}
	
	public void execute(Activity ctx, String data, Handler h) {
		activity = ctx;
		this.handler = h;
		try {
			JSONObject obj = new JSONObject(data);

			surfaceView = AlarmAlertActivity.surface;
			surfaceHolder = AlarmAlertActivity.surfaceHolder;
			
			if (obj.has("mediatype"))
				mediatype = obj.getInt("mediatype");
			if (!checkCameraHardware(ctx))
				return;
			if (mediatype == this.MEDIA_TYPE_VIDEO) {
				takeVideo(ctx, obj);
			} else {
				takePicture(ctx, obj);
			}
		} catch (Exception e) {
			Log.e("error", e.getMessage());
			e.printStackTrace();
		}

	}

	public void finish(Activity ctx, String data, Handler handler) {
		if(mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		if (camera != null) {
			
			camera.release();
			camera = null;
			
		}
	}
	public void finishCallback() {
	
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("msg", publish_msg);
		b.putString("status", running_status);
		message.setData(b);
		message.what = 0;
		if(this.handler != null) handler.sendMessage(message);
		
		
	}

	@Override
	public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
		releaseCamera();
		
	}

}
