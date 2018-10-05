package engineTester.WorldTimeManager;

public class WorldClock {
	
	private static final WorldClock instance = new WorldClock();
	
	private Clock clock;
	
	public static WorldClock get() {
		return instance;
	}
	
	private WorldClock() {
		clock = new Clock("00:00:00");
	}
	
	public void step() {
		step(1);
	}

	public void step(int inc) {
		clock.incrementSeconds(inc);
	}
	
	public DayPart getDayPart() {
		return DayPart.valueOf(clock);
	}
	
	public Clock getClock() {
		return clock;
	}
	
	public float getDayPartProgress() {
		return DayPart.progress(clock);
	}
	
	public float getDayProgress() {
		return (float)clock.toSeconds()/(24.0f*3600.0f);
	}

	public String toString() {
		String clockString = clock.toString();
		String daypartString = getDayPart().toString();
		//return clock.toString() + " (" + getDayPart().toString() + ")";
		return clockString + " (" + daypartString + ")";
	}

}
