package com.rdpharr.DataHabit;

import models.Analytic;
import android.app.Activity;
import android.os.Bundle;

public class InfoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Analytic a = new Analytic(this);
	    a.logPageView("info");
	    setContentView(R.layout.info);
	}

}
