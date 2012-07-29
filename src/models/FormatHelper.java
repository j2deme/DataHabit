package models;

import java.text.DateFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

/**
 * Class to assist with Date/Time formats according to shared prefs
 * 0=DateFormat.FULL, 1=LONG, 2=MEDIUM, 3=SHORT
 * 4= Sortable Time (default)
 * 
 * @author roger pharr
 *
 */
public class FormatHelper {
	int dateLength, timeLength;
	String sortableDate = "yyyy-MM-dd";
	String sortableTime = "HH:mm:ss"; 
	String s;//return string
	/**
	 * Number of options available for date time formats
	 */
	public int options = 4; //number of options
	/**
	 * default option for datetime format
	 */
	public int defaultOption = 4;

	public FormatHelper(Context ctx){
		//get type from settings
		SharedPreferences settings = ctx.getSharedPreferences("dateFormat", 0);
		dateLength = settings.getInt("formatType", defaultOption);
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
	public long strToMillis (String d, String t) {
		long l=0;
		DateTime dt = DateTime.parse(d+" "+t);
		l = dt.getMillis();
//			Date date = null, time = null;
//			date = DateFormat.getDateInstance(dateLength).parse(d);
//			time = DateFormat.getTimeInstance(timeLength).parse(t);
//			date.setHours(time.getHours());
//			date.setMinutes(time.getMinutes());
//			date.setSeconds(time.getSeconds());
//			l=date.getTime();
		return l;
	}
	public String milliToDateTime(long milli){
		if(dateLength<4){
			s = DateFormat.getDateInstance(dateLength).format(milli);
			s = s + " "+ DateFormat.getTimeInstance(timeLength).format(milli);
		}else{
			DateTime c = new DateTime().withMillis(milli);
			s = c.toString(sortableDate+" "+sortableTime);
		}
		return s;
	}
	public String milliToDate(long milli){
		if(dateLength<4){
			s = DateFormat.getDateInstance(dateLength).format(milli);
		}else{
			DateTime c = new DateTime().withMillis(milli);
			s = c.toString(sortableDate);
		}
		return s;
	}
	public String milliToTime(long milli){
		if(dateLength<4){
			s = DateFormat.getTimeInstance(timeLength).format(milli);
		}else{
			DateTime c = new DateTime().withMillis(milli);
			s = c.toString(sortableTime);
		}
		return s;
	}
	public String toString(){
		DateTime dt = new DateTime();
		return milliToDateTime(dt.getMillis());
	}
}