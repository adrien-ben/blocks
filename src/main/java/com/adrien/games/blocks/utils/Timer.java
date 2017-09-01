package com.adrien.games.blocks.utils;

public class Timer {

	private long time;

	public Timer() {
		this.time = System.nanoTime();
	}

	public long top() {
		final long time = System.nanoTime();
		final long elapsed = time - this.time;
		this.time = time;
		return elapsed;
	}

}
