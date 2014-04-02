package com.freek.ledwidget;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class LEDService extends Service
{
	public static final String ACTION_TOGGLE_LED = "com.freek.ledwidget.LEDWidgetProvider.ACTION_TOGGLE_LED";
	boolean isLEDOn;
	Camera camera;
	SurfaceTexture surfaceTexture;
	WakeLock wakeLock;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(intent.getAction().equals(ACTION_TOGGLE_LED))
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
	private void log(String msg)
	{
		Log.d("LED",msg);
	}
}
