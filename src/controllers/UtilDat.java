package controllers;

import models.FormatHelper;
import models.StopWatch;

import org.joda.time.Duration;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.rdpharr.DataHabit.R;

public class UtilDat {
	public static void setInputControl(
			RelativeLayout rl, 
			int pos, 
			TextView tv, 
			TextView tv2,
			RatingBar rb,
			SeekBar sb, 
			EditText et,
			RadioGroup rg, 
			TextView b3  ){
        rb.setVisibility(8);
		sb.setVisibility(8);
		et.setVisibility(8);
		rg.setVisibility(8);
		b3.setVisibility(8);
		rl.setVisibility(8);
		if (tv != null)tv.setVisibility(0);
        switch (pos) {
	    	case -1:
	    		if (tv != null)tv.setVisibility(8);
	    		break;
    		case 0:
        		rb.setVisibility(0);
        		if (tv2 != null)tv2.setText(R.string.star_description);
        		break;
        	case 1:
        		sb.setVisibility(0);
        		if (tv2 != null)tv2.setText(R.string.slider_description);
        		break;
        	case 2:
        		et.setVisibility(0);
        		if (tv2 != null)tv2.setText(R.string.keyboard_description);
        		break;
        	case 3:
        		rg.setVisibility(0);
        		if (tv2 != null)tv2.setText(R.string.yesno_description);
        		break;
        	case 4:
        		b3.setVisibility(0);
        		if (tv2 != null)tv2.setText(R.string.pushbutton_description);
        		break;
        	case 5:
        		rl.setVisibility(0);
        		if (tv2 != null)tv2.setText(R.string.timer_description);
        		break;
        	default: 
         	   break;
        }
	}
	public static void setValue(
			EditText etSecs,
			EditText etMins,
			EditText etHours,
			int type, 
			double value, 
			RatingBar rb,
			SeekBar sb, 
			EditText et,
			RadioGroup rg, 
			TextView b3){
		switch (type) {
	    	case 0:
	    		rb.setRating((float) value);
	    		break;
	    	case 1:
	    		sb.setProgress((int) value);
	    		break;
	    	case 2:
	    		et.setText(String.valueOf(value));
	    		break;
	    	case 3:
	    		if (value==0){
	    			rg.check(R.id.radio0);
	    		} else{
	    			rg.check(R.id.radio1);
	    		}
	    		break;
	    	case 4:
	    		break;
	    	case 5:
	    		setTimerValue(etSecs,etMins,etHours, value);
	    		break;
			default:
				break;
 		}
	}
	public static double getValue(
			EditText etSecs,
			EditText etMins,
			EditText etHours,
			int type,
			RatingBar rb,
			SeekBar sb, 
			EditText et,
			RadioGroup rg, 
			TextView b3){
		double value=0;
		switch (type) {
	    	case 0:
	    		value = rb.getRating();
	    		break;
	    	case 1:
	    		value = sb.getProgress();
	    		break;
	    	case 2:
	    		value = Float.valueOf(et.getText().toString());
	    		break;
	    	case 3:
	    		if(rg.getCheckedRadioButtonId()==R.id.radio0){
	    			value = 0;
	    		}else{
	    			value=1;
	    		}
	    		break;
	    	case 4:
	    		value=1;
	    		break;
	    	case 5:
	    		value = getTimerValue(etSecs,etMins,etHours);
	    		break;
			default:
				break;
 		}
		return value;
	}
	private static double getTimerValue(
			EditText etSecs,
			EditText etMins,
			EditText etHours){
		double returnVal = 0;
		returnVal = returnVal + Double.valueOf(etSecs.getText().toString());
		returnVal = returnVal + Double.valueOf(etMins.getText().toString())*60;
		returnVal = returnVal + Double.valueOf(etHours.getText().toString())*3600;
		returnVal = returnVal * 1000; //toMillis
		return returnVal;
	}
	private static void setTimerValue(EditText etSecs, EditText etMins,
			EditText etHours, double value) {
		value = value/1000; //from millis
		//hours
		int hours = (int) (value / 3600);
		value = value - (hours*3600);
		etHours.setText(String.format("%02d", hours));
		
		//mins
		int mins = (int) (value / 60);
		value = value - (mins*60);
		etMins.setText(String.format("%02d",mins));
		
		//secs
		etSecs.setText(String.format("%04.1f",value));
	}
	final static int MSG_START_TIMER = 0;
    final static int MSG_STOP_TIMER = 1;
    final static int MSG_UPDATE_TIMER = 2;
	static StopWatch timer = new StopWatch();
    final static int REFRESH_RATE = 25;
    static double startTime;
    static EditText global_etSecs; 
    static EditText global_etMins;
    static EditText global_etHours;
    static FormatHelper f;
	public static void timerControl(Context ctx,EditText etSecs, EditText etMins,
			EditText etHours, TextView tvTimerControl) {
		f=new FormatHelper(ctx);
		String control = tvTimerControl.getText().toString();
		if (control ==  ctx.getString(R.string.start_timer)){
			tvTimerControl.setText(f.underline(ctx.getString(R.string.stop_timer)));
			tvTimerControl.setTextColor(Color.RED);
			global_etSecs = etSecs;
			global_etMins = etMins;
			global_etHours = etHours;
        	startTime = getTimerValue(etSecs,etMins,etHours);
   			mHandler.sendEmptyMessage(MSG_START_TIMER);
   		}else{
   			tvTimerControl.setText(f.underline(ctx.getString(R.string.start_timer)));
   			tvTimerControl.setTextColor(Color.WHITE);
			mHandler.sendEmptyMessage(MSG_STOP_TIMER);
		}
	}
	static Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_START_TIMER:
                timer.start(); //start timer
                mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                break;
            case MSG_UPDATE_TIMER:
                setTimerValue(global_etSecs,global_etMins,global_etHours, startTime + timer.getElapsedTime());
       			mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER,REFRESH_RATE); //text view is updated every second, 
                break;                                  //though the timer is still running
            case MSG_STOP_TIMER:
                mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                timer.stop();//stop timer
                break;
            default:
                break;
            }
        }
    };
	public static String getValueString(Context ctx, int trackerType, float value){
		String returnVal="";
		switch (trackerType){
			case 3:			//yes/no choice
				if (value==0){
					returnVal = ctx.getString(R.string.no);
				}else{
					returnVal = ctx.getString(R.string.yes);
				}
				break;
			case 5:			//stopwatch
				Duration d = new Duration((int)value);
				returnVal = String.format(
						"%02d:%02d:%02d.%01d", 
						d.getStandardHours(), 
						d.getStandardMinutes()	%60, 
						d.getStandardSeconds()	%60,
						d.getMillis()          	%1000 /100);
				break;
			default:
				returnVal = String.valueOf(value);
		}
		return returnVal;
	}
}