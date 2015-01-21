package com.iems5722.translateapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class TranslateTask extends AsyncTask<String, Void, Void> {
	private final String TAG = "TranslateTask";
	
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
	protected Void doInBackground(String... input) {
		// IO on non UI thread
		Socket socket = null;
		String userInput = input[0];
		response = "";
		try {
			Log.d(TAG, "Trying to get socket");
			InetAddress destination = InetAddress.getByName(destAddress);
			Log.d(TAG, "Connecting to " + destination.getHostAddress());
			socket = new Socket(destination, destPort);			
			
			// send data to server
			Thread.sleep(10000);
			Log.d(TAG, "Sending " + userInput + " to server");
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(userInput);

			// read response from server
			Log.d(TAG, "Getting response");
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			response += reader.readLine();
			Log.d(TAG, "Received " + response);
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
			response += "Unknown Host " + e.toString();
		} catch (IOException e) {
			e.printStackTrace();
			response += "IOException " + e.toString();
		} catch (InterruptedException e) {
			response += "InterruptedException " + e.toString();
			e.printStackTrace();
		} finally {
			Log.d(TAG, "Finally");
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					response += "IOException " + e.toString();
				}
			}
		}
		Log.d(TAG, "Returning");
		return null;
	}
	
	@Override
	protected void onPostExecute(Void v) {
		// Update view on UI thread
		Log.d(TAG, "Updating UI");
		textView.setText("");
		textView.setText(response);
	}
}
