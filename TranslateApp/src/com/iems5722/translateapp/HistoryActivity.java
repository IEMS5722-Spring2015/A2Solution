package com.iems5722.translateapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HistoryActivity extends Activity {
	private final String TAG = "HistoryActivity";

	ListView histList;
	HistoryManager historyManager;
	ArrayAdapter<String> histAdapter;
	List<String> histKeys = new ArrayList<String>();
	List<String> dataList =  new ArrayList<String>();	

	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		
		setContentView(R.layout.activity_history);
		// get references to layout objects
		histList = (ListView) this.findViewById(R.id.histList);
		
		// Load history
		historyManager = new HistoryManager(this);
		histAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		histList.setAdapter(histAdapter);

		histList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView parent, View view, final int position, final long id) {
				// ask the user if they want to delete record
				Builder builder = new Builder(context);
				builder.setMessage(R.string.delete_query);
				builder.setCancelable(true);
				builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, String.valueOf(position));
						Log.d(TAG, String.valueOf(id));
						dataList.get(position);
						String key = histKeys.get(position);
						historyManager.removeHistory(key);
						dialog.dismiss();
						readHistory();
						histAdapter.notifyDataSetChanged();
					}
				});				
				builder.show();		
				return false;
			}
		});
		readHistory();
	}
	
	private void readHistory() {
		JSONObject histObj = historyManager.getHistory();
		histKeys.clear();
		dataList.clear();
		Iterator<String> keys = histObj.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			try {
				histKeys.add(key);
				dataList.add(histObj.getString(key));
			} catch (JSONException e) {
				Log.e(TAG, "Failed to add to history");
				e.printStackTrace();
			}
		}
		Log.d(TAG, "History updated");
		histAdapter.notifyDataSetChanged();
	}
	
	
}
