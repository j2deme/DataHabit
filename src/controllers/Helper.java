package controllers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

public class Helper {
	public static SpannableString underline(String s){
		SpannableString content = new SpannableString(s);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		return content;
	}
	public static String calToTime (DateTime c){
		String t;
		DateTimeFormatter sdf = DateTimeFormat.forPattern("HH:mm");
		t = sdf.print(c);
		return t;
	}
	public static String calToDate (DateTime c){
		String d;
		DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy-MM-dd");
		d = sdf.print(c);
		return d;
	}
	public static DateTime strToCal (String d, String t){
		DateTime c;
		DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		c = sdf.parseDateTime(d+" "+t);
		return c;
	}
	public static long strToMillis (String d, String t){
		DateTime c;
		DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		c = sdf.parseDateTime(d+" "+t);
		return c.getMillis();
	}
	public static String milliToStr (long milli){
		DateTime c = new DateTime().withMillis(milli);
		String s = c.toString("yyyy-MM-dd HH:mm:ss");
		return s;
	}
}