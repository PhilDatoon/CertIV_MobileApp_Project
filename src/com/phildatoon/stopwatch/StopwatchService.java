package com.phildatoon.stopwatch;

/**
 * Source code reference:
 * https://github.com/bostwick/android-stopwatch
 * 
 * Retrieved 25 Nov 2013
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class StopwatchService extends Service {	
	public class LocalBinder extends Binder {
		StopwatchService getService() {
			return StopwatchService.this;
		}
	}
	
	private Stopwatch m_stopwatch;
	private LocalBinder m_binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return m_binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_stopwatch = new Stopwatch();
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
   
    public void start() {
		m_stopwatch.start();
	}
	
	public void pause() {
		m_stopwatch.pause();
	}
	
	public void lap() {
		m_stopwatch.lap();
	}
	
	public void reset() {
		m_stopwatch.reset();
	}
	
	public long getElapsedTime() {
		return m_stopwatch.getElapsedTime();
	}
	
	public String getFormattedElapsedTime() {
		return formatElapsedTime(getElapsedTime());
	}
	
	public boolean isStopwatchRunning() {
		return m_stopwatch.isRunning();
	}
	
	/***
	 * Given the time elapsed in milliseconds of seconds, returns the string
	 * representation of that time. 
	 * 
	 * @param now, the current time in milliseconds of seconds
	 * @return 	String with the current time in the format MM:SS:MS or 
	 * 			HH:MM:SS:MS, depending on elapsed time.
	 */
	private String formatElapsedTime(long now) {
		long hours=0, minutes=0, seconds=0, ms=0;
		StringBuilder sb = new StringBuilder();
		
		if (now < 1000) {
			ms = now / 10;
		} else if (now < 60000) {
			seconds = now / 1000;
			now -= seconds * 1000;
			ms = now/ 10;
		} else if (now < 3600000) {
			hours = now / 3600000;
			now -= hours * 3600000;
			minutes = now / 60000;
			now -= minutes * 60000;
			seconds = now / 1000;
			now -= seconds * 1000;
			ms = now / 10;
		}
		
		if (hours > 0) {
			sb.append(hours).append(":")
			.append(formatDigits(minutes)).append(":")
			.append(formatDigits(seconds)).append(".")
			.append(formatDigits(ms));
		} else {
			sb.append(formatDigits(minutes)).append(":")
			.append(formatDigits(seconds)).append(":")
			.append(formatDigits(ms));
		}
		
		return sb.toString();
	}
	
	@SuppressLint("UseValueOf")
	private String formatDigits(long num) {
		return (num < 10) ? "0" + num : new Long(num).toString();
	}
}
