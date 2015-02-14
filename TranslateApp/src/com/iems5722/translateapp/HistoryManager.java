package com.iems5722.translateapp;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;


public class HistoryManager {
	private final String TAG = "HistoryManager";
	
	private Context mContext;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private final String histKey = "HISTORY";

	HistoryManager(Context mContext) {
		this.mContext = mContext;
		sharedPref = mContext.getSharedPreferences(MainActivity.class.getSimpleName(), mContext.MODE_PRIVATE);
	}
	
	public JSONObject getHistory() {
		String historyString = sharedPref.getString(histKey, "");
		Log.i(TAG, "History found " + historyString);
		if (historyString.equals("")) {
			return new JSONObject();
		}
		JSONObject historyObj = new JSONObject();
		try {
			historyObj = new JSONObject(historyString);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to open history");
			e.printStackTrace();
		}
		return historyObj;
	}
	
	public boolean appendHistory(String value) {
		Date date = new Date();
		String key = Long.toString(date.getTime());
		JSONObject historyObj = getHistory();
		try {
			historyObj.put(key, value);
			return saveHistory(historyObj);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to add history");
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean removeHistory(String key) {
		JSONObject historyObj = getHistory();
		historyObj.remove(key);
		return saveHistory(historyObj);
	}
	
	private boolean saveHistory(JSONObject historyObj) {
		String historyStr = historyObj.toString();
		editor = sharedPref.edit();
		editor.putString(histKey, historyStr);
		editor.commit();
		return true;
	}
}
