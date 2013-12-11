package com.phildatoon.stopwatch;

/**
 * Source code reference:
 * https://github.com/bostwick/android-stopwatch
 * 
 * Retrieved 25 Nov 2013
 */

import java.util.ArrayList;
import java.util.List;

public class Stopwatch {
	
	// implements a method that returns the current time, in milliseconds.
	public interface GetTime {
		public long now();
	}
	
	// default way to get time. Just use the system clock.
	private GetTime SystemTime = new GetTime() {
		@Override
		public long now() {
			return System.currentTimeMillis();
		}
	};
	
	// sets state of the stopwatch
	public enum State { PAUSED, RUNNING };
	
	private GetTime m_time;
	private long m_startTime;
	private long m_stopTime;
	private long m_pauseOffset;
	private List<Long> m_laps = new ArrayList<Long>();
	private State m_state;
	
	public Stopwatch() {
		m_time = SystemTime;
		reset();
	}
	public Stopwatch(GetTime time) {
		m_time = time;
		reset();
	}
	
	// starts the stopwatch running. If the stopwatch is already running, this does nothing.
	public void start() {
		if ( m_state == State.PAUSED ) {
			m_pauseOffset = getElapsedTime();
			m_stopTime = 0;
			m_startTime = m_time.now();
			m_state = State.RUNNING;
		}
	}

	// pauses the stopwatch. If the stopwatch is already running, do nothing.
	public void pause() {
		if ( m_state == State.RUNNING ) {
			m_stopTime = m_time.now();
			m_state = State.PAUSED;
		}
	}

	// resets the stopwatch to the initial state, clearing all stored times. 
	public void reset() {
		m_state = State.PAUSED;
		m_startTime 	= 0;
		m_stopTime 		= 0;
		m_pauseOffset 	= 0;
		m_laps.clear();
	}
	
	// records a lap at the current time.
	public void lap() {
		m_laps.add(getElapsedTime());
	}
	
	// returns the amount of time recorded by the stopwatch, in milliseconds
	public long getElapsedTime() {
		if ( m_state == State.PAUSED ) {
			return (m_stopTime - m_startTime) + m_pauseOffset;
		} else {
			return (m_time.now() - m_startTime) + m_pauseOffset;
		}
	}
	
	// returns A list of the laps recorded. Each lap is given as a millisecond value from when the stopwatch began running. 
	public List<Long> getLaps() {
		return m_laps;
	}
	
	// returns true if the stopwatch is currently running and recording time, false otherwise.
	public boolean isRunning() {
		return (m_state == State.RUNNING);
	}
}
