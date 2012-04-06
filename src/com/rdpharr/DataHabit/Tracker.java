package com.rdpharr.DataHabit;

import org.joda.time.DateTime;
import android.content.Context;
import android.database.Cursor;


/*
 *Main class for accessing Datahabit trackers 
 */

public class Tracker {
	private int id; //autonumbered by sqlite, 0 means new tracker
	private String name; //name fo tracker
	private int type; //Maps to @arrays/input_types
	private int reminderEnabled; //0 for not enabled, 1 for enabled
	private int reminderFreq; //number of times per cycle
	private int reminderCycle;//maps to @arrays/reminder_frequencies
	private long nextReminder; //timestamp
	private long lastUpdate; //timestamp
	
	public void getTracker(Context ctx, int trackerId){
		//trackerId will be 0 if new
		if (trackerId ==0) setTrackerDefaults(ctx);
		else getTrackerFromDb(ctx,trackerId);
	}
	private void setTrackerDefaults(Context ctx){
		id=0;
		name=ctx.getString(R.string.set_title);//default title "Click to set title"
		type=0;
		reminderEnabled=0;//not enabled
		reminderFreq=1;
		reminderCycle=1; //per day
		DateTime dt = new DateTime();
		nextReminder=dt.getMillis(); //now
		lastUpdate=dt.getMillis();//now
	}
	private void getTrackerFromDb(Context ctx, int TrackerId){
		
		//open the database and fetch the values
		dbAdapter mDbHelper= new dbAdapter(ctx);
        mDbHelper.open();
        Cursor c=mDbHelper.fetchTracker(TrackerId);
        
        //populate values
        id=TrackerId;
		name = c.getString(1);
		type = c.getInt(2);
		reminderEnabled=c.getInt(3);
		reminderFreq = c.getInt(4);
		reminderCycle=c.getInt(5);
		nextReminder=c.getLong(6);
		lastUpdate=c.getLong(7);
		
		//close that which were opened
		c.close();
		mDbHelper.close();
	}
	public void submitData(Context ctx){
		dbAdapter mDbHelper=new dbAdapter(ctx);
        mDbHelper.open();
		if (id==0) mDbHelper.createTracker(name, type, reminderEnabled, 
				reminderFreq, reminderCycle, nextReminder, lastUpdate);
		else mDbHelper.updateTracker(id, name, type, reminderEnabled, 
				reminderFreq, reminderCycle, nextReminder, lastUpdate);
		mDbHelper.close();
	}
	public void delete(Context ctx){
		//open the database and fetch the values
		dbAdapter mDbHelper= new dbAdapter(ctx);
        mDbHelper.open();
        mDbHelper.deleteTracker(id);
    	
        
        //delete associated datapoints
    	Cursor d = mDbHelper.fetchAllData(id);
    	if (d.getCount()>0){
    		for (int i=0; i<d.getCount(); i++){
    			d.moveToPosition(i);
    			int dataRow = d.getInt(0);
    			mDbHelper.deleteData(dataRow);
    		}
    	}
    	d = mDbHelper.fetchAllData(id);
    	
		//close that which were opened
		d.close();
		mDbHelper.close();
	}
	
	
	//just getters/setters down here
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getReminderEnabled() {
		return reminderEnabled;
	}

	public void setReminderEnabled(int reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}

	public int getReminderFreq() {
		return reminderFreq;
	}

	public void setReminderFreq(int reminderFreq) {
		this.reminderFreq = reminderFreq;
	}

	public int getReminderCycle() {
		return reminderCycle;
	}

	public void setReminderCycle(int reminderCycle) {
		this.reminderCycle = reminderCycle;
	}

	public long getNextReminder() {
		return nextReminder;
	}

	public void setNextReminder(long nextReminder) {
		this.nextReminder = nextReminder;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
