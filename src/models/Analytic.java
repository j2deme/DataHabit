package models;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


public class Analytic {

	String acctID = "UA-29616011-1";//account ID from Google Analytics
	Integer DispatchInterval = 30;//seconds
	
	/** Sends data to Google analytics account. 
	 * 
	 * @param pageName Human readable name of page calling
	 * @param ctx Activity context
	 * @param t Tracker object to pull data from. Set to null if N/A
	 */
	public Analytic(String pageName, Context ctx, Tracker t){
		GoogleAnalyticsTracker analytic = GoogleAnalyticsTracker.getInstance();
		analytic.startNewSession(acctID, DispatchInterval, ctx);
		analytic.trackPageView(pageName);
		if (t!=null) {
			analytic.setCustomVar(1, "Tracker Name", t.getName());
			analytic.setCustomVar(2, "Tracker Type", Integer.toString(t.getType()));
			analytic.setCustomVar(3, "Reminders Enabled", Integer.toString(t.getReminderEnabled()));
		}
	}

}
