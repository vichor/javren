package engineTester.WorldTimeManager;

public class Clock {
	
	public int hour, minute, second;
	
	public Clock(int hour, int minutes, int seconds) {
		this.hour = hour;
		this.minute = minutes;
		this.second = seconds;
	}
	
	public Clock(int hour, int minutes) {
		this.hour = hour;
		this.minute = minutes;
		this.second = 0;
	}
	
	public Clock(String time) throws NumberFormatException {
		setTime(time);
	}
	
	public void setTime(String time) throws NumberFormatException {
		this.hour = Integer.parseInt(time.substring(0, 2));
		this.minute = Integer.parseInt(time.substring(3, 5));
		try {
			this.second = Integer.parseInt(time.substring(6));
		} catch (Exception e) {
			this.second = 0;
		}
	}
	
	public void incrementHours() {
		incrementHours(1);
	}
	
	public void incrementHours(int inc) {
		hour += inc;
		if (hour > 23) {
			hour = 0;
		}
	}
	
	public void incrementMinutes() {
		incrementMinutes(1);
	}

	public void incrementMinutes(int inc) {
		minute += inc;
		int hoursinc = minute/60;
		minute %= 60;
		if (hoursinc > 0) {
			incrementHours(hoursinc);
		}
	}
	
	public void incrementSeconds() {
		incrementSeconds(1);
	}

	public void incrementSeconds(int inc) {
		second += inc;
		int minutesinc = second/60;
		second %= 60;
		if (minutesinc > 0) {
			incrementMinutes(minutesinc);
		}
	}

	
	public String toString() {
		String hourString = Integer.toString(hour);
		if (hourString.length() < 2) {
			hourString = "0" + hourString;
		}
		String minutesString = Integer.toString(minute);
		if (minutesString.length() < 2) {
			minutesString = "0" + minutesString;
		}
		String secondsString = Integer.toString(second);
		if (secondsString.length() < 2) {
			secondsString = "0" + secondsString;
		}
		return hourString+":"+minutesString+":"+secondsString;
	}
	
	
	public float toHours() {
		return hour + minute/60.0f + second/3600.0f;
	}
	
	public float toMinutes() {
		return hour*60 + minute + second/60.0f;
	}
	
	public int toSeconds() {
		return hour*3600 + minute*60 + second;
	}

}
