package com.rdpharr.DataHabit;

import org.joda.time.DateTime;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.easytracking.TrackedActivity;


public class DataLoggerActivity extends TrackedActivity {
	private RatingBar rb;
	private SeekBar sb;
	private EditText et;
	private RadioGroup rg;
	private TextView b3;
	private int trackerID, dataRowID;
	private int type;
	private TextView tvStartTime, tvStartDate;
	private TextView title;
	private EditText etSecs, etMins, etHours;
	private TextView tvTimerControl;
	private EditText etComment;
	private Tracker t;
	private DataPoint d;
	boolean timerRunning;
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        trackerID = i.getIntExtra("TrackerRowID", 0);
        dataRowID = i.getIntExtra("dataRowID", 0);
        
        //timer control for orientation changes
        boolean timerRunning = savedInstanceState.getBoolean("timerRunning", false);
        if (timerRunning) UtilDat.timerControl(DataLoggerActivity.this,etSecs,etMins,etHours,tvTimerControl);
		
        if (trackerID==0){
        	TextView tv = new TextView(this);
        	tv.setText(this.getString(R.string.no_monitors));
        	setContentView(tv);
        }else{
        	setContentView(R.layout.data_logger);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        getControls();
	        t = new Tracker(this, trackerID);
	        d = new DataPoint(this,trackerID,dataRowID);
			
	        //setup form data
	        title.setText(t.getName());
	        UtilDat.setValue(etSecs, etMins, etHours, t.getType(),d.getValue(),rb, sb, et, rg, b3);
			etComment.setText(d.getComment());
			DateTime time = new DateTime(d.getTime());
			tvStartTime.setText(Helper.underline(Helper.calToTime(time)));
			tvStartDate.setText(Helper.underline(Helper.calToDate(time)));

			final TextView tvDataSave = (TextView) findViewById(R.id.tvDataSave);
            tvDataSave.setText(Helper.underline(this.getString(R.string.save)));
            tvDataSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
            		d.setComment(etComment.getText().toString());
            		d.setValue((float) UtilDat.getValue(etSecs, etMins, etHours, type,rb, sb, et, rg, b3));
            		d.setTime(Helper.strToMillis(tvStartDate.getText().toString(), tvStartTime.getText().toString()));
                	d.submitData(DataLoggerActivity.this);
                	Toast toast = Toast.makeText(getApplicationContext(),
                			getResources().getString(R.string.data_saved),
                			Toast.LENGTH_SHORT);
                	toast.show();
                	finish();
                }
            });
	        TextView tvDataCancel = (TextView)findViewById(R.id.TvDataCancel);
	        tvDataCancel.setText(Helper.underline(getResources().getString(R.string.cancel)));
	        tvDataCancel.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
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
    	tvTimerControl = (TextView)findViewById(R.id.tvTimerControl);
    	etComment = (EditText)findViewById(R.id.etComment);
	}
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putBoolean("timerRunning", timerRunning);
	}
}