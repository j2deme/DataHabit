package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

/**
 * Class to assist with Date/Time formats according to shared prefs
 * @author roger pharr
 *
 */
public class FormatHelper {
	int dateLength, timeLength; //0=DateFormat.FULL, 1=LONG, 2=MEDIUM, 3=SHORT

	public FormatHelper(Context ctx){
		//get type from settings
		SharedPreferences settings = ctx.getSharedPreferences("dateFormat", 0);
		dateLength = settings.getInt("formatType", 0);
		if (dateLength==0){
			timeLength=1;
		}else timeLength = dateLength;
	}
	public FormatHelper(int option){
		dateLength = option;
		if (dateLength==0){
			timeLength=1;
		}else timeLength = dateLength;
	}
	public SpannableString underline(String s){
		SpannableString content = new SpannableString(s);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		return content;
	}
	@SuppressWarnings("deprecation")
	public long strToMillis (String d, String t) {
		long l=0;
		try {
			Date date = null, time = null;
			date = DateFormat.getDateInstance(dateLength).parse(d);
			time = DateFormat.getTimeInstance(timeLength).parse(t);
			date.setHours(time.getHours());
			date.setMinutes(time.getMinutes());
			date.setSeconds(time.getSeconds());
			l=date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return l;
	}
	public String milliToDateTime(long milli){
		String s = DateFormat.getDateInstance(dateLength).format(milli);
		s = s + " "+ DateFormat.getTimeInstance(timeLength).format(milli);
		return s;
	}
	public String milliToDate(long milli){
		String s = DateFormat.getDateInstance(dateLength).format(milli);
		return s;
	}
	public String milliToTime(long milli){
		String s = DateFormat.getTimeInstance(timeLength).format(milli);
		return s;
	}
	public String toString(){
		DateTime dt = new DateTime();
		return milliToDateTime(dt.getMillis());
	}
}