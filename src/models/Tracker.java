package models;

import org.joda.time.DateTime;

import android.content.Context;
import android.database.Cursor;

import com.rdpharr.DataHabit.R;


/**Main class for accessing datahabit trackers
 *@author roger pharr
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
	
	/**Tracker is the object that stores name, type, and reminder info for the items tracked
	 * @param ctx 			Activity Context
	 * @param trackerId		Unique ID of tracker
	 */
	public Tracker(Context ctx, int trackerId){
		//trackerId will be 0 if new
		if (trackerId ==0) setTrackerDefaults(ctx);
		else getTrackerFromDb(ctx,trackerId);
	}
	/**Defaults for Tracker:
	 * id=0, name="click to set title", reminders=0
	 * reminder freq=1, cycle=days (1), date=now
	 * @param ctx			Activity Context
	 */
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
	/**
	 * Gets tracker data from database
	 * @param ctx 			Activity Context
	 * @param trackerId		Unique ID of tracker
	 */
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
	/**
	 * Submits data to database
	 * @param ctx			Activity Context
	 */
	public void submitData(Context ctx){
		dbAdapter mDbHelper=new dbAdapter(ctx);
        mDbHelper.open();
		if (id==0) mDbHelper.createTracker(name, type, reminderEnabled, 
				reminderFreq, reminderCycle, nextReminder, lastUpdate);
		else mDbHelper.updateTracker(id, name, type, reminderEnabled, 
				reminderFreq, reminderCycle, nextReminder, lastUpdate);
		mDbHelper.close();
	}
	/**
	 * Deletes tracker form database
	 * @param ctx			Activity Context
	 */
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
	/**
	 * Unique ID of tracker
	 * @return Unique id of tracker
	 */
	public int getId() {
		return id;
	}
	/**
	 * Name of tracker
	 * @return Name that the user set for the tracker
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets name of tracker
	 * @param name	Name of Tracker
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Type of tracker (star rating, numeric data, etc.
	 * @return 	index of "inut_types" array
	 */
	public int getType() {
		return type;
	}
	/**
	 * Type of tracker (star rating, numeric data, etc.
	 * @param type	index of "imput_types" array
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * whether reminders are enabled for the tracker
	 * @return 0 for false, 1 for true
	 */
	public int getReminderEnabled() {
		return reminderEnabled;
	}
	/**
	 * whether reminders are enabled for the tracker
	 * @param reminderEnabled	0 for false, 1 for true
	 */
	public void setReminderEnabled(int reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}
	/**
	 * Times per cycle that reminders should occur. The "2" in
	 * "2 times per day"
	 * @return			number of times per cycle
	 */
	public int getReminderFreq() {
		return reminderFreq;
	}
	/**
	 * Times per cycle that reminders should occur. The "2" in
	 * "2 times per day"
	 * @param reminderFreq	number of times per cycle
	 */
	public void setReminderFreq(int reminderFreq) {
		this.reminderFreq = reminderFreq;
	}
	/**
	 * The cycle of reminders (hours,days,weeks) 
	 * @return index of "reminder_frequencies" array
	 */
	public int getReminderCycle() {
		return reminderCycle;
	}
	/**
	 * The cycle of reminders (hours,days,weeks)
	 * @param reminderCycle index of "reminder_frequencies" array
	 */
	public void setReminderCycle(int reminderCycle) {
		this.reminderCycle = reminderCycle;
	}
	/**
	 * the time of next reminder
	 * @return time in millis
	 */
	public long getNextReminder() {
		return nextReminder;
	}
	/**
	 * timme of next reminder
	 * @param nextReminder	time in millis
	 */
	public void setNextReminder(long nextReminder) {
		this.nextReminder = nextReminder;
	}
	/**
	 * time of last update
	 * @return time in millis
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}
	/**
	 * time of last update
	 * @param lastUpdate	time in millis
	 */
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}