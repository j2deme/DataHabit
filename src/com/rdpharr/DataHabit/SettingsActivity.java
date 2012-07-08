package com.rdpharr.DataHabit;

import models.FormatHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.easytracking.TrackedActivity;

public class SettingsActivity extends TrackedActivity {
	private CheckBox sound;
	private CheckBox vibrate;
	private TextView customSound;
	private String soundUri;
	FormatHelper f;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        getviewItems();
        setDefaults();
        setupListeners();
        
	}
	private void getviewItems() {
		sound = (CheckBox) findViewById(R.id.notification_sound);
		vibrate = (CheckBox) findViewById(R.id.notification_vibrate);
		customSound = (TextView) findViewById(R.id.tvCustomSound);
		customSound.setText(f.underline(getResources().getString(R.string.custom_sound)));
	}
	private void setDefaults(){
		SharedPreferences settings = getSharedPreferences("notifications", 0);
		if (settings.getInt("sound", 0) ==1) sound.setChecked(true);
		if (settings.getInt("vibrate", 0) ==1) vibrate.setChecked(true);
		soundUri = settings.getString("soundUri", null);
		
	}
	private void submitData(){
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
		editor.putString("soundUri", soundUri);
		editor.commit();
		Toast toast = Toast.makeText(getApplicationContext(), 
				getResources().getString(R.string.settings_saved), 
				Toast.LENGTH_SHORT);
		toast.show();
    	finish();
	}
	private void getTone(){
    	Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
    	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
    	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
    	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
    	this.startActivityForResult(intent, 5);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	 {
	     if (resultCode == Activity.RESULT_OK && requestCode == 5)
	     {
	          Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	          if (uri != null) soundUri = uri.toString();
	      }            
	  }
	private void setupListeners(){
		customSound.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	getTone();
            }
        });
        final TextView tvTrackerSave = (TextView) findViewById(R.id.tvTrackerSave);
        tvTrackerSave.setText(f.underline("Save"));
        tvTrackerSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	submitData();
            }
        });
        TextView tvTrackerCancel = (TextView)findViewById(R.id.tvTrackerCancel);
        tvTrackerCancel.setText(f.underline("Cancel"));
        tvTrackerCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
}