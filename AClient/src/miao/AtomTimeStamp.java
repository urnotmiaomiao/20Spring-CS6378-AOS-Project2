package miao;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomTimeStamp {
	private AtomicInteger atomTimestamp = new AtomicInteger(0);

	public int getTimestamp() {
		return this.atomTimestamp.get();
	}
	
	public void setTimestamp(int ts) {
		this.atomTimestamp.set(ts);
	}
	
	public int increaseTimestamp() {
		return this.atomTimestamp.getAndIncrement();
	}
	
}
