package com.rdpharr.DataHabit;

import models.Analytic;
import models.FormatHelper;
import models.dbAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import controllers.ExportData;

public class ExportDataActivity extends ListActivity {
	CheckBox cbSeparateDateTime, cbIncludeLastUpdate;
	TextView tvSelectAll, tvSelectNone, tvCancel, tvExport;
	ListView lv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.export_data);
	    
	    //log pageview
	    Analytic a = new Analytic(this);
	    a.logPageView("Export Data");
	    
	    findViewItems();
	    fillList();
	    setCheckBoxes();
	    tvExport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	boolean[] options = {
            			cbSeparateDateTime.isChecked(),
            			cbIncludeLastUpdate.isChecked()
            	};
            	exportData(options);
            }
		});
	    tvCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	for(int i=0;i<lv.getCount();i++){
                	finish();
            	}
            }
		});
		tvSelectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	for(int i=0;i<lv.getCount();i++){
                	lv.setItemChecked(i, true);
            	}
            }
		});
		tvSelectNone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	for(int i=0;i<lv.getCount();i++){
                	lv.setItemChecked(i, false);
            	}
            }
		});
	}
	private void setCheckBoxes(){
		final boolean[] options = {false, true};
	    SharedPreferences settings = getSharedPreferences("exportSettings", 0);
	    options[0]=settings.getBoolean("seperateDateTime", false);
	    options[1]=settings.getBoolean("includeLastUpdate", true);
	    
	    cbSeparateDateTime.setChecked(options[0]);
		cbIncludeLastUpdate.setChecked(options[1]);
		
		cbSeparateDateTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SharedPreferences settings = getSharedPreferences("exportSettings", 0);
        	    SharedPreferences.Editor editor = settings.edit();
        	    editor.putBoolean("seperateDateTime", 
        	    		cbSeparateDateTime.isChecked());
        	    editor.commit();
            }
        });
		cbIncludeLastUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SharedPreferences settings = getSharedPreferences("exportSettings", 0);
        	    SharedPreferences.Editor editor = settings.edit();
        	    editor.putBoolean("includeLastUpdate", 
        	    		cbIncludeLastUpdate.isChecked());
        	    editor.commit();
            }
        });
	}
	private void fillList() {
        dbAdapter mDbHelper = new dbAdapter(this);
        mDbHelper.open();
		Cursor c = mDbHelper.fetchAllTrackers();
        String[] name_str = new String[] { dbAdapter.KEY_Tracker_Name };
        int[] name_field = new int[] { android.R.id.text1 };
        SimpleCursorAdapter tracker_names =
                new SimpleCursorAdapter(this, 
                		android.R.layout.simple_list_item_multiple_choice, 
                		c, 
                		name_str,
                		name_field);
        setListAdapter(tracker_names);
    }
	private void findViewItems(){
		FormatHelper f = new FormatHelper(this);
		cbIncludeLastUpdate = (CheckBox) findViewById(R.id.cbIncludeLastUpdate);
		cbSeparateDateTime = (CheckBox) findViewById(R.id.cbSeparateDateTime);
		tvSelectNone = (TextView) findViewById(R.id.tvSelectNone);
		tvSelectAll = (TextView) findViewById(R.id.tvSelectAll);
		tvCancel = (TextView) findViewById(R.id.tvCancel);
		tvExport = (TextView) findViewById(R.id.tvExport);
		lv=getListView();
		
		//underline text views
		tvSelectNone.setText(f.underline((String) tvSelectNone.getText()));
		tvSelectAll.setText(f.underline((String) tvSelectAll.getText()));
		tvCancel.setText(f.underline((String) tvCancel.getText()));
		tvExport.setText(f.underline((String) tvExport.getText()));
		
	}
	private void exportData(boolean[] options){
		long[] selectedTrackers = lv.getCheckedItemIds();
		if(selectedTrackers.length>0){
			String outFile = ExportData.exportSomeData(options, this, selectedTrackers);
			Intent email = new Intent(android.content.Intent.ACTION_SEND);
	  		email.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.eMailSubject)); 
	  		email.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.eMailText)); 
	  		email.setType("application/octet-stream");
	  		email.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(outFile));
	  		this.startActivity(email);
	  		finish();			
		} else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(this.getString(R.string.NoTrackersSelected))
				.setCancelable(false)
				.setPositiveButton(this.getString(R.string.OK)
		    		   , new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //Do nothing. Alert only
		           }
		       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		
	}
}