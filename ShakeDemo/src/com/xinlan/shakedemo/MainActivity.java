package com.xinlan.shakedemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;

public class MainActivity extends Activity {
	private SoundPlayer player;
	private Button mBtn;

	private SensorManager sm;
	private Sensor acceleromererSensor;
	private SensorEventListener acceleromererListener;

	private long curTime;
	private long lastUpdate;
	private int i;
	private float last_x = 0.0f;
	private float last_y = 0.0f;
	private float last_z = 0.0f;
	private static final int SHAKE_THRESHOLD = 1500;
	
	private long preShakeTime=-1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		mBtn = (Button) findViewById(R.id.btn);
		mBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				player.playSound(R.raw.sound);
			}
		});
	}

	private void init() {
		player = new SoundPlayer(this);
		player.loadSound();
		player.playSound(R.raw.sound);

		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		acceleromererListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor arg0, int arg1) {

			}

			public void onSensorChanged(SensorEvent event) {
				curTime = System.currentTimeMillis();
				// 每300毫秒检测一次
				if ((curTime - lastUpdate) > 300) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;
				float x = event.values[SensorManager.DATA_X];
				float y = event.values[SensorManager.DATA_Y];
				float z = event.values[SensorManager.DATA_Z];
				float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;
				if (speed > SHAKE_THRESHOLD) {
					shakeDo(curTime);
				}
				last_x = x;
				last_y = y;
				last_z = z;
				}
			}

		};
		sm.registerListener(acceleromererListener, acceleromererSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void shakeDo(long time){
		long delta=time-preShakeTime;
		if(delta>1000){//连续两次出发晃动事件 时间间隔必须达到1s
			player.playSound(R.raw.sound);
			preShakeTime=time;
		}
	}
}// end class
