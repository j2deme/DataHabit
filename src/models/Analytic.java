package models;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.rdpharr.DataHabit.R;


public class Analytic {

	String acctID = "UA-29616011-1";//account ID from Google Analytics
	Integer DispatchInterval = 30;//seconds
	
	Context c;
	GoogleAnalyticsTracker analytic;
	
	/** Sends data to Google analytics account. 
	 * 
	 * @param pageName Human readable name of page calling
	 * @param ctx Activity context
	 * @param t Tracker object to pull data from. Set to null if N/A
	 */
	public Analytic(Context ctx){
		c=ctx;
		analytic = GoogleAnalyticsTracker.getInstance();
		analytic.startNewSession(acctID, DispatchInterval, ctx);
	}
	/**
	 * Logs a pageview
	 * @param pageName Name of page
	 */
	public void logPageView(String pageName){
		analytic.trackPageView(pageName);
	}
	/**
	 * Logs Tracker data via custom variable
	 * @param t Tracker to pull data from
	 */
	public void logTracker (Tracker t){
		analytic.setCustomVar(1, "Tracker Name", t.getName());
		String[] mTestArray = c.getResources().getStringArray(R.array.input_types); 
		analytic.setCustomVar(1, "Tracker Type", mTestArray[t.getType()]);
		analytic.setCustomVar(1, "Reminders Enabled", Integer.toString(t.getReminderEnabled()));
	}

}
