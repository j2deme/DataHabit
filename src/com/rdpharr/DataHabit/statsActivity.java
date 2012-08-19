package com.rdpharr.DataHabit;

import models.Analytic;
import models.Stats;
import models.Tracker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class statsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        int trackerID = i.getIntExtra("TrackerRowID", 0);
        setContentView(R.layout.stats);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (trackerID==0){
        	//default view. Somehow you got here without a tracker intent
        }else{
        	LinearLayout ll = (LinearLayout) findViewById(R.id.ll_stats);
        	TextView tv = (TextView) findViewById(R.id.title_text);
        	Stats s = new Stats(trackerID, ll,tv, statsActivity.this);
        	Tracker t=new Tracker(this, trackerID);
        	Analytic a = new Analytic(this);
        	a.logPageView("Statistics");
        	a.logTracker(t);
        	s.show();
        }
	}
}
