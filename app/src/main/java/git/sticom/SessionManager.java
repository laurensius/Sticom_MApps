package git.sticom;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "AndroidHivePref";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	
	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";
	public static final String kunci_id = "keyid";
	public static final String kunci_idKus = "keyidKus";
	public static final String kunci_nama = "keynama";

	
	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String name, String id, String nama){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		
		// Storing name in pref
		editor.putString(KEY_NAME, name);
		editor.putString(kunci_id, id);
		editor.putString(kunci_nama, nama);
		
		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkSession(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Katalog Activity
			Intent i = new Intent(_context, SplashActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));
		user.put(kunci_id, pref.getString(kunci_id, null));
		user.put(kunci_nama, pref.getString(kunci_nama, null));
		
		// return user
		return user;
	}
	
	/**
	 * Clear session details
	 * */
	public void destroySession(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, SplashActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}

}
