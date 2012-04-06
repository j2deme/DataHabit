package com.rdpharr.DataHabit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.easytracking.TrackedActivity;

public class settingsActivity extends TrackedActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        setDefaults();
        setupListeners();
        
	}
	private void setDefaults(){
		CheckBox sound = (CheckBox) findViewById(R.id.notification_sound);
		CheckBox vibrate = (CheckBox) findViewById(R.id.notification_vibrate);
		
		SharedPreferences settings = getSharedPreferences("notifications", 0);
		if (settings.getInt("sound", 0) ==1) sound.setChecked(true);
		if (settings.getInt("vibrate", 0) ==1) vibrate.setChecked(true);
		
	}
	private void submitData(){
		CheckBox sound = (CheckBox) findViewById(R.id.notification_sound);
		CheckBox vibrate = (CheckBox) findViewById(R.id.notification_vibrate);
		
		SharedPreferences settings = getSharedPreferences("notifications", 0);
		SharedPreferences.Editor editor = settings.edit();
	      
		if(sound.isChecked()){
			editor.putInt("sound", 1);
		}else{
			editor.putInt("sound", 0);
		}
		if(vibrate.isChecked()){
			editor.putInt("vibrate", 1);
		}else{
			editor.putInt("vibrate", 0);
		}
		editor.commit();
		Toast toast = Toast.makeText(getApplicationContext(), 
				getResources().getString(R.string.settings_saved), 
				Toast.LENGTH_SHORT);
		toast.show();
    	finish();
	}
	private void setupListeners(){
        final TextView tvTrackerSave = (TextView) findViewById(R.id.tvTrackerSave);
        tvTrackerSave.setText(helper.underline("Save"));
        tvTrackerSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	submitData();
            }
        });
        TextView tvTrackerCancel = (TextView)findViewById(R.id.tvTrackerCancel);
        tvTrackerCancel.setText(helper.underline("Cancel"));
        tvTrackerCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
}
