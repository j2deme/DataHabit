package com.rdpharr.DataHabit;

import models.dbAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.apps.analytics.easytracking.TrackedActivity;

public class DashboardActivity extends TrackedActivity {
	private dbAdapter mDbHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dashboard);
	    mDbHelper = new dbAdapter(this);
        mDbHelper.open();
	    
	    //start reminder service
        AlarmMgr.startAlarm(this);
	 
	    //attach event handler to dash buttons
	    DashboardClickListener dBClickListener = new DashboardClickListener();
	    findViewById(R.id.dashboard_button_export).setOnClickListener((DashboardClickListener) dBClickListener);
	    findViewById(R.id.dashboard_button_help).setOnClickListener((DashboardClickListener) dBClickListener);
	    findViewById(R.id.dashboard_button_trackers).setOnClickListener((DashboardClickListener) dBClickListener);
	    findViewById(R.id.dashboard_button_settings).setOnClickListener((DashboardClickListener) dBClickListener);
	}
	private class DashboardClickListener implements android.view.View.OnClickListener {
	    public void onClick(View v) {
	        Intent i = null;
	        switch (v.getId()) {
	            case R.id.dashboard_button_trackers:
	            	i = new Intent(DashboardActivity.this, TrackerListActivity.class);
	                break;
	            case R.id.dashboard_button_export:
	            	i = new Intent(DashboardActivity.this, ExportDataActivity.class);
	            	break;
	            case R.id.dashboard_button_help:
	            	i = new Intent(DashboardActivity.this, InfoActivity.class);
	                break;
	            case R.id.dashboard_button_settings:
	            	i = new Intent(DashboardActivity.this, SettingsActivity.class);
	                break;
	            default:
	                break;
	        }
	        if(i != null) {
	            startActivity(i);
	        }
	    }
	}
}
