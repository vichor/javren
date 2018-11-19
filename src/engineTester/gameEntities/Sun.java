package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engineTester.WorldTimeManager.DayPart;
import engineTester.WorldTimeManager.WorldClock;
import entities.Light;

public class Sun extends Light {
	
	private static final int ORBIT_RADIUS = 100000;
	private static final int ORBIT_HEIGHT = 100000;

	private static final int RED_COMPONENT = 0;
	private static final int BLUE_COMPONENT = 1;
	private static final int GREEN_COMPONENT = 2;
	
    /* Color components for day parts:                         RED                        BLUE                       GREEN        */
	private static final Vector2f NIGHT_COLOR[] 	= { new Vector2f(0.0f,  0.0f),  new Vector2f(0.0f, 0.0f),  new Vector2f(0.0f, 0.0f) };
	private static final Vector2f DAWN_COLOR[] 		= { new Vector2f(0.0f,  1.10f), new Vector2f(0.0f, 0.75f), new Vector2f(0.0f, 0.15f) };
	private static final Vector2f MORNING_COLOR[] 	= { new Vector2f(1.10f, 2.00f), new Vector2f(0.75f, 2.00f), new Vector2f(0.15f, 2.00f) };
	private static final Vector2f NOON_COLOR[] 		= { new Vector2f(2.00f, 2.00f), new Vector2f(2.00f, 2.00f), new Vector2f(2.00f, 2.00f) };
	private static final Vector2f AFTERNOON_COLOR[] = { new Vector2f(2.00f, 2.00f), new Vector2f(2.00f, 1.00f), new Vector2f(2.00f, 0.00f) };
	private static final Vector2f TWILIGHT_COLOR[] 	= { new Vector2f(2.00f, 0.0f),  new Vector2f(1.00f, 0.0f), new Vector2f(0.00f, 0.0f) };

	
	public Sun () {
		this.position = new Vector3f(ORBIT_RADIUS, ORBIT_HEIGHT, -10000);
		this.color = new Vector3f(1f, 1f, 1f);
	}
	
	
	public void update() {
		updateColor();
		updatePosition();
	}
	
	private void updatePosition() {
		WorldClock clock = WorldClock.get();
		float progress = clock.getDayProgress();
		//System.out.print("\tprogress = " + progress);
		position.x = ORBIT_RADIUS * (float)Math.cos(Math.toRadians((progress*360)-90));
		position.y = ORBIT_HEIGHT * (float)Math.sin(Math.toRadians((progress*360)-90));
		//System.out.println ("\t"+position);
	}

	private void updateColor() {
		WorldClock clock = WorldClock.get();
		DayPart dayPart = clock.getDayPart();
		Vector2f targetColorRed = NOON_COLOR[RED_COMPONENT];
		Vector2f targetColorBlue = NOON_COLOR[BLUE_COMPONENT];
		Vector2f targetColorGreen = NOON_COLOR[GREEN_COMPONENT];
		switch (dayPart) {
		case NIGHT: 
			targetColorRed = NIGHT_COLOR[RED_COMPONENT]; 
			targetColorBlue = NIGHT_COLOR[BLUE_COMPONENT]; 
			targetColorGreen = NIGHT_COLOR[GREEN_COMPONENT]; 
			break;
		case DAWN: 
			targetColorRed = DAWN_COLOR[RED_COMPONENT]; 
			targetColorBlue = DAWN_COLOR[BLUE_COMPONENT]; 
			targetColorGreen = DAWN_COLOR[GREEN_COMPONENT]; 
			break;
		case MORNING: 
			targetColorRed = MORNING_COLOR[RED_COMPONENT]; 
			targetColorBlue = MORNING_COLOR[BLUE_COMPONENT]; 
			targetColorGreen = MORNING_COLOR[GREEN_COMPONENT]; 
			break;
		case AFTERNOON: 
			targetColorRed = AFTERNOON_COLOR[RED_COMPONENT]; 
			targetColorBlue = AFTERNOON_COLOR[BLUE_COMPONENT]; 
			targetColorGreen = AFTERNOON_COLOR[GREEN_COMPONENT]; 
			break;
		case TWILIGHT: 
			targetColorRed = TWILIGHT_COLOR[RED_COMPONENT]; 
			targetColorBlue = TWILIGHT_COLOR[BLUE_COMPONENT]; 
			targetColorGreen = TWILIGHT_COLOR[GREEN_COMPONENT]; 
			break;
		case NOON: 
			break;
		default:
			System.err.println("Wrong day part: " + dayPart);
		}
		color.x = targetColorRed.x + (clock.getClock().toSeconds() - dayPart.start.toSeconds())*(targetColorRed.y-targetColorRed.x)/(dayPart.end.toSeconds()-dayPart.start.toSeconds());
		color.y = targetColorBlue.x + (clock.getClock().toSeconds() - dayPart.start.toSeconds())*(targetColorBlue.y-targetColorBlue.x)/(dayPart.end.toSeconds()-dayPart.start.toSeconds());
		color.z = targetColorGreen.x + (clock.getClock().toSeconds() - dayPart.start.toSeconds())*(targetColorGreen.y-targetColorGreen.x)/(dayPart.end.toSeconds()-dayPart.start.toSeconds());
		//System.out.print("\t"+color);
	}
	

}
