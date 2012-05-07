package com.rdpharr.DataHabit;

import models.Tracker;
import models.dbAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.apps.analytics.easytracking.TrackedListActivity;

import controllers.Helper;

public class TrackerListActivity extends TrackedListActivity {
	private dbAdapter mDbHelper;
	private int trackerId;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // fill list
        mDbHelper = new dbAdapter(this);
        mDbHelper.open();
        fillList();
                
        final TextView tvNewMonitor = (TextView) findViewById(R.id.tvNewMonitor);
        tvNewMonitor.setText(Helper.underline(this.getString(R.string.new_tracker)));
        tvNewMonitor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(TrackerListActivity.this, TabTrackerActivity.class);
            	i.putExtra("tabID", 1);
            	TrackerListActivity.this.startActivity(i);
            }
        });
        ListView lv = getListView();
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
            	goToLogger(position);
            }
          });
    }
    private void fillList() {
		Cursor c = mDbHelper.fetchAllTrackers();
        startManagingCursor(c);
        String[] name_str = new String[] { dbAdapter.KEY_Tracker_Name };
        int[] name_field = new int[] { R.id.tvTitle };
        SimpleCursorAdapter tracker_names =
                new SimpleCursorAdapter(this, R.layout.main_listview, c, name_str, name_field);
        setListAdapter(tracker_names);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.open:
            	goToLogger(info.position);
            	return true;
            case R.id.edit:
            	goToTracker(info.position);
            	return true;
            case R.id.delete:
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	builder.setMessage(this.getString(R.string.delete_tracker_dialog))
            	       .setCancelable(false)
            	       .setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   deleteTracker(info.position);
            	        	   fillList();
            	           }
            	       })
            	       .setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	AlertDialog alert = builder.create();
            	alert.show();
                return true;
            case R.id.history:
            	goToHistory(info.position);
            	return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void goToTracker (int position){
    	Intent i = new Intent(TrackerListActivity.this, TabTrackerActivity.class);
    	i.putExtra("TrackerRowID", getTrackerId(position));
    	i.putExtra("tabID", 1);
    	TrackerListActivity.this.startActivity(i);
    }
    public void goToLogger(int position){
    	Intent i = new Intent(TrackerListActivity.this, TabTrackerActivity.class);
    	i.putExtra("TrackerRowID", getTrackerId(position));//
    	i.putExtra("tabID", 0);
    	TrackerListActivity.this.startActivity(i);
    }
    public void deleteTracker(int position){
    	Tracker t = new Tracker(this, getTrackerId(position));
    	t.delete(this);
    }
    public void goToHistory(int position){
    	Intent i = new Intent(TrackerListActivity.this, TabTrackerActivity.class);
    	i.putExtra("TrackerRowID", getTrackerId(position));//
    	i.putExtra("tabID", 2);
    	TrackerListActivity.this.startActivity(i);
    }
    private int getTrackerId(int position){
    	Cursor c = mDbHelper.fetchAllTrackers();
        startManagingCursor(c);
        c.moveToPosition(position);
        int trackerNum = c.getInt(0);
        return trackerNum;
    }
}