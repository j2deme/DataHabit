package com.rdpharr.DataHabit;

import android.content.Context;
import android.database.Cursor;

public class dbHelper {
	private static dbAdapter mDbHelper;
	public static int getTrackerType(Context ctx, int trackerId){
		int returnVal;
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchTracker(trackerId);
		returnVal = c.getInt(2);
		mDbHelper.close();
		return returnVal;
	}
	public static int getDataRowId(Context ctx, int trackerId, int position){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
    	Cursor c = mDbHelper.fetchAllData(trackerId);
        c.moveToPosition(position);
    	int dataRowID = c.getInt(0);
    	mDbHelper.close();
    	return dataRowID;
    }
}
