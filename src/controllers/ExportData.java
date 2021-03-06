package controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.FormatHelper;
import models.dbAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

import com.rdpharr.DataHabit.R;

public class ExportData {
	private static dbAdapter mDbHelper;
	private static int trackerType;
	private static FormatHelper f;
	private static List<String[]> results;
	
	public static String exportSomeData(boolean[] options, Context ctx, long[] selectedItems){
		//options = {"Separate Date and Time Fields", "Include Last Update Time"};
		String outFile = Environment.getExternalStorageDirectory().toString() 
				+ "/" +"DataHabit.csv";
		Log.d("outFile", outFile);
		results= new ArrayList<String[]>();
		trackerCsvHeader(options, ctx);
		
		for (int i=0; i<selectedItems.length; i++){
			trackerCsv(options, ctx, (int) selectedItems[i]);
    	}
		
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(outFile));
			writer.writeAll(results);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		outFile = "file://" + outFile;
        return outFile;
	}
	private static void trackerCsvHeader(boolean[] options, Context cxt){
		String[] header;
		if (options[1]==true){
			if (options[0]==false){//1,0
				header = new String[5];//name,date,time,value,comment
				header[0] = cxt.getString(R.string.TrackerName);
				header[1] = cxt.getString(R.string.date);
				header[2] = cxt.getString(R.string.time);
				header[3] = cxt.getString(R.string.value);
				header[4] = cxt.getString(R.string.Comments);
			}else{//1,1
				header = new String[7];//name,date,time,value,comment,lastupdatedate,lastupdatetime
				header[0] = cxt.getString(R.string.TrackerName);
				header[1] = cxt.getString(R.string.date);
				header[2] = cxt.getString(R.string.time);
				header[3] = cxt.getString(R.string.value);
				header[4] = cxt.getString(R.string.Comments);
				header[5] = cxt.getString(R.string.LastUpdatedDate);
				header[6] = cxt.getString(R.string.LastUpdatedTime);
			}
		}else{
			if (options[0]==false){//0,0
				header = new String[4];//name,date,value,comment
				header[0] = cxt.getString(R.string.TrackerName);
				header[1] = cxt.getString(R.string.date);
				header[2] = cxt.getString(R.string.value);
				header[3] = cxt.getString(R.string.Comments);
			}else{//0,1
				header = new String[5];//name,date,value,comment,lastupdatedate
				header[0] = cxt.getString(R.string.TrackerName);
				header[1] = cxt.getString(R.string.date);
				header[2] = cxt.getString(R.string.value);
				header[3] = cxt.getString(R.string.Comments);
				header[4] = cxt.getString(R.string.LastUpdatedDate);
			}
		}
		results.add(header);
	}
	private static void trackerCsv(boolean[] options, Context ctx, int trackerID){
		f=new FormatHelper(ctx);
		
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		
        Cursor c = mDbHelper.fetchTracker(trackerID);
		String trackerName = c.getString(1) ;
		trackerType = c.getInt(2);
        c.close();
        
        Cursor d = mDbHelper.fetchAllData(trackerID);
    	if (options[1]==true){
			if (options[0]==false){//1,0
				for (int i=0; i<d.getCount(); i++){
					String[] data = new String[5];//name,date,time,value,comment
					d.moveToPosition(i);
					data[0] = trackerName;
					data[1] = f.milliToDate(d.getLong(2));
					data[2] = f.milliToTime(d.getLong(2));
					data[3] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[4] = d.getString(4);
					results.add(data);
				}
			}else{//1,1
				for (int i=0; i<d.getCount(); i++){
					String[] data = new String[7];//name,date,time,value,comment,lastupdatedate,lastupdatetime
					d.moveToPosition(i);
					data[0] = trackerName;
					data[1] = f.milliToDate(d.getLong(2));
					data[2] = f.milliToTime(d.getLong(2));
					data[3] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[4] = d.getString(4);
					data[5] = f.milliToDate(d.getLong(5));
					data[6] = f.milliToTime(d.getLong(5));
					results.add(data);
				}
			}
		}else{
			if (options[0]==false){//0,0
				for (int i=0; i<d.getCount(); i++){
					String[] data = new String[4];//name,date,value,comment
					d.moveToPosition(i);
					data[0] = trackerName;
					data[1] = f.milliToDateTime(d.getLong(2));
					data[2] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[3] = d.getString(4);
					results.add(data);
				}
			}else{//0,1
				for (int i=0; i<d.getCount(); i++){
					String[] data = new String[5];//name,date,value,comment,lastupdatedate
					d.moveToPosition(i);
					data[0] = trackerName;
					data[1] = f.milliToDateTime(d.getLong(2));
					data[2] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[3] = d.getString(4);
					data[4] = f.milliToDateTime(d.getLong(5));
					results.add(data);
				}
			}
		}
		d.close();
		mDbHelper.close();
	}
}
