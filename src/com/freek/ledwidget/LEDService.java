package com.freek.ledwidget;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LEDService extends Service
{
	public static final String ACTION_TOGGLE_LED = "com.freek.ledwidget.LEDWidgetProvider.ACTION_TOGGLE_LED";
	boolean isLEDOn;
	Camera camera;

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
		
		// In order to work, the camera needs a surface to turn on.
		// Here I pass it a dummy Surface Texture to make it happy.
		try
		{
			camera = Camera.open();
			camera.setPreviewTexture(new SurfaceTexture(0));
			camera.startPreview();
			Parameters p = camera.getParameters();
			p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(p);
			// Now update the widgets to reflect the change.
			Intent intent = new Intent(this, LEDWidgetProvider.class);
			intent.setAction(LEDWidgetProvider.UPDATE_WIDGET_ON);
			sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
			toast("Unable to turn on LED");
		}
		
	}

	private void turnLEDOff()
	{
		if (camera != null)
		{
			// Stopping the camera is enough to turn off the LED
			camera.stopPreview();
			camera.release();
			camera = null;
		} else
			throw new NullPointerException("Camera doesn't exist to turn off.");
		
		// Now update the widgets to reflect the change.
		Intent intent = new Intent(this, LEDWidgetProvider.class);
		intent.setAction(LEDWidgetProvider.UPDATE_WIDGET_OFF);
		sendBroadcast(intent);
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
