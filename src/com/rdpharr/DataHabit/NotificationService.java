package com.rdpharr.DataHabit;

import java.util.Calendar;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class NotificationService extends IntentService {
	private dbAdapter mDbHelper;
	private int notificationID;
	
	public NotificationService() {
		super("DataHabit Notification Service");
		//that's the name of our service
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		notificationID=0;
		//initialize database
        mDbHelper = new dbAdapter(this);
        mDbHelper.open();
        
        Log.d(getClass().getName(),"checking trackers");
        checkTrackers();
        mDbHelper.close();
	}
	private void checkTrackers(){
		Cursor c = mDbHelper.fetchAllTrackers();
		
		//loop through all trackers
		for (int i=0; i<c.getCount(); i++)
		{
			Calendar cal = Calendar.getInstance();
			long timeNow = cal.getTimeInMillis();
			
			//get data out of cursor
			c.moveToPosition(i);
			long nextRem =  c.getLong(6);
			int trackerID = c.getInt(0);
			String trackerName = c.getString(1);
			Log.d(getClass().getName(), "Tracker:"+ trackerName);
			
			
			if (c.getInt(3)<1){ //not enabled
				//do nothing, move to next item
			}else {
				if (timeNow>nextRem){
					Log.d(getClass().getName(), "triggering reminder");
					triggerReminder(trackerID,trackerName);
					nextReminder(c);
				}else{
					Log.d(getClass().getName(), "not triggering reminder");
				}
			}
		}
		c.close();//managed cursors don't work in services
	}
	public void triggerReminder(int trackerID, String trackerName){
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(com.rdpharr.DataHabit.R.drawable.ic_launcher, trackerName, when);
		
		
		SharedPreferences settings = getSharedPreferences("notifications", 0);
		if (settings.getInt("sound", 0) ==1) notification.defaults |= Notification.DEFAULT_SOUND;
		if (settings.getInt("vibrate", 0) ==1) notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(R.string.app_name);
		CharSequence contentText = trackerName;
		Intent i = new Intent(NotificationService.this, tabTrackerActivity.class);
		i.putExtra("TrackerRowID", trackerID);
		i.putExtra("tabID", 0);
		i.setAction("actionstring" + System.currentTimeMillis()); //used to make intent unique
		PendingIntent j = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
    	
    	notification.setLatestEventInfo(context, contentTitle, contentText, j);
    	mNotificationManager.notify(notificationID, notification);
    	notificationID++;
	}
	public void nextReminder (Cursor c){
		long rowId = c.getInt(0);
		String TrackerName = c.getString(1);
		int TrackerType = c.getInt(2);
		int TrackerEnabled = c.getInt(3);
		int TrackerFreq =c.getInt(4);
		int TrackerCycle =c.getInt(5);
		long ThisReminder = c.getLong(6);
		long TrackerLastUpdate= c.getLong(7);

		//calculate cycle
		long cycle = 0;
		switch (TrackerCycle){
			case 0: //hours
				cycle = 60*60*1000;
				break;
			case 1: //days
				cycle = 24*60*60*1000;
				break;
			case 2: //weeks
				cycle = 7*24*60*60*1000;
				break;
			default:
				break;
		}
		
		cycle = cycle * TrackerFreq;
		long NewReminderDate = ThisReminder + cycle;
		Calendar cal = Calendar.getInstance();
		while (cal.getTimeInMillis()>NewReminderDate){
			NewReminderDate = NewReminderDate + cycle;
		}		
		mDbHelper.updateTracker(rowId, TrackerName, TrackerType,TrackerEnabled,
				TrackerFreq, TrackerCycle, NewReminderDate, TrackerLastUpdate);
	}
}