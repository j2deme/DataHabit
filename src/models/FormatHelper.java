package models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

public class FormatHelper {
	private String date, time, timeNoSecs;

	public FormatHelper(Context ctx){
		//get type from settings
		SharedPreferences settings = ctx.getSharedPreferences("dateFormat", 0);
		int i = settings.getInt("formatType", 0);
		setFormat(i);
	}
	public FormatHelper(int option){
		setFormat(option);
	}
	public SpannableString underline(String s){
		SpannableString content = new SpannableString(s);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		return content;
	}
	public long strToMillis (String d, String t){
		DateTime c;
		DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		c = sdf.parseDateTime(d+" "+t);
		return c.getMillis();
	}
	public String milliToDateTime(long milli){
		DateTime c = new DateTime().withMillis(milli);
		String s = c.toString(date+" "+time);
		return s;
	}
	public String milliToDateTimeNoSecs(long milli){
		DateTime c = new DateTime().withMillis(milli);
		String s = c.toString(date+" "+timeNoSecs);
		return s;
	}
	public String milliToDate(long milli){
		DateTime c = new DateTime().withMillis(milli);
		String s = c.toString(date);
		return s;
	}
	public String milliToTime(long milli){
		DateTime c = new DateTime().withMillis(milli);
		String s = c.toString(time);
		return s;
	}
	private void setFormat(int option){
		//set type according to option #
		switch(option){
			case 0:
				//universal sortable time
				date = "yyyy-MM-dd";
				time="HH:mm:ss";
				timeNoSecs="HH:mm";
				break;
			case 1:
				//US w/o day name
				date = "MM-dd-yyyy";
				time="HH:mm:ss";
				timeNoSecs="HH:mm";
				break;
			case 2:
				//US with day name
				date = "ddd, MM-dd-yyyy";
				time="HH:mm:ss";
				timeNoSecs="HH:mm";
				break;
			case 3:
				//Non-US w/o day name
				date = "dd.MM.yyyy";
				time="HH:mm:ss";
				timeNoSecs="HH:mm";
				break;
			case 4:
				//Non-US w/ day name
				date = "ddd, dd.MM.yyyy";
				time="HH:mm:ss";
				timeNoSecs="HH:mm";
				break;
			default:
				date = "yyyy-MM-dd";
				time="HH:mm:ss";
				timeNoSecs="HH:mm";
				break;
		}
	}
}
