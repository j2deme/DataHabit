package com.rdpharr.DataHabit;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class TabTrackerActivity extends TabActivity {
	private int trackerID;
	private int tabID;
	private int dataID;
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_tracker);
	    Intent i = getIntent();
	    trackerID = i.getIntExtra("TrackerRowID", 0);
	    tabID = i.getIntExtra("tabID", -1);
	    dataID = i.getIntExtra("dataRowID", 0);
	    
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    intent = new Intent().setClass(this, DataLoggerActivity.class);
	    intent.putExtra("TrackerRowID", trackerID);
	    intent.putExtra("dataRowID", dataID);
	    spec = tabHost.newTabSpec("Add Data").setIndicator("Add Data",
	                      res.getDrawable(R.drawable.ti_check))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, TrackerActivity.class);
	    intent.putExtra("TrackerRowID", trackerID);
	    spec = tabHost.newTabSpec("Settings").setIndicator("Settings",
	                      res.getDrawable(R.drawable.ti_tools))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, HistoryActivity.class);
	    intent.putExtra("TrackerRowID", trackerID);
	    spec = tabHost.newTabSpec("History").setIndicator("History",
	                      res.getDrawable(R.drawable.ti_list))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(tabID);
	}
}