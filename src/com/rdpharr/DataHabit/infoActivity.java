package com.rdpharr.DataHabit;

import android.os.Bundle;

import com.google.android.apps.analytics.easytracking.TrackedActivity;

public class infoActivity extends TrackedActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.info);
	}

}
