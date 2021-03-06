/*
 *TrackerActivity:
 *Manages settings for a single tracker: name, type, reminders
 *Layout is tracker.xml
 *Tracker objects from tracker.java
 */

package com.rdpharr.DataHabit;

import models.Analytic;
import models.FormatHelper;
import models.Tracker;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import controllers.UtilDat;

public class TrackerActivity extends Activity {
	private TextView tvName;//Name of Tracker
	private Spinner spinner;//Type of Tracker
	private CheckBox cbEnabled; //are reminders enabled?
	private TextView tvNumReminders; //number of reminders
	private TextView tvRemindCycle; //hours,days,weeks selector
	private TextView tvStartTime; //time to start reminders
	private TextView tvStartDate; //date to start reminders
	private String[] items; //list of tracker types from array
	private RelativeLayout rlTimer; //timer layout
	private EditText etSecs;
	private EditText etMins;
	private EditText etHours;
	private TextView tvTimerControl;
	private int trackerID; //tracker id from  intent, new is 0
	private Tracker t;
	private FormatHelper f;
	private DateTime dt;
	
	 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        Intent i = getIntent();
        trackerID = i.getIntExtra("TrackerRowID", 0);
        t = new Tracker(this, trackerID);
        Analytic a = new Analytic(this);
        a.logPageView("Tracker Setup");
        a.logTracker(t);
        f=new FormatHelper(this);
        dt = new DateTime();
		
        
        findViewItems();
        setView();
        setupListeners();
        reminderListeners();
	}
	public void reminderListeners(){
		tvStartDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
        	  	int mYear = dt.getYear();
        	    int mMonth = dt.getMonthOfYear()-1;
        	    int mDay = dt.getDayOfMonth();
        	    DatePickerDialog dtDialog = new DatePickerDialog(TrackerActivity.this, 
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
        	    
        	    TimePickerDialog tpDialog = new TimePickerDialog(TrackerActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
		tvRemindCycle.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
				//final CharSequence[] items = {"Hours", "Days", "Weeks"};
		
				AlertDialog.Builder alert = new AlertDialog.Builder(TrackerActivity.this);
				alert.setTitle(getResources().getString(R.string.set_cycle));
				alert.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	tvRemindCycle.setText(f.underline((String) items[item]));
				    	t.setReminderCycle(item);
				    }
				});
				alert.show();
	          }
   		});

		tvNumReminders.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	        	  AlertDialog.Builder alert = new AlertDialog.Builder(TrackerActivity.this);
	        	  alert.setTitle(getResources().getString(R.string.set_reminders));
	        	  final EditText input = new EditText(TrackerActivity.this);
	        	  input.setText(tvNumReminders.getText().toString());
	        	  input.setInputType(InputType.TYPE_CLASS_NUMBER);
	        	  alert.setView(input);
	        	  alert.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        		  tvNumReminders.setText(f.underline(input.getText().toString()));
	        	    }
	        	  });
	        	  alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int whichButton) {
	        	      // Canceled.
	        	    }
	        	  });
	        	  alert.show();
	          }
		});
		tvName.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	        	  final String title = getResources().getString(R.string.set_title);
	        	  AlertDialog.Builder alert = new AlertDialog.Builder(TrackerActivity.this);
	        	  alert.setTitle(title);
	        	  final EditText input = new EditText(TrackerActivity.this);
	        	  String setTV = tvName.getText().toString();
	        	  if (setTV == title) setTV = "";
	        	  input.setText(setTV);
	        	  alert.setView(input);
	        	  alert.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        		  String text = input.getText().toString();
	        		  text.trim();
	        		  if (text.length()==0) text = title; 
	        		  tvName.setText(f.underline(text));
	        	    }
	        	  });
	        	  alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int whichButton) {
	        	      // Canceled.
	        	    }
	        	  });
	        	  alert.show();
	          }
		});
	}
 	public void setupListeners(){
    	Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new MySpinnerListener());
        
        final TextView tvTrackerSave = (TextView) findViewById(R.id.tvTrackerSave);
        tvTrackerSave.setText(f.underline(getResources().getString(R.string.save)));
        tvTrackerSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	submitData();
            }
        });
        TextView tvTrackerCancel = (TextView)findViewById(R.id.tvTrackerCancel);
        tvTrackerCancel.setText(f.underline(getResources().getString(R.string.cancel)));
        tvTrackerCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        cbEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener()
    	{
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    	    {
    	        setEnabled(isChecked);
    	    }
    	});
        tvTimerControl.setText(f.underline(getResources().getString(R.string.start_timer)));
        tvTimerControl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	UtilDat.timerControl(TrackerActivity.this,etSecs,etMins,etHours,tvTimerControl);
            }
        });
    }
	private void submitData(){
		t.setName((String) tvName.getText().toString());
		t.setType(spinner.getSelectedItemPosition());
		t.setReminderEnabled(cbEnabled.isChecked()?1:0);
		t.setReminderFreq(Integer.parseInt(tvNumReminders.getText().toString()));
		
		DateTime c = DateTime.now();
		t.setLastUpdate(c.getMillis());
		t.setNextReminder(f.strToMillis(tvStartDate.getText().toString(), tvStartTime.getText().toString()));
		t.submitData(this);
		finish();
	}
	private void setEnabled(boolean val){
		//sets view items for reminders as visible or not depending on whether the checkbox is checked
		
		cbEnabled.setChecked(val);
		
		int value=8;//Gone
		if (val) value=0;//VISIBLE
		
		tvNumReminders.setVisibility(value);
		tvRemindCycle.setVisibility(value);
		tvStartTime.setVisibility(value);
		tvStartDate.setVisibility(value);
		TextView textView5 =(TextView) findViewById(R.id.textView5); 
		TextView tvStartTimeLabel =(TextView) findViewById(R.id.tvStartTimeLabel);
		TextView tvStartDateLabel=(TextView) findViewById(R.id.tvStartDateLabel);
		textView5.setVisibility(value);
		tvStartTimeLabel.setVisibility(value);
		tvStartDateLabel.setVisibility(value);
	}
	private void setView(){
		//sets view from tracker
		tvName.setText(f.underline(t.getName()));
		Log.d("past that line","yes I am");
		spinner.setSelection(t.getType());
    	if (t.getReminderEnabled()>0)setEnabled(true);
		else setEnabled(false);
		tvNumReminders.setText(f.underline(String.valueOf(t.getReminderFreq())));
		String strCycle = (String) items[t.getReminderCycle()];
		tvRemindCycle.setText(f.underline(strCycle));
		long nextReminder = t.getNextReminder();
		tvStartTime.setText(f.underline(f.milliToTime(nextReminder)));
		tvStartDate.setText(f.underline(f.milliToDate(nextReminder)));
	}
	private void findViewItems(){
		tvName = (TextView) findViewById(R.id.tvName);
		spinner = (Spinner) findViewById(R.id.spinner1);
		cbEnabled = (CheckBox) findViewById(R.id.cbEnabled);
		tvNumReminders= (TextView) findViewById(R.id.tvNumReminders);
		tvRemindCycle= (TextView) findViewById(R.id.tvRemindCycle);
		tvStartTime= (TextView) findViewById(R.id.tvStartTime);
		tvStartDate= (TextView) findViewById(R.id.tvStartDate);
    	rlTimer = (RelativeLayout)findViewById(R.id.rlTimer);
    	etSecs = (EditText)findViewById(R.id.etSecs);
    	etMins = (EditText)findViewById(R.id.etMins);
    	etHours = (EditText)findViewById(R.id.etHours);
    	tvTimerControl = (TextView)findViewById(R.id.tvTimerControl);
    	items = getResources().getStringArray(R.array.reminder_frequencies);
	}
	private class MySpinnerListener implements OnItemSelectedListener {
        TextView tv = (TextView) findViewById(R.id.chose_label);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        RatingBar rb = (RatingBar)findViewById(R.id.ratingBar1);
        SeekBar sb = (SeekBar)findViewById(R.id.seekBar1);
        EditText et = (EditText)findViewById(R.id.editText2);
        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);
        TextView b3 = (TextView)findViewById(R.id.btnLogger);
        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
        	UtilDat.setInputControl(rlTimer, pos, tv, tv2, rb, sb, et, rg, b3);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        	UtilDat.setInputControl(rlTimer,-1, tv, tv2, rb, sb, et, rg, b3);
        }
    }
}