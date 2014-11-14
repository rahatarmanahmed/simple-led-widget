package com.freek.ledwidget;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class LEDService extends Service
{
	public static final String ACTION_TOGGLE_LED = "com.freek.ledwidget.LEDService.ACTION_TOGGLE_LED";
	public static final int NOTIFICATION_ID = 1;
	boolean isLEDOn;
	Camera camera;
	SurfaceTexture surfaceTexture;
	WakeLock wakeLock;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d("LED", intent.toString());
		Log.d("LED", "Action: " + intent.getAction());
		if(ACTION_TOGGLE_LED.equals(intent.getAction()))
			isLEDOn = toggleLED();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		isLEDOn = false;
	}

	@Override
	public void onDestroy()
	{
		if(camera != null)
			turnLEDOff();
		super.onDestroy();
	}
	
	/**
	 * Toggles the LED
	 * 
	 * @return true if LED turned on, false if LED turned off
	 */
	private boolean toggleLED()
	{
		try
		{

			if (isLEDOn())
			{
				// Turn off the LED
				turnLEDOff();
				return false;
			} else
			{
				// Turn on the LED
				turnLEDOn();
				return true;
			}

		} catch (Exception e)
		{
			toast("Unable to open camera.");
			e.printStackTrace();
		}
		return false;
	}

	@SuppressLint("Wakelock")
	private void turnLEDOn() throws IOException
	{
		Log.d("LED", "Beginning turnLEDOn()");
		try
		{
			// Acquire wakelock to fix unresponsiveness after locking
			Log.d("LED", "Acquiring wake lock");
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			if(wakeLock != null && wakeLock.isHeld()) // Shouldn't need this but never hurts.
				wakeLock.release();
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LED");
			wakeLock.acquire();
			Log.d("LED", "Wake lock acquired");
			
			Log.d("LED", "Opening camera");
			camera = Camera.open();
			
			Log.d("LED", "Setting parameters");
			Parameters p = camera.getParameters();
			p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(p);
			
			Log.d("LED", "Starting preview");
			camera.startPreview();

			// In order to work, the camera needs a surface to turn on.
			// Here I pass it a dummy Surface Texture to make it happy.
			Log.d("LED", "Setting preview texture");
			surfaceTexture = new SurfaceTexture(0);
			camera.setPreviewTexture(surfaceTexture);
			
			Log.d("LED", "Updating widgets");
			// Now update the widgets to reflect the change.
			Intent intent = new Intent(this, LEDWidgetProvider.class);
			intent.setAction(LEDWidgetProvider.UPDATE_WIDGET_ON);
			sendBroadcast(intent);
			
			
			Intent notificationIntent = new Intent(this, LEDService.class);
			notificationIntent.setAction(ACTION_TOGGLE_LED);
			
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);			
			
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.appwidget_settings_led_on)
				.setContentTitle("LED is ON")
				.setContentText("Tap to turn off.")
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setOngoing(true)
				.setContentIntent(pendingIntent)
				.build();
			notificationManager.notify(NOTIFICATION_ID, notification);
			
		} catch (Exception e) {
			e.printStackTrace();
			toast("Unable to turn on LED");
		}
		Log.d("LED", "Ending turnLEDOn()");		
	}

	@SuppressLint("NewApi")
	private void turnLEDOff()
	{
		Log.d("LED", "Beginning turnLEDOff()");
		
		if (wakeLock != null)
		{
			Log.d("LED", "Releasing wake lock");
			wakeLock.release();
			wakeLock = null;
			Log.d("LED", "Wake lock released");
		}
		
		if (camera != null)
		{
			try
			{
				// Stopping the camera is enough to turn off the LED
				Log.d("LED", "Camera isn't null, stopping it to turn off LED.");
				Log.d("LED", "Stopping preview.");
				camera.stopPreview();
				Log.d("LED", "Releasing camera.");
				camera.release();
				camera = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			throw new NullPointerException("Camera doesn't exist to turn off.");
		
		// Now update the widgets to reflect the change.
		Log.d("LED", "Updating widgets");
		Intent intent = new Intent(this, LEDWidgetProvider.class);
		intent.setAction(LEDWidgetProvider.UPDATE_WIDGET_OFF);
		sendBroadcast(intent);
		Log.d("LED", "Beginning turnLEDOff()");
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	boolean isLEDOn()
	{
		return camera != null;
	}
	
	/**
	 * Silly convenience method for making toasts.
	 * @param msg
	 */
	private void toast(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
