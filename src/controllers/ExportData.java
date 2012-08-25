package controllers;

import java.io.FileWriter;
import java.io.IOException;
import models.FormatHelper;
import models.dbAdapter;
import android.content.Context;
import android.database.Cursor;
import au.com.bytecode.opencsv.CSVWriter;
import com.rdpharr.DataHabit.R;

public class ExportData {
	private static dbAdapter mDbHelper;
	private static int trackerType;
	private static FormatHelper f;
	
	public static String exportSomeData(boolean[] options, Context ctx, long[] selectedItems){
		//options = {"Separate Date and Time Fields", "Include Last Update Time"};
        String outFile = "file:///mnt/sdcard/DataHabit.csv";
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(outFile));
			writer.writeNext(trackerCsvHeader(options, ctx));
			for (int i=0; i<selectedItems.length; i++){
				writer.writeNext(trackerCsv(options, ctx, (int) selectedItems[i]));
        	}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return outFile;
	}
	private static String[] trackerCsvHeader(boolean[] options, Context cxt){
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
				header[3] = cxt.getString(R.string.value);
				header[4] = cxt.getString(R.string.Comments);
			}else{//0,1
				header = new String[5];//name,date,value,comment,lastupdatedate
				header[0] = cxt.getString(R.string.TrackerName);
				header[1] = cxt.getString(R.string.date);
				header[3] = cxt.getString(R.string.value);
				header[4] = cxt.getString(R.string.Comments);
				header[5] = cxt.getString(R.string.LastUpdatedDate);
			}
		}
		return header;
	}
	private static String[] trackerCsv(boolean[] options, Context ctx, int trackerID){
		f=new FormatHelper(ctx);
		String[] data = null;
		
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
		
        Cursor c = mDbHelper.fetchTracker(trackerID);
		String trackerName = c.getString(1) ;
		trackerType = c.getInt(2);
        c.close();
        
        Cursor d = mDbHelper.fetchAllData(trackerID);
        for (int i=0; i<d.getCount(); i++){
			d.moveToPosition(i);
			if (options[1]==true){
				if (options[0]==false){//1,0
					data = new String[5];//name,date,time,value,comment
					data[0] = trackerName;
					data[1] = f.milliToDate(d.getLong(2));
					data[2] = f.milliToTime(d.getLong(2));
					data[3] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[4] = d.getString(4);
				}else{//1,1
					data = new String[7];//name,date,time,value,comment,lastupdatedate,lastupdatetime
					data[0] = trackerName;
					data[1] = f.milliToDate(d.getLong(2));
					data[2] = f.milliToTime(d.getLong(2));
					data[3] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[4] = d.getString(4);
					data[5] = f.milliToDate(d.getLong(5));
					data[6] = f.milliToTime(d.getLong(5));
				}
			}else{
				if (options[0]==false){//0,0
					data = new String[4];//name,date,value,comment
					data[0] = trackerName;
					data[1] = f.milliToDateTime(d.getLong(2));
					data[3] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[4] = d.getString(4);
				}else{//0,1
					data = new String[5];//name,date,value,comment,lastupdatedate
					data[0] = trackerName;
					data[1] = f.milliToDateTime(d.getLong(2));
					data[3] = UtilDat.getValueString(ctx, trackerType,d.getFloat(3));
					data[4] = d.getString(4);
					data[5] = f.milliToDateTime(d.getLong(5));
				}
			}
		}
		d.close();
		mDbHelper.close();
		return data;
	}
}
