package com.rdpharr.DataHabit;

import models.StatsTable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
        	StatsTable s = new StatsTable(trackerID, ll, statsActivity.this);
        	s.show();
        }
	}
}
