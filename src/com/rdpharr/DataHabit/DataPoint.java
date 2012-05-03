package com.rdpharr.DataHabit;

import org.joda.time.DateTime;
import android.content.Context;
import android.database.Cursor;
/**
 * Class that records the values, comments, 
 * and times for each data point
 * @author roger pharr
 */
public class DataPoint {
	private int id; //autonumbered by sqlite, 0 means new tracker
	private int trackerId;//id of parent tracker
	private long time; //user entered time
	private double value;//user set numeric value of data point
	private String comment;//user entered comment
	private long updateTime; //system time of save
	private dbAdapter mDbHelper;
	/**
	 * Class that records the values, comments, 
	 * and times for each data point
	 * @param ctx			Activity Context
	 * @param trackerId		Unique ID for tracker (parent)
	 * @param rowId			Unique ID for data point
	 */
	public DataPoint(Context ctx, int trackerId, int rowId){
		this.trackerId = trackerId;
		id=rowId;//id will be 0 if new
		if (id ==0) setDefaults(ctx);
		else getFromDb(ctx);
	}
	/**
	 * submits data point to database
	 * @param ctx	Activity Context
	 */
	public void submitData(Context ctx){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		DateTime dt = new DateTime();
		if (id>0){
			//update existing data point
			mDbHelper.updateData(id,
					trackerId, 
					time,
					value, 
					comment, 
					dt.getMillis()
					);
		}else{
			//save new data point
			mDbHelper.createData(
					trackerId, 
					time, 
					value, 
					comment, 
					dt.getMillis()
					);
		}
		mDbHelper.close();
	}
	/**
	 * Sets default values for data point
	 * value=0, date/time=now, comment=""
	 * @param ctx	Activity Context
	 */
	private void setDefaults(Context ctx){
		DateTime dt = new DateTime();
		time=dt.getMillis();
		value=0;
		comment="";
		updateTime=dt.getMillis();
	}
	/** 
	 * Gets values for data point form database
	 * @param ctx	Activity Context
	 */
	private void getFromDb(Context ctx){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchData(id);
        time=c.getLong(2);
        value=c.getFloat(3);
        comment=c.getString(4);
        updateTime=c.getLong(5);
    }
	/**
	 * gets unique id for data point
	 * @return unique id for data point
	 */
	public int getId() {
		return id;
	}
	/**
	 * gets unique id of Tracker associated with this data point
	 * @return tracker id
	 */
	public int getTrackerId() {
		return trackerId;
	}
	/**
	 * sets unique id of tracker associated with this data point
	 * @param trackerId unique id of tracker
	 */
	public void setTrackerId(int trackerId) {
		this.trackerId = trackerId;
	}
	/**
	 * gets time displayed for data point
	 * @return time in millis
	 */
	public long getTime() {
		return time;
	}
	/**
	 * sets time associated with the data point
	 * @param time time in millis
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * gets user entered value/number associated with data point
	 * @return value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * sets user entered value/number associated with this data point
	 * @param value 
	 */
	public void setValue(double value) {
		this.value = value;
	}
	/**
	 * user entered comment for the data point
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * user entered comment for the data point
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * system generated time data was set in database
	 * @return time in millis
	 */
	public long getUpdateTime() {
		return updateTime;
	}
	/**
	 * system generated time data was set in database
	 * @param updateTime time in millis
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
}
