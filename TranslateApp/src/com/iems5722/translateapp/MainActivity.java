package com.iems5722.translateapp;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	EditText inputText;
	Button submitButton;
	TextView outputText;
	Map<String, String> dictionary;
	
	// Translate server
	String destAddress = "iems5722v.ie.cuhk.edu.hk";
	int destPort = 3001;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// get references to layout objects
		inputText = (EditText) this.findViewById(R.id.textInput);
		submitButton = (Button) this.findViewById(R.id.submit);
		outputText = (TextView) this.findViewById(R.id.textOutput);
		// add click listener to button
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// call the translate function
				translateText();
			}
		});
		// load dictionary
		WordDictionary wordDict = new WordDictionary();
		dictionary = wordDict.getDictionary();
	}
	
	// translate look up
	private void translateText() {
		String input = inputText.getText().toString();
		// check if input has been entered
		if (input.isEmpty()) {
			// toast a message to the user to ask for input
			Toast.makeText(this, R.string.toast_msg, Toast.LENGTH_SHORT).show();
			return;
		}
		// try get word out of dictionary
		TranslateTask translate = new TranslateTask(destAddress, destPort, outputText);
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
