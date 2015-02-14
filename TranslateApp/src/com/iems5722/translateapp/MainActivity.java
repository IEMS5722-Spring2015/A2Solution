package com.iems5722.translateapp;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	
	EditText inputText;
	Button tcpButton, httpButton, histButton;
	TextView outputText;
	Map<String, String> dictionary;
	Context mContext;
	HistoryManager histManager;
	
	// Translate TCP server
	String tcpAddress = "iems5722v.ie.cuhk.edu.hk";
	int tcpPort = 3001;
	
	// Translate HTTP server
	String httpAddress = "http://iems5722v.ie.cuhk.edu.hk/translate.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		histManager = new HistoryManager(mContext);
		
		setContentView(R.layout.activity_main);
		// get references to layout objects
		inputText = (EditText) this.findViewById(R.id.textInput);
		outputText = (TextView) this.findViewById(R.id.textOutput);
		
		// add click listener to button
		tcpButton = (Button) this.findViewById(R.id.tcp_submit);
		tcpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "TCP clicked");
				// call the translate function
				translateText("tcp");
			}
		});
		// add click listener to button
		httpButton = (Button) this.findViewById(R.id.http_submit);
		httpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "HTTP clicked");
				// call the translate function
				translateText("http");
			}
		});
		
		// add click listener to button
		histButton = (Button) this.findViewById(R.id.view_history);
		histButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "History clicked");
				// go to history activity
				Intent histIntent = new Intent(mContext, HistoryActivity.class);
				startActivity(histIntent);
			}
		});		
	}
	
	// translate look up
	private void translateText(String connection) {
		String input = inputText.getText().toString();
		// check if input has been entered
		if (input.isEmpty()) {
			// toast a message to the user to ask for input
			Toast.makeText(this, R.string.toast_msg, Toast.LENGTH_SHORT).show();
			return;
		}
		// try get word out of dictionary
		TranslateTask translate = new TranslateTask(histManager, tcpAddress, tcpPort, httpAddress, outputText, connection);
		translate.execute(input);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
