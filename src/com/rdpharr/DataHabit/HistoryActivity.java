package com.rdpharr.DataHabit;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.google.android.apps.analytics.easytracking.TrackedListActivity;


public class HistoryActivity extends TrackedListActivity {
	private dbAdapter mDbHelper;
	private int trackerID;
	private int trackerType;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new dbAdapter(this);
        mDbHelper.open();
        Intent i = getIntent();
        trackerID = i.getIntExtra("TrackerRowID", 0);
        setContentView(R.layout.history_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TextView tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvEmail.setText(helper.underline(this.getString(R.string.email_data)));
        if (trackerID==0){

        }else{
	        trackerType = dbHelper.getTrackerType(getApplicationContext(), trackerID);
	        
	        fillList();
	        
	        //context menu
	        ListView lv = getListView();
	        registerForContextMenu(lv);
	        lv.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	            	goToData(position);
	            }
	          });
	                
	        tvEmail.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
					try {
						saveAndEmail();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        });
        }
    }
	private void fillList(){
        //get history and place in list
        mDbHelper = new dbAdapter(this);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAllData(trackerID);
        startManagingCursor(c);
        String[] value_str = new String[] { 
        		dbAdapter.KEY_Data_Value, 
        		dbAdapter.KEY_Data_TimeStamp,
        		dbAdapter.KEY_Data_Notes};
        int[] value_field = new int[] { R.id.history_value, R.id.history_date, R.id.history_comment };
        SimpleCursorAdapter history_data =
                new SimpleCursorAdapter(this, R.layout.history_listview, c, value_str, value_field);
        history_data.setViewBinder(new ViewBinder() {
            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
            	switch (aColumnIndex){
            		case 2:
	            		long createDate = aCursor.getLong(aColumnIndex);
	                    TextView tvDate = (TextView) aView;
	                    tvDate.setText(helper.milliToStr(createDate));
	                    return true;
	                case 3:
	            		float value = aCursor.getFloat(aColumnIndex);
	            		TextView tvValue = (TextView) aView;
	            		tvValue.setText(utilDat.getValueString(HistoryActivity.this, trackerType, value));
	                    return true;
	                default:
	                	TextView tvComment = (TextView) aView;
	                	String comment =  aCursor.getString(aColumnIndex);
	                	tvComment.setText(comment);
	                	return true;
            	}
            }
        });
        setListAdapter(history_data);
	}
	private void saveAndEmail() throws IOException{
		//Make file
		String strFile = fileHelper.trackerCsvHeader(this) +fileHelper.trackerCsv(this, trackerID);
		String outFile = fileHelper.makeFile("data.csv", strFile);
		
		//send email
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.eMailSubject)); 
		email.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.eMailText)); 
		email.setType("application/octet-stream");
		email.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(outFile));
		this.startActivity(email);
		
	}
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_context, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
            	goToData(info.position);
            	return true;
            case R.id.delete:
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	builder.setMessage(this.getString(R.string.delete_data_dialog))
            	       .setCancelable(false)
            	       .setPositiveButton((this.getString(R.string.yes)), new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   deletePoint(info.position);
            	        	   fillList();
            	           }
            	       })
            	       .setNegativeButton((this.getString(R.string.no)), new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	AlertDialog alert = builder.create();
            	alert.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void deletePoint(int position){
    	Cursor c = mDbHelper.fetchAllData(trackerID);
        startManagingCursor(c);
        c.moveToPosition(position);
    	int dataRowID = c.getInt(0);
    	mDbHelper.deleteData(dataRowID);
    }
    public void goToData(int position){
    	int dataRowID = dbHelper.getDataRowId(getApplicationContext(), trackerID, position);//c.getInt(0);
    	Intent i = new Intent(HistoryActivity.this, tabTrackerActivity.class);
    	i.putExtra("TrackerRowID", trackerID);
    	i.putExtra("dataRowID", dataRowID);
    	HistoryActivity.this.startActivity(i);
    }
}