
public class Clock {
	public static int currentTime;

	public Clock() {
		currentTime = 0;
	}

	public static void incrementClock() {
		currentTime++;
	}

	static int getCurrentMs() {
		return currentTime;
	}

}
