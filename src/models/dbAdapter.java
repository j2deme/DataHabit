package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbAdapter {
	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
	private static final String DATABASE_NAME = "DataHabit";
    private static final int DATABASE_VERSION = 3;
    private final Context mCtx;
    
    //Tracker Table: Describes the monitors and their layout
    public static final String Tracker_Table = "trackers";
    public static final String KEY_Tracker_ROWID = "_id";
    public static final String KEY_Tracker_Name="TrackerName";
    public static final String KEY_Tracker_Type="TrackerType";
    public static final String KEY_Tracker_Remind_Enabled="TrackerReminderEnabled";
    public static final String KEY_Tracker_Remind_Freq="TrackerReminderFreq";
    public static final String KEY_Tracker_Remind_Cycle="TrackerReminderCycle";
    public static final String KEY_Tracker_NextReminder = "NextReminder";
    public static final String KEY_Tracker_LastUpdate = "TrackerLastUpdate";
    public static final String Tracker_TABLE_CREATE = 
    		"create table " + Tracker_Table + "("
    		+ KEY_Tracker_ROWID + " integer primary key autoincrement, "
    		+ KEY_Tracker_Name + " text,"
    		+ KEY_Tracker_Type + " integer,"
    		+ KEY_Tracker_Remind_Enabled + " integer,"
    		+ KEY_Tracker_Remind_Freq + " integer,"
    		+ KEY_Tracker_Remind_Cycle + " integer,"
    		+ KEY_Tracker_NextReminder + " real,"
    		+ KEY_Tracker_LastUpdate + " real"
    		+");";
    
    //Data Table: Stores the collected data
    public static final String Data_Table = "data";
    public static final String KEY_Data_ROWID = "_id";
    public static final String KEY_Data_TrackerID = "TrackerID";
    public static final String KEY_Data_TimeStamp = "Timestamp";
    public static final String KEY_Data_Value="Value";
    public static final String KEY_Data_Notes="Notes";
    public static final String KEY_Data_Updated = "Updated";
    public static final String Data_TABLE_CREATE = 
    		"create table " + Data_Table + "("
    		+ KEY_Data_ROWID + " integer primary key autoincrement, "
    		+ KEY_Data_TrackerID + " integer,"
    		+ KEY_Data_TimeStamp + " text,"
    		+ KEY_Data_Value + " real,"
    		+ KEY_Data_Notes + " integer,"
    		+ KEY_Data_Updated + " real"
    		+");";
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
	        db.execSQL(Tracker_TABLE_CREATE);
	        db.execSQL(Data_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Tracker_Table);
            db.execSQL("DROP TABLE IF EXISTS " + Data_Table);
            onCreate(db);
		}
    }
    public dbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    public dbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        mDbHelper.close();
    }
    
    //tracker table functions (create, delete, fetchAll, fetch, update)
    public long createTracker(
    		String TrackerName, 
    		int TrackerType,
    		int TrackerEnabled,
    		int TrackerFreq,
    		int TrackerCycle,
    		long NextReminder,
    		long TrackerLastUpdate
    		) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Tracker_Name, TrackerName);
        initialValues.put(KEY_Tracker_Type, TrackerType);
        initialValues.put(KEY_Tracker_Remind_Enabled, TrackerEnabled);
        initialValues.put(KEY_Tracker_Remind_Freq, TrackerFreq);
        initialValues.put(KEY_Tracker_Remind_Cycle, TrackerCycle);
        initialValues.put(KEY_Tracker_NextReminder, NextReminder);
        initialValues.put(KEY_Tracker_LastUpdate, TrackerLastUpdate);
        return mDb.insert(Tracker_Table, null, initialValues);
    }
    public boolean deleteTracker(long rowId) {

        return mDb.delete(Tracker_Table, KEY_Tracker_ROWID + "=" + rowId, null) > 0;
    }
    public Cursor fetchAllTrackers() {
        return mDb.query(Tracker_Table, new String[] {
        		KEY_Tracker_ROWID, 
        		KEY_Tracker_Name,
        		KEY_Tracker_Type,
        		KEY_Tracker_Remind_Enabled,
        		KEY_Tracker_Remind_Freq,
        		KEY_Tracker_Remind_Cycle,
        		KEY_Tracker_NextReminder,
        		KEY_Tracker_LastUpdate}, null, null, null, null, KEY_Tracker_Name);
    }
    public Cursor fetchTracker (long rowId) throws SQLException {
        Cursor mCursor =
            mDb.query(Tracker_Table, new String[] {
            		KEY_Tracker_ROWID, 
            		KEY_Tracker_Name,
            		KEY_Tracker_Type,
            		KEY_Tracker_Remind_Enabled,
            		KEY_Tracker_Remind_Freq,
            		KEY_Tracker_Remind_Cycle,
            		KEY_Tracker_NextReminder,
            		KEY_Tracker_LastUpdate}, 
            		KEY_Tracker_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public boolean updateTracker(long rowId,
    		String TrackerName, 
    		int TrackerType,
    		int TrackerEnabled,
    		int TrackerFreq,
    		int TrackerCycle,
    		long NextReminder,
    		long TrackerLastUpdate
    		) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Tracker_Name, TrackerName);
        initialValues.put(KEY_Tracker_Type, TrackerType);
        initialValues.put(KEY_Tracker_Remind_Enabled, TrackerEnabled);
        initialValues.put(KEY_Tracker_Remind_Freq, TrackerFreq);
        initialValues.put(KEY_Tracker_Remind_Cycle, TrackerCycle);
        initialValues.put(KEY_Tracker_NextReminder, NextReminder);
        initialValues.put(KEY_Tracker_LastUpdate, TrackerLastUpdate);

        return mDb.update(Tracker_Table, initialValues, KEY_Tracker_ROWID + "=" + rowId, null) > 0;
    }
    
    //Data table insert, delete, update, selectAll, selectOne
    
    public long createData(
    		int TrackerID,
    		long TimeStamp,
    		double Value,
    		String Notes,
    		long Updated
    		) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Data_TrackerID, TrackerID);
        initialValues.put(KEY_Data_TimeStamp, TimeStamp);
        initialValues.put(KEY_Data_Value, Value);
        initialValues.put(KEY_Data_Notes, Notes);
        initialValues.put(KEY_Data_Updated, Updated);
        return mDb.insert(Data_Table, null, initialValues);
    }
    public boolean deleteData(long rowId) {

        return mDb.delete(Data_Table, KEY_Data_ROWID + "=" + rowId, null) > 0;
    }
    public Cursor fetchAllData(long TrackerID) {
        return mDb.query(Data_Table, new String[] {
        		KEY_Data_ROWID, 
        		KEY_Data_TrackerID,
        		KEY_Data_TimeStamp,
        		KEY_Data_Value,
        		KEY_Data_Notes,
        		KEY_Data_Updated},
        		KEY_Data_TrackerID + "=" + TrackerID, null, null, null, KEY_Data_TimeStamp+" DESC");
    }
    public Cursor fetchData (long rowId) throws SQLException {
        Cursor mCursor =
            mDb.query(Data_Table, new String[] {
            		KEY_Data_ROWID, 
            		KEY_Data_TrackerID,
            		KEY_Data_TimeStamp,
            		KEY_Data_Value,
            		KEY_Data_Notes,
            		KEY_Data_Updated}, 
            		KEY_Data_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public boolean updateData(
    		long rowId,
    		int TrackerID, 
    		long TimeStamp,
    		double Value,
    		String Notes,
    		long Updated
    		) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Data_TrackerID, TrackerID);
        initialValues.put(KEY_Data_TimeStamp, TimeStamp);
        initialValues.put(KEY_Data_Value, Value);
        initialValues.put(KEY_Data_Notes, Notes);
        initialValues.put(KEY_Data_Updated, Updated);
 
        return mDb.update(Data_Table, initialValues, KEY_Data_ROWID + "=" + rowId, null) > 0;
    }
}
