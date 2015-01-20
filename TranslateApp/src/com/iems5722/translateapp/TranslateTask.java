package com.iems5722.translateapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.widget.TextView;

public class TranslateTask extends AsyncTask<String, Void, String> {
	String destAddress;
	int destPort;
	String response = "";
	TextView textView;
	
	public TranslateTask(String destAddress, int destPort, TextView textView) {
		this.destAddress = destAddress;
		this.destPort = destPort;
		this.textView = textView;
	}
		
	@Override
	protected String doInBackground(String... input) {
		// IO on non UI thread
		Socket socket = null;
		String userInput = input[0];
		try {
			socket = new Socket(destAddress, destPort);
			
			// send data to server
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.print(userInput);

			// read response from server
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			response = reader.readLine();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
			response += "Unknown Host " + e.toString();
		} catch (IOException e) {
			e.printStackTrace();
			response += "IOException " + e.toString();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					response += "IOException " + e.toString();
				}
			}
		}
		return response;
	}
	
	@Override
	public void onPostExecute(String response) {
		// Update view on UI thread
		textView.setText("");
		textView.setText(response);
	}
}
