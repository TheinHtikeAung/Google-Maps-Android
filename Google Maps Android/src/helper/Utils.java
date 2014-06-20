package helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();

	public static boolean isNullOrEmptyString(final String aString) {
		return ((aString == null) || (aString.length() == 0));
	}

	public static void checkConn(final Context c) {
		
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			Log.i(TAG, "Have Internet Connection");
		}else{
			Log.i(TAG, "No Internet Connection");
			
			((Activity)c).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// Show error messages
					new AlertDialog.Builder(c)
					.setTitle("No network connectoin")
					.setMessage("Can't connect to the network. \nCheck your data connection and try again.")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
				}
			});

		}
	}
	
	public static boolean tryParseInt(String value)  
	{  
	     try  
	     {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch(NumberFormatException nfe)  
	      {  
	          return false;  
	      }  
	}
	
	public static ProgressDialog getProgressDialog(Context c){
		ProgressDialog progressDialog = new ProgressDialog(c);
		progressDialog.setMessage("Calculating ...");
		progressDialog.setCancelable(false);
		
		return progressDialog;
	}
}
