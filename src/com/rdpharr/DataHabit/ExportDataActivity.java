package com.rdpharr.DataHabit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import controllers.ExportLogic;

public class ExportDataActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    final CharSequence[] items = {"Separate Date and Time Fields", "Include Last Update Time"};
	    final boolean[] options = {false, true};
	    
	    //setup alert
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Export Options");
	    builder.setMultiChoiceItems(items, options, new DialogInterface.OnMultiChoiceClickListener() {
		   public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			   options[which]=isChecked;
			}
	    });
	    builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
      	  public void onClick(DialogInterface dialog, int whichButton) {
      		  exportData(options);
      	    }
      	  });
	    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
      	    public void onClick(DialogInterface dialog, int whichButton) {
      	      // Canceled.
      	    }
      	  });
	    
	    //show alert
	    AlertDialog alert = builder.create();
	}
	private void exportData(boolean[] options){
	    String outFile = ExportLogic.exportData(options, this);
      //send email
  		Intent email = new Intent(android.content.Intent.ACTION_SEND);
  		email.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.eMailSubject)); 
  		email.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.eMailText)); 
  		email.setType("application/octet-stream");
  		email.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(outFile));
  		this.startActivity(email);
  		finish();		
	}
}