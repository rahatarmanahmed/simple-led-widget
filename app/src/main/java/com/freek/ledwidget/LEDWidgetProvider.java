package com.freek.ledwidget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

public class LEDWidgetProvider extends AppWidgetProvider
{

	public static final String UPDATE_WIDGET_ON = "com.freek.ledwidget.LEDWidgetProvider.UPDATE_WIDGET_ON";
	public static final String UPDATE_WIDGET_OFF = "com.freek.ledwidget.LEDWidgetProvider.UPDATE_WIDGET_OFF";

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		// Set pending intent to call the Service
		Intent intent = new Intent(context, LEDService.class);
		intent.setAction(LEDService.ACTION_TOGGLE_LED);
		
		PendingIntent pIntent = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);


		for (int appWidgetId : appWidgetIds)
		{
			// Check what category of widget we're dealing with
			// lockscreens only supported in 4.2+
			boolean isLockscreen = false;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			{
				Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
				int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
				isLockscreen = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;	
			}
			
			
			if(isLockscreen) // Create lockscreen specific layout
			{
				// Create the layout and attach the listener
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.led_widget_lockscreen_layout);
				views.setOnClickPendingIntent(R.id.btn_led, pIntent);
				// Set the image to off by default
				// (This update should only be called on creation)
				views.setImageViewResource(R.id.img_led,
						R.drawable.appwidget_settings_led_off_big);
				// Update the widget
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
			else // Create homescreen specific layout
			{
				// Create the layout and attach the listener
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.led_widget_layout);
				views.setOnClickPendingIntent(R.id.btn_led, pIntent);
				// Set the images to off by default
				// (This update should only be called on creation)
				views.setImageViewResource(R.id.img_led,
						R.drawable.appwidget_settings_led_off);
				views.setImageViewResource(R.id.ind_led,
						R.drawable.appwidget_settings_ind_off_holo);
				// Update the widget
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}
		

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onReceive(Context context, Intent intent)
	{
		super.onReceive(context, intent);
		// Here we do the same thing as onUpdate, except we know if
		// the widget should use it's on images, or off images
		if (intent.getAction().equals(UPDATE_WIDGET_OFF)
				|| intent.getAction().equals(UPDATE_WIDGET_ON))
		{

			ComponentName thisWidget = new ComponentName(context,
					LEDWidgetProvider.class);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
			
//			// Set pending intent to call the Service
			Intent widgetIntent = new Intent(context, LEDService.class);
			widgetIntent.setAction(LEDService.ACTION_TOGGLE_LED);
			PendingIntent pIntent = PendingIntent.getService(context, 0,
					widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			for (int appWidgetId : appWidgetIds)
			{
				// Check what category of widget we're dealing with
				// lockscreens only supported in 4.2+
				boolean isLockscreen = false;
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				{
					Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
					int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
					isLockscreen = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;	
				}
				
				
				if(isLockscreen) // Create lockscreen specific layout
				{
					
					// Create the layout and attach the listener
					RemoteViews views = new RemoteViews(context.getPackageName(),
							R.layout.led_widget_lockscreen_layout);
					views.setOnClickPendingIntent(R.id.btn_led, pIntent);
					
					// Set the image to off by default
					// (This update should only be called on creation)
					if (intent.getAction().equals(UPDATE_WIDGET_ON))
					{
						views.setImageViewResource(R.id.img_led,
								R.drawable.appwidget_settings_led_on_big);
					} else if (intent.getAction().equals(UPDATE_WIDGET_OFF))
					{
						views.setImageViewResource(R.id.img_led,
								R.drawable.appwidget_settings_led_off_big);
					}
					
					// Update the widget
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
				else // Create homescreen specific layout
				{
//					// Create the layout and the listener
					RemoteViews views = new RemoteViews(context.getPackageName(),
							R.layout.led_widget_layout);
					views.setOnClickPendingIntent(R.id.btn_led, pIntent);
//					// Set the images to the right setting
					if (intent.getAction().equals(UPDATE_WIDGET_ON))
					{
						views.setImageViewResource(R.id.img_led,
								R.drawable.appwidget_settings_led_on);
						views.setImageViewResource(R.id.ind_led,
								R.drawable.appwidget_settings_ind_on_holo);
					} else if (intent.getAction().equals(UPDATE_WIDGET_OFF))
					{
						views.setImageViewResource(R.id.img_led,
								R.drawable.appwidget_settings_led_off);
						views.setImageViewResource(R.id.ind_led,
								R.drawable.appwidget_settings_ind_off_holo);
					}
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			}
			

//
//			// Create the layout and the listener
//			RemoteViews views = new RemoteViews(context.getPackageName(),
//					R.layout.led_widget_layout);
//			views.setOnClickPendingIntent(R.id.btn_led, pIntent);
//			// Set the images to the right setting
//			if (intent.getAction().equals(UPDATE_WIDGET_ON))
//			{
//				views.setImageViewResource(R.id.img_led,
//						R.drawable.appwidget_settings_led_on);
//				views.setImageViewResource(R.id.ind_led,
//						R.drawable.appwidget_settings_ind_on_holo);
//			} else if (intent.getAction().equals(UPDATE_WIDGET_OFF))
//			{
//				views.setImageViewResource(R.id.img_led,
//						R.drawable.appwidget_settings_led_off);
//				views.setImageViewResource(R.id.ind_led,
//						R.drawable.appwidget_settings_ind_off_holo);
//			}
//			manager.updateAppWidget(thisWidget, views);
		}

	}
}
