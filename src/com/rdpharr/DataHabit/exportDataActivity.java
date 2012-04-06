package com.rdpharr.DataHabit;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.apps.analytics.easytracking.TrackedActivity;

public class exportDataActivity extends TrackedActivity {
	private dbAdapter mDbHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mDbHelper = new dbAdapter(this);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchAllTrackers();
        startManagingCursor(c);
        String strFile=fileHelper.trackerCsvHeader(this);
        for (int i=0; i<c.getCount(); i++){
        	c.moveToPosition(i);
        	int trackerID = c.getInt(0);
        	strFile = strFile + fileHelper.trackerCsv(this, trackerID);
        }
        String outFile = fileHelper.makeFile("data.csv", strFile);
      //send email
  		Intent email = new Intent(android.content.Intent.ACTION_SEND);
  		email.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.eMailSubject)); 
  		email.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.eMailText)); 
  		email.setType("application/octet-stream");
  		email.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(outFile));
  		this.startActivity(email);
  		mDbHelper.close();
  		finish();
	}
}
