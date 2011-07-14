/* Copyright (c) 2008-2011 -- CommonsWare, LLC

	 Licensed under the Apache License, Version 2.0 (the "License");
	 you may not use this file except in compliance with the License.
	 You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.
*/
	 
package de.snertlab.discoserv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.telephony.TelephonyManager;


public class SettingsPreferencesActivity extends PreferenceActivity {
	
	private static final String PASSWD_STARS = "****";
	public static String KEY_USERNAME = "username";
	public static String KEY_PASSWORD = "password";
	
	private OnSharedPreferenceChangeListener onSharedPreferenceChangeListenerAddSummeryText;
	private EditTextPreference mUsernamePreference;
	private EditTextPreference mPasswordPreference;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		addPreferencesFromResource(R.xml.preferences);
		initOnSharedPreferenceChangeListener();
		mUsernamePreference = (EditTextPreference)getPreferenceScreen().findPreference(KEY_USERNAME);
		mPasswordPreference = (EditTextPreference)getPreferenceScreen().findPreference(KEY_PASSWORD);
	}
	
	private void initOnSharedPreferenceChangeListener(){
		onSharedPreferenceChangeListenerAddSummeryText = new OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				 if (KEY_USERNAME.equals(key)) {
					 mUsernamePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_USERNAME, ""));
				 }else if(KEY_PASSWORD.equals(key)){
					 String passwort = getPreferenceScreen().getSharedPreferences().getString(KEY_PASSWORD, "");
					 if(! "".equals(passwort)){
						 passwort = PASSWD_STARS;	
					 }
					 mPasswordPreference.setSummary(passwort);
				 }
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String username = getPreferenceScreen().getSharedPreferences().getString(KEY_USERNAME, "");
		String passwort = getPreferenceScreen().getSharedPreferences().getString(KEY_PASSWORD, ""); 
		if("".equals(username)){
			username = getTelephonNr();
			mUsernamePreference.setText(username);
		}
		mUsernamePreference.setSummary(username);
		if(! "".equals(passwort)){
			passwort = PASSWD_STARS;
		}
		mPasswordPreference.setSummary(passwort);	
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListenerAddSummeryText);
	}
	
	@Override
	protected void onPause() {
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListenerAddSummeryText);
		super.onPause();
	}
	
	private String getTelephonNr(){
		TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String userId = telephonyMgr.getLine1Number();
		if(userId == null || userId.trim().equals("")){
			userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		if(userId==null) return "";
		return userId;
	}
}

