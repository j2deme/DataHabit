package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import models.dbAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.rdpharr.DataHabit.R;

public class ExportLogic {
	private static dbAdapter mDbHelper;
	private static int trackerType;
	public static String exportData(boolean[] options, Context ctx){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchAllTrackers();
        String strFile=trackerCsvHeader(ctx);
        for (int i=0; i<c.getCount(); i++){
        	c.moveToPosition(i);
        	int trackerID = c.getInt(0);
        	strFile = strFile + trackerCsv(ctx, trackerID);
        }
        String outFile = makeFile("DataHabit.csv", strFile);
        return outFile;
	}
	public static String trackerCsvHeader(Context cxt){
		String strCSV = cxt.getString(R.string.csvHeader);
		return strCSV;
	}
	public static String trackerCsv(Context ctx, int trackerID){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		Log.d("trackerID",Integer.toString(trackerID));
		Cursor c = mDbHelper.fetchTracker(trackerID);
		String trackerName = c.getString(1) ;
		trackerType = c.getInt(2);
        c.close();
        
        Cursor d = mDbHelper.fetchAllData(trackerID);
        String strCSV = "";
		for (int i=0; i<d.getCount(); i++){
			d.moveToPosition(i);
			strCSV = strCSV + "\""+trackerName + "\","; 
			strCSV = strCSV + "\""+Helper.milliToStr(d.getLong(2))+ "\",";
			strCSV = strCSV + UtilDat.getValueString(ctx, trackerType,d.getFloat(3))+ ",";
			strCSV = strCSV + "\""+d.getString(4)+ "\"\n";
		}
		d.close();
		return strCSV;
	}
	public static String makeFile (String fileName, String fileContents){
		File root = Environment.getExternalStorageDirectory();
		File csvFile = new File(root, fileName);
		try {
			FileWriter csvWriter = new FileWriter(csvFile);
			BufferedWriter out = new BufferedWriter(csvWriter);
			out.write(fileContents);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        String returnVal = "file:///mnt/sdcard/" + fileName;
        return returnVal;
	}
}
