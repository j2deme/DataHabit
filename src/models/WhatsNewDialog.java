package models;

import com.rdpharr.DataHabit.R;
import com.rdpharr.DataHabit.TrackerActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class WhatsNewDialog {
	int savedVersion;
	Context c;
	public WhatsNewDialog(Context ctx){
		c=ctx;
		SharedPreferences settings = c.getSharedPreferences("settings", 0);
		savedVersion = settings.getInt("version", 1);
		//check version
	}
	public void showDialog(){
		if (savedVersion != getVersion()){
			AlertDialog.Builder alert = new AlertDialog.Builder(c);
			alert.setTitle(c.getResources().getString(R.string.WhatsNew))
				.setMessage(c.getResources().getString(R.string.WhatsNewDialog))
				.setNegativeButton(c.getResources().getString(R.string.OK), 
						new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
			           }
			       });
			alert.show();
			SharedPreferences settings = c.getSharedPreferences("settings", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("version", getVersion());
			editor.commit();
		}
		
	}
	public int getVersion() {
	    int v = 0;
	    try {
	        v = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
	    } catch (NameNotFoundException e) {
	        // Huh? Really?
	    }
	    return v;
	}

}
