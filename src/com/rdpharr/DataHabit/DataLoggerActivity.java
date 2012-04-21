package com.rdpharr.DataHabit;

import java.util.Calendar;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.easytracking.TrackedActivity;


public class DataLoggerActivity extends TrackedActivity {
	private dbAdapter mDbHelper;
	private RatingBar rb;
	private SeekBar sb;
	private EditText et;
	private RadioGroup rg;
	private TextView b3;
	private int trackerID;
	private int dataRowID;
	private int type;
	private long submitDate;
	private TextView tvStartTime;
	private TextView tvStartDate;
	private TextView title;
	private EditText etSecs;
	private EditText etMins;
	private EditText etHours;
	private RelativeLayout rlTimer;
	private TextView tvTimerControl;
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        trackerID = i.getIntExtra("TrackerRowID", 0);
        dataRowID = i.getIntExtra("dataRowID", 0);
        
		       
        if (trackerID==0){
        	TextView tv = new TextView(this);
        	tv.setText(this.getString(R.string.no_monitors));
        	setContentView(tv);
        }else{
        	setContentView(R.layout.data_logger);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        getControls();
	        //get tracker info
	        mDbHelper = new dbAdapter(this);
	        mDbHelper.open();
	        Cursor c = mDbHelper.fetchTracker(trackerID);
	        startManagingCursor(c);
	        //COLUMNS: RowID(0), name(1), type(2), freq, color, nextreminder, icon, lastupdate(7)
	        
	        title.setText(c.getString(1)); //setup form title
	        
	    	//setup form with correct control
	        type = c.getInt(2);
	        UtilDat.setInputControl(rlTimer, type, null, null, rb, sb, et, rg, b3);
	        //populate values if edit activity
	        if (dataRowID>0){
	        	populateValues();
	        	//update data button
	            final TextView tvDataSave = (TextView) findViewById(R.id.tvDataSave);
	            tvDataSave.setText(Helper.underline(this.getString(R.string.update)));
	            tvDataSave.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	updateData(dataRowID,type,trackerID);
	                	Toast toast = Toast.makeText(getApplicationContext(),
	                			getResources().getString(R.string.data_updated),
	                			Toast.LENGTH_SHORT);
	                	toast.show();
	                	mDbHelper.close();
	                	finish();
	                }
	            });
	        }else{
	            //set default values
	        	DateTime cal = DateTime.now();
	    		tvStartTime.setText(Helper.underline(Helper.calToTime(cal)));
	    		tvStartDate.setText(Helper.underline(Helper.calToDate(cal)));
	        	
	    		//submit data button
	            final TextView tvDataSave = (TextView) findViewById(R.id.tvDataSave);
	            tvDataSave.setText(Helper.underline(getResources().getString(R.string.save)));
	            tvDataSave.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	submitData(type,trackerID);
	                	Toast toast = Toast.makeText(getApplicationContext(),
	                			getResources().getString(R.string.data_saved), 
	                			Toast.LENGTH_SHORT);
	                	toast.show();
	                	mDbHelper.close();
	                	finish();
	                }
	            });
	        }
	        TextView tvDataCancel = (TextView)findViewById(R.id.TvDataCancel);
	        tvDataCancel.setText(Helper.underline(getResources().getString(R.string.cancel)));
	        tvDataCancel.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	mDbHelper.close();
	            	finish();
	            }
	        });
	        tvTimerControl.setText(Helper.underline(getResources().getString(R.string.start_timer)));
	        tvTimerControl.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	UtilDat.timerControl(DataLoggerActivity.this,etSecs,etMins,etHours,tvTimerControl);
	            }
	        });
			tvStartDate.setOnClickListener(new View.OnClickListener() {
		          public void onClick(View v) {
		        	  	String s = (String) tvStartDate.getText().toString();
		        	  	String[] s2 = s.split("-");
		        	  	int mYear = Integer.parseInt(s2[0]);
		        	    int mMonth = Integer.parseInt(s2[1])-1;
		        	    int mDay = Integer.parseInt(s2[2]);
		        	    DatePickerDialog dtDialog = new DatePickerDialog(DataLoggerActivity.this, new DatePickerDialog.OnDateSetListener() {
	    	                public void onDateSet(DatePicker view, int year, 
	                                int monthOfYear, int dayOfMonth) {
	    	                	tvStartDate.setText(Helper.underline(
	    	                			String.valueOf(year) +
	    	                			"-" +
	    	                			String.valueOf(monthOfYear+1)+
	    	                			"-" +
	    	                			String.valueOf(dayOfMonth)));
	    	                }
	        	    },mYear, mMonth, mDay);
	        	  dtDialog.show();
		          }
			});
			tvStartTime.setOnClickListener(new View.OnClickListener() {
		          public void onClick(View v) {
		        	  	String s = (String) tvStartTime.getText().toString();
		        	  	String[] s2 = s.split(":");
		        	    int mHour = Integer.parseInt(s2[0]);
		        	    int mMinute= Integer.parseInt(s2[1]);
		        	    
		        	    TimePickerDialog tpDialog = new TimePickerDialog(DataLoggerActivity.this, new TimePickerDialog.OnTimeSetListener() {
	        	                public void onTimeSet(android.widget.TimePicker view,
	        	                        int hourOfDay, int minute) {
	        	                	tvStartTime.setText(Helper.underline(
	        	                			String.valueOf(hourOfDay) +":" 
	        	                			+ String.valueOf(String.format("%02d",minute))));
	        	                }
		        	    },mHour, mMinute, false);
		        	  tpDialog.show();
		          }
			});
    	}
	}
    
	private void getControls() {
		tvStartTime= (TextView) findViewById(R.id.tvStartTime);
		tvStartDate= (TextView) findViewById(R.id.tvStartDate);
		rb = (RatingBar)findViewById(R.id.ratingBar1);
		sb = (SeekBar)findViewById(R.id.seekBar1);
        et = (EditText)findViewById(R.id.editText2);
        rg = (RadioGroup)findViewById(R.id.radioGroup1);
        b3 = (TextView)findViewById(R.id.btnLogger);
        title = (TextView) findViewById(R.id.tvTitle);
    	etSecs = (EditText)findViewById(R.id.etSecs);
    	etMins = (EditText)findViewById(R.id.etMins);
    	etHours = (EditText)findViewById(R.id.etHours);
    	rlTimer = (RelativeLayout)findViewById(R.id.rlTimer); 
    	tvTimerControl = (TextView)findViewById(R.id.tvTimerControl);
	}

	private void populateValues(){
		Cursor c = mDbHelper.fetchData(dataRowID);
		submitDate = c.getLong(2);
		UtilDat.setValue(etSecs, etMins, etHours, type,c.getFloat(3),rb, sb, et, rg, b3);
		EditText etComment = (EditText)findViewById(R.id.etComment);
		etComment.setText(c.getString(4));
		DateTime dt = new DateTime();
		DateTime cal = dt.withMillis(submitDate);
		tvStartTime.setText(Helper.underline(Helper.calToTime(cal)));
		tvStartDate.setText(Helper.underline(Helper.calToDate(cal)));
	}
	private void submitData(int type, int trackerID){
		EditText etComment = (EditText)findViewById(R.id.etComment);
		String comment = etComment.getText().toString();
		double value = UtilDat.getValue(etSecs, etMins, etHours, type,rb, sb, et, rg, b3);
		
		DateTime c = new DateTime();
		c= Helper.strToCal(tvStartDate.getText().toString(), tvStartTime.getText().toString());
		long dataTime = c.getMillis();
		
		mDbHelper.createData(
				trackerID, 
				dataTime, 
				value, 
				comment, 
				Calendar.getInstance().getTimeInMillis()
				);
	}
	private void updateData(int rowId,int type, int trackerID){
		EditText etComment = (EditText)findViewById(R.id.etComment);
		String comment = etComment.getText().toString();
		double value = UtilDat.getValue(etSecs, etMins, etHours, type,rb, sb, et, rg, b3);
		
		DateTime c = new DateTime();
		c= Helper.strToCal(tvStartDate.getText().toString(), tvStartTime.getText().toString());
		long dataTime = c.getMillis();
		
		mDbHelper.updateData(rowId,
				trackerID, 
				dataTime,
				value, 
				comment, 
				Calendar.getInstance().getTimeInMillis()
				);
	}

}