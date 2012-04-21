package com.rdpharr.DataHabit;

import org.joda.time.DateTime;
import android.content.Context;
import android.database.Cursor;

public class DataPoint {
	private int id; //autonumbered by sqlite, 0 means new tracker
	private int trackerId;//id of parent tracker
	private long time; //user entered time
	private double value;//user set numeric value of data point
	private String comment;//user entered comment
	private long updateTime; //system time of save
	private dbAdapter mDbHelper;
	
	public DataPoint(Context ctx, int trackerId, int rowId){
		this.trackerId = trackerId;
		id=rowId;//id will be 0 if new
		if (id ==0) setDefaults(ctx);
		else getFromDb(ctx);
	}
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
	private void setDefaults(Context ctx){
		DateTime dt = new DateTime();
		time=dt.getMillis();
		value=0;
		comment="";
		updateTime=dt.getMillis();
	}
	private void getFromDb(Context ctx){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchData(id);
        time=c.getLong(2);
        value=c.getFloat(3);
        comment=c.getColumnName(4);
        updateTime=c.getLong(5);
    }
	
	//getters/setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTrackerId() {
		return trackerId;
	}
	public void setTrackerId(int trackerId) {
		this.trackerId = trackerId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
}
