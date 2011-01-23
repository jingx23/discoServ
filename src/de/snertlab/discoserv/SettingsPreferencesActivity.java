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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;


public class SettingsPreferencesActivity extends PreferenceActivity {
	
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
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				 if (KEY_USERNAME.equals(key)) {
					 mUsernamePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_USERNAME, ""));
				 }else if(KEY_PASSWORD.equals(key)){
					 String stars = makeStarsForPassword(getPreferenceScreen().getSharedPreferences().getString(KEY_PASSWORD, ""));
					 mPasswordPreference.setSummary(stars);
				 }
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mUsernamePreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_USERNAME, ""));
		String stars = makeStarsForPassword(getPreferenceScreen().getSharedPreferences().getString(KEY_PASSWORD, ""));
		mPasswordPreference.setSummary(stars);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListenerAddSummeryText);
	}
	
	@Override
	protected void onPause() {
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListenerAddSummeryText);
		super.onPause();
	}
	
	private String makeStarsForPassword(String password){
		String star = "*";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < password.length(); i++) {
			sb.append(star);
		}
		return sb.toString();
	}
}

