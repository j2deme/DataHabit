package models;

import java.text.DecimalFormat;

import org.joda.time.DateTime;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatsTable {
	private dbAdapter mDbHelper;
	private Tracker t;
	private LinearLayout ll;
	private Context ctx;
	private double n7,n30,nAll; //count
	private double e7,e30,eAll; //sum 
	public StatsTable(int trackerId, LinearLayout layout, Context activity){
		t = new Tracker(activity, trackerId);
		ll = layout;
		ctx=activity;
        e7=0;
        e30=0;
        eAll=0;
        n7=0;
        n30=0;
        nAll=0;
	}
	public void show(){
		fillCalcs();
		DecimalFormat df0 = new DecimalFormat("#");
		
		addTextView(t.getName()+" Statistics", true);
		addTextView("", true);
		
		//create 7day section
		addTextView("Last 7 Days", true);
		addTextView("Number of Data Points="+df0.format(n7), false);
		String s = "";
		if (n7>0){s = myFormat(e7/n7);}
			else{s="N/A";}
		addTextView("Average of Data Points="+s, false);
		addTextView("Total of Data Points="+myFormat(e7), false);
		addTextView("",false);
		
		//header, number of data points, average, total
		//create 30 day section
		addTextView("Last 30 Days", true);
		addTextView("Number of Data Points="+df0.format(n30), false);
		if (n30>0){s = myFormat(e30/n30);}
			else{s="N/A";}
		addTextView("Average of Data Points="+s, false);
		addTextView("Total of Data Points="+myFormat(e30), false);
		addTextView("",false);
		
		//create all time section
		addTextView("All Time", true);
		addTextView("Number of Data Points="+df0.format(nAll), false);
		addTextView("Average of Data Points="+myFormat(eAll/nAll), false);
		addTextView("Total of Data Points="+myFormat(eAll), false);
		addTextView("",false);
	}
	private String myFormat(Double d){
		String s="";
		DecimalFormat df1 = new DecimalFormat("#.#");
		DecimalFormat df2 = new DecimalFormat("#.##");
		switch (t.getType()){
			case 3:
				s=df2.format(d)+" yes";
				break;
			case 5:
				d=d/(1000*60);
				s=df1.format(d)+" minutes";
				break;
			default:
				s=df1.format(d);
				break;
		}
		return s;
	}
	private void addTextView(String s, boolean bold){
		TextView tv = new TextView(ctx);
		tv.setText(s);
		tv.setPadding(7, 1, 7, 1);
		tv.setTextSize(2, 20);
		if (bold) tv.setTypeface(Typeface.DEFAULT_BOLD);
		ll.addView(tv);
	}
	private void fillCalcs(){
		mDbHelper = new dbAdapter(ctx);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAllData(t.getId());
        
        double now = new DateTime().getMillis();
        Log.d("now",String.valueOf(now));
        double daysAgo7 = now-(7*24*60*60*1000);
        Log.d("daysAgo7",String.valueOf(daysAgo7));
        double daysAgo30 = now +(30*24*60*60*1000);
        Log.d("daysAgo30",String.valueOf(daysAgo30));
        
        for (int i = 0; i<c.getCount(); i++){
        	c.moveToPosition(i);
        	
        	double dateVal = c.getDouble(2);
        	nAll+=1;
        	if (dateVal>=daysAgo30){
        		n30+=1;
        		if (dateVal>=daysAgo7)n7+=1;
        	}
        	
        	double value = c.getDouble(3);
        	eAll+=value;
        	if (dateVal>=daysAgo30){
        		e30+=value;
        		if (dateVal>=daysAgo7)e7+=value;
        	}
        }
        
        c.close();
        mDbHelper.close();
	}
}