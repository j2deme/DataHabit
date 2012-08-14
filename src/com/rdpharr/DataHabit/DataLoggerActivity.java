package com.rdpharr.DataHabit;

import models.DataPoint;
import models.FormatHelper;
import models.Tracker;
import models.WhatsNewDialog;

import org.joda.time.DateTime;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
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
import controllers.UtilDat;


public class DataLoggerActivity extends TrackedActivity {
	private RatingBar rb;
	private SeekBar sb;
	private EditText et;
	private RadioGroup rg;
	private TextView b3;
	private int trackerID, dataRowID;
	private RelativeLayout rlTimer;
	private TextView tvStartTime, tvStartDate;
	private TextView title;
	private EditText etSecs, etMins, etHours;
	private TextView tvTimerControl;
	private EditText etComment;
	private Tracker t;
	private DataPoint d;
	FormatHelper f;
	boolean timerRunning;
	private long time;
	private DateTime dt;
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //check for new version
        WhatsNewDialog w = new WhatsNewDialog(this);
        w.showDialog();
        
        f = new FormatHelper(this);
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
	        t = new Tracker(this, trackerID);
	        d = new DataPoint(this,trackerID,dataRowID);
	        dt = new DateTime(d.getTime());
			
	        //setup form data
	        title.setText(t.getName());
	        UtilDat.setInputControl(rlTimer, t.getType(), null, null, rb, sb, et, rg, b3);
		    UtilDat.setValue(etSecs, etMins, etHours, t.getType(),d.getValue(),rb, sb, et, rg, b3);
			etComment.setText(d.getComment());
			time = dt.getMillis();
			tvStartTime.setText(f.underline(f.milliToTime(time)));
			tvStartDate.setText(f.underline(f.milliToDate(time)));

			final TextView tvDataSave = (TextView) findViewById(R.id.tvDataSave);
            tvDataSave.setText(f.underline(this.getString(R.string.save)));
            tvDataSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
            		d.setComment(etComment.getText().toString());
            		d.setValue((float) UtilDat.getValue(etSecs, etMins, etHours, t.getType(),rb, sb, et, rg, b3));
            		d.setTime(f.strToMillis(tvStartDate.getText().toString(), tvStartTime.getText().toString()));
                	d.submitData(DataLoggerActivity.this);
                	Toast toast = Toast.makeText(getApplicationContext(),
                			getResources().getString(R.string.data_saved),
                			Toast.LENGTH_SHORT);
                	toast.show();
                	//in case orientation is locked
	            	DataLoggerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	            	finish();
                }
            });
	        TextView tvDataCancel = (TextView)findViewById(R.id.TvDataCancel);
	        tvDataCancel.setText(f.underline(getResources().getString(R.string.cancel)));
	        tvDataCancel.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	//in case orientation is locked
	            	DataLoggerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	            	finish();
	            }
	        });
	        tvTimerControl.setText(f.underline(getResources().getString(R.string.start_timer)));
	        tvTimerControl.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	//freeze/unfreeze orientation so timer doesn't stop unexpectedly
	            	int orientation = DataLoggerActivity.this.getRequestedOrientation();
	            	if(orientation == ActivityInfo.SCREEN_ORIENTATION_NOSENSOR){
	            		DataLoggerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	            	}else{
	            		DataLoggerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	            	}
	            	UtilDat.timerControl(DataLoggerActivity.this,etSecs,etMins,etHours,tvTimerControl);
	            }
	        });
			tvStartDate.setOnClickListener(new View.OnClickListener() {
		          public void onClick(View v) {
		        	  	int mYear = dt.getYear();
		        	    int mMonth = dt.getMonthOfYear()-1;
		        	    int mDay = dt.getDayOfMonth();
		        	    DatePickerDialog dtDialog = new DatePickerDialog(DataLoggerActivity.this, 
		        	    		new DatePickerDialog.OnDateSetListener() {
	    	                		public void onDateSet(DatePicker view, int year, 
	    	                				int monthOfYear, int dayOfMonth) {
	    	                			dt = dt.withYear(year);
			    	                	dt = dt.withMonthOfYear(monthOfYear+1);
			    	                	dt = dt.withDayOfMonth(dayOfMonth);
			    	                	tvStartDate.setText(f.underline(f.milliToDate(dt.getMillis())));
	    	                		}
		        	    	},mYear, mMonth, mDay);
		        	    dtDialog.show();
		          	}
			});
			tvStartTime.setOnClickListener(new View.OnClickListener() {
		          public void onClick(View v) {
		        	  	int mHour = dt.getHourOfDay();
		        	    int mMinute= dt.getMinuteOfHour();
		        	    
		        	    TimePickerDialog tpDialog = new TimePickerDialog(DataLoggerActivity.this, new TimePickerDialog.OnTimeSetListener() {
	        	                public void onTimeSet(android.widget.TimePicker view,
	        	                        int hourOfDay, int minute) {
	        	                	dt = dt.withHourOfDay(hourOfDay);
	        	                	dt = dt.withMinuteOfHour(minute);
	        	                	tvStartTime.setText(f.underline(f.milliToTime(dt.getMillis())));
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
    	rlTimer = (RelativeLayout)findViewById(R.id.rlTimer); 
	}
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putBoolean("timerRunning", timerRunning);
	}
}