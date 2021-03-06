package engineTester.WorldTimeManager;


public enum DayPart {
	/* day parts start/end at full hours. If minutes are used, refactor the valueOf method below */
	DAWN 		(new Clock("05:00:00"), new Clock("07:59:59")), 
	MORNING		(new Clock("08:00:00"), new Clock("09:59:59")), 
	NOON		(new Clock("10:00:00"), new Clock("13:59:59")), 
	AFTERNOON	(new Clock("14:00:00"), new Clock("16:59:59")),
	TWILIGHT	(new Clock("17:00:00"), new Clock("21:59:59")), 
	NIGHT		(new Clock("22:00:00"), new Clock("04:59:59"));
	
	public final Clock start;
	public final Clock end;

	private DayPart(Clock start, Clock end){
		this.start = start;
		this.end = end;
	}
	
	public static DayPart valueOf(Clock time){
		DayPart result=null;
		for (DayPart daypart : DayPart.values()) {
			if ((time.hour >= daypart.start.hour) && (time.hour <= daypart.end.hour)) {
				result = daypart;
				break;
			}else if (daypart.start.hour > daypart.end.hour) { // turn around the clock
				if ((time.hour >= daypart.start.hour) || (time.hour <= daypart.end.hour)) {
					result = daypart;
					break;
				}
			}
		}
		return result;
	}
	
	public static float progress(Clock time) {
		DayPart daypart = valueOf(time);
		float startHours = daypart.start.toHours();
		float endHours = daypart.end.toHours();
		float timeHours = time.toHours();
		
		float totalHours, consumedHours;
		if (startHours < endHours) {
			totalHours = endHours - startHours;
		}else {
			totalHours = 24 - startHours + endHours;
		}

		if (endHours > timeHours) {
			consumedHours = totalHours - (endHours - timeHours);
		}else {
			consumedHours = totalHours - (endHours + (24 - timeHours));
		}

		return consumedHours/totalHours;
	}
}
