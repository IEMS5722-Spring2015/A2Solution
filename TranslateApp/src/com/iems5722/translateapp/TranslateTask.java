package com.iems5722.translateapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class TranslateTask extends AsyncTask<String, Void, Void> {
	private final String TAG = "TranslateTask";
	
	String tcpAddress, httpAddress, connection;
	int tcpPort;
	String response = "";
	TextView textView;
	
	HistoryManager histManager;
	String userInput;
	
	public TranslateTask(HistoryManager histManager, String tcpAddress, int tcpPort, String httpAddress, TextView textView, String connection) {
		this.tcpAddress = tcpAddress;
		this.tcpPort = tcpPort;
		this.httpAddress = httpAddress;
		this.textView = textView;
		this.connection = connection;
		this.histManager = histManager;
		Log.d(TAG, "Init new task");
	}
		
	@Override
	protected Void doInBackground(String... input) {
		Log.d(TAG, "Doing translate task");
		String userInput = input[0];
		
		if (this.connection.equals("tcp")) {
			// use tcp translation server
			translateTCP(userInput);
		}
		else if (this.connection.equals("http")) {
			// use http translation server
			translateHTTP(userInput);
		}
		else {
			Log.e(TAG, "Unknown service requested");
		}
		Log.d(TAG, "Returning");
		return null;
	}
		
	private void translateTCP(String userInput) {	
		Log.d(TAG, "TCP translate");
		// IO on non UI thread
		Socket socket = null;
		response = "";
		try {
			this.userInput = userInput;
			
			Log.d(TAG, "Trying to get socket");
			InetAddress destination = InetAddress.getByName(tcpAddress);
			Log.d(TAG, "Connecting to " + destination.getHostAddress());
			socket = new Socket(destination, tcpPort);			
			
			// send data to server
			Log.d(TAG, "Sending " + userInput + " to server");
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(userInput);

			// read response from server
			Log.d(TAG, "Getting response");
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			response += reader.readLine();
			Log.d(TAG, "Received " + response);
			reader.close();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
			response += "Unknown Host " + e.toString();
		} catch (IOException e) {
			e.printStackTrace();
			response += "IOException " + e.toString();
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
	}
	
	private void translateHTTP(String userInput) {
		Log.d(TAG, "HTTP translate");
		String getRequest = this.httpAddress + "?word=" + userInput;
		
		URLConnection conn = null;
		response = "";
		try {
			URL url = new URL(getRequest);
			conn = url.openConnection();
			
			InputStream in = new BufferedInputStream(conn.getInputStream());
			byte[] buffer = new byte[in.available()];
			in.read(buffer, 0, in.available());
			response += new String(buffer);
			
			in.close();
			
		} catch (MalformedURLException e) {
			response += "Malformed URL " + getRequest;
			e.printStackTrace();
		} catch (IOException e) {
			response += "IOException " + e.toString();
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPostExecute(Void v) {
		// Update view on UI thread
		Log.d(TAG, "Updating UI");
		textView.setText("");
		textView.setText(response);
		String histRecord = userInput + ":" +response;
		histManager.appendHistory(histRecord);
	}
}
