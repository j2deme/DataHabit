package models;

import java.text.DecimalFormat;

import org.joda.time.DateTime;

import com.rdpharr.DataHabit.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Stats {
	private dbAdapter mDbHelper;
	private Tracker t;
	private LinearLayout ll;
	private TextView titleTv;
	private Context ctx;
	private double n7,n30,nAll; //count
	private double e7,e30,eAll; //sum 
	public Stats(int trackerId, LinearLayout layout, TextView title, Context activity){
		t = new Tracker(activity, trackerId);
		ll = layout;
		ctx=activity;
		titleTv = title;
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
		
		titleTv.setText(t.getName()+" "+ctx.getString(R.string.Statistics));
		
		//create 7day section
		addTextView(ctx.getString(R.string.Last7Days), true);
		addTextView(ctx.getString(R.string.NumDataPoints)+":"+df0.format(n7), false);
		String s = "";
		if (n7>0){s = myFormat(e7/n7);}
			else{s="N/A";}
		addTextView(ctx.getString(R.string.AvgDataPoints)+":"+s, false);
		addTextView(ctx.getString(R.string.SumDataPoints)+":"+myFormat(e7), false);
		addTextView("",false);
		
		//header, number of data points, average, total
		//create 30 day section
		addTextView(ctx.getString(R.string.Last30Days), true);
		addTextView(ctx.getString(R.string.NumDataPoints)+":"+df0.format(n30), false);
		if (n30>0){s = myFormat(e30/n30);}
			else{s="N/A";}
		addTextView(ctx.getString(R.string.AvgDataPoints)+":"+s, false);
		addTextView(ctx.getString(R.string.SumDataPoints)+":"+myFormat(e30), false);
		addTextView("",false);
		
		//create all time section
		addTextView(ctx.getString(R.string.AllTime), true);
		addTextView(ctx.getString(R.string.NumDataPoints)+":"+df0.format(nAll), false);
		if (nAll>0){s = myFormat(eAll/nAll);}
		else{s="N/A";}
		addTextView(ctx.getString(R.string.AvgDataPoints)+":"+s, false);
		addTextView(ctx.getString(R.string.SumDataPoints)+":"+myFormat(eAll), false);
		addTextView("",false);
	}
	private String myFormat(Double d){
		String s="";
		DecimalFormat df1 = new DecimalFormat("#.#");
		DecimalFormat df2 = new DecimalFormat("#.##");
		switch (t.getType()){
			case 3:
				s=df2.format(d)+" "+ctx.getString(R.string.yes);
				break;
			case 5:
				d=d/(1000*60);
				s=df1.format(d)+" "+ctx.getString(R.string.minutes);
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
        
        long days = 24*60*60*1000; //one day in millis
        long daysAgo7 = new DateTime().getMillis();
        daysAgo7 = daysAgo7 - (7*days);
        long daysAgo30 = new DateTime().getMillis();
        daysAgo30 = daysAgo30 - (30*days);
        
        for (int i = 0; i<c.getCount(); i++){
        	c.moveToPosition(i);
        	
        	long dateVal = c.getLong(2);
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