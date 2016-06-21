/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lzstudio.healthy.pedometer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseActivity;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The {@link StepServiceController} and
 * {@link StepServiceBinding} classes show how to interact with the service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class StepService extends Service {
	private static final String TAG = "name.bagi.levente.pedometer.StepService";
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private StepDetector mStepDetector;
	private StepDisplayer mStepDisplayer;
	private PaceNotifier mPaceNotifier;
	private DistanceNotifier mDistanceNotifier;
	private SpeedNotifier mSpeedNotifier;
	private CaloriesNotifier mCaloriesNotifier;

	private PowerManager.WakeLock wakeLock;
	private NotificationManager mNM;

	private int mSteps;
	private int mPace;
	private float mDistance;
	private float mSpeed;
	private float mCalories;
	private long second = 0;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class StepBinder extends Binder {
		public StepService getService() {
			return StepService.this;
		}
	}

	private static final int TIME_CHANGE = 501;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (TIME_CHANGE == msg.what) {
				second++;
				if (mCallback != null) {
					mCallback.timeChanged(second);
				}
				handler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
			}
		};
	};

	@Override
	public void onCreate() {
		Log.i(TAG, "[SERVICE] onCreate");
		super.onCreate();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();

		// Load settings
		mSettings = getSharedPreferences("setting", Context.MODE_PRIVATE);
		mPedometerSettings = new PedometerSettings(mSettings);

		acquireWakeLock();

		// Start detecting
		mStepDetector = new StepDetector();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		registerDetector();

		// Register our receiver for the ACTION_SCREEN_OFF action. This will
		// make our receiver
		// code be called whenever the phone enters standby mode.
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		mStepDisplayer = new StepDisplayer();
		mStepDisplayer.addListener(mStepListener);
		mStepDetector.addStepListener(mStepDisplayer);

		mPaceNotifier = new PaceNotifier(mPedometerSettings);
		mPaceNotifier.addListener(mPaceListener);
		mStepDetector.addStepListener(mPaceNotifier);

		mDistanceNotifier = new DistanceNotifier(mDistanceListener,
				mPedometerSettings);
		mStepDetector.addStepListener(mDistanceNotifier);

		mSpeedNotifier = new SpeedNotifier(mSpeedListener, mPedometerSettings);
		mPaceNotifier.addListener(mSpeedNotifier);

		mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener,
				mPedometerSettings);
		mStepDetector.addStepListener(mCaloriesNotifier);

		// Used when debugging:
		// mStepBuzzer = new StepBuzzer(this);
		// mStepDetector.addStepListener(mStepBuzzer);
		handler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
		// Start voice
		reloadSettings();

		// Tell the user we started.
		Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "[SERVICE] onStart");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "[SERVICE] onDestroy");
		second = 0;
		// Unregister our receiver.
		unregisterReceiver(mReceiver);
		unregisterDetector();
		handler.removeMessages(TIME_CHANGE);
		mNM.cancel(R.string.app_name);
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
		super.onDestroy();

		// Stop detecting
		mSensorManager.unregisterListener(mStepDetector);

		// Tell the user we stopped.
		Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT)
				.show();
	}

	private void registerDetector() {
		mSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER /*
															 * | Sensor.
															 * TYPE_MAGNETIC_FIELD
															 * | Sensor.
															 * TYPE_ORIENTATION
															 */);
		mSensorManager.registerListener(mStepDetector, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void unregisterDetector() {
		mSensorManager.unregisterListener(mStepDetector);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "[SERVICE] onBind");
		return mBinder;
	}

	/**
	 * Receives messages from activity.
	 */
	private final IBinder mBinder = new StepBinder();

	public interface ICallback {
		public void stepsChanged(int value);

		public void paceChanged(int value);

		public void distanceChanged(float value);

		public void speedChanged(float value);

		public void caloriesChanged(float value);

		public void timeChanged(long time);
	}

	private ICallback mCallback;

	public void registerCallback(ICallback cb) {
		mCallback = cb;
		// mStepDisplayer.passValue();
		// mPaceListener.passValue();
	}

	public void reloadSettings() {
		mSettings = getSharedPreferences("setting", Context.MODE_PRIVATE);
		if (mStepDetector != null) {
			mStepDetector.setSensitivity(mSettings.getFloat("sensitivity", 30));
		}

		if (mStepDisplayer != null)
			mStepDisplayer.reloadSettings();
		if (mPaceNotifier != null)
			mPaceNotifier.reloadSettings();
		if (mDistanceNotifier != null)
			mDistanceNotifier.reloadSettings();
		if (mSpeedNotifier != null)
			mSpeedNotifier.reloadSettings();
		if (mCaloriesNotifier != null)
			mCaloriesNotifier.reloadSettings();
	}

	public void resetValues() {
		mStepDisplayer.setSteps(0);
		mPaceNotifier.setPace(0);
		mDistanceNotifier.setDistance(0);
		mSpeedNotifier.setSpeed(0);
		mCaloriesNotifier.setCalories(0);
	}

	/**
	 * Forwards pace values from PaceNotifier to the activity.
	 */
	private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
		public void stepsChanged(int value) {
			mSteps = value;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.stepsChanged(mSteps);
			}
		}
	};
	/**
	 * Forwards pace values from PaceNotifier to the activity.
	 */
	private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
		public void paceChanged(int value) {
			mPace = value;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.paceChanged(mPace);
			}
		}
	};
	/**
	 * Forwards distance values from DistanceNotifier to the activity.
	 */
	private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
		public void valueChanged(float value) {
			mDistance = value;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.distanceChanged(mDistance);
			}
		}
	};
	/**
	 * Forwards speed values from SpeedNotifier to the activity.
	 */
	private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
		public void valueChanged(float value) {
			mSpeed = value;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.speedChanged(mSpeed);
			}
		}
	};
	/**
	 * Forwards calories values from CaloriesNotifier to the activity.
	 */
	private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
		public void valueChanged(float value) {
			mCalories = value;
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.caloriesChanged(mCalories);
			}
		}
	};

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		CharSequence text = getText(R.string.app_name);
		Notification notification = new Notification(R.drawable.ic_launcher,
				null, System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;
		Intent pedometerIntent = new Intent();
		pedometerIntent
				.setComponent(new ComponentName(this, BaseActivity.class));
		pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				pedometerIntent, 0);
		notification.setLatestEventInfo(this, text,
				getText(R.string.notification_subtitle), contentIntent);

		mNM.notify(R.string.app_name, notification);
	}

	// BroadcastReceiver for handling ACTION_SCREEN_OFF.
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Check action just to be on the safe side.
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				// Unregisters the listener and registers it again.
				StepService.this.unregisterDetector();
				StepService.this.registerDetector();
			}
		}
	};

	private void acquireWakeLock() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		int wakeFlags;
		wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
		wakeLock = pm.newWakeLock(wakeFlags, TAG);
		if (null != wakeLock) {
			wakeLock.acquire();
		}
	}

}
