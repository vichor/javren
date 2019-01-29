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
	
	/* Day is divided into parts. Each day part has a start and an end color. The Sun will change its color linearly from start to end
	 * Colors are expressed with the 3 components in a 2D vector. The first dimension of the vector contains the start value for the
	 * color component; the second dimension of the vector contains the end value for the color component
	 */
    /* Color components for day parts:                         RED                        BLUE                       GREEN               */
	private static final Vector2f COLORS[][] 	= {   /*           start, end                  start, end                 start, end     */
				/* Night     */                     { new Vector2f(0.0f,  0.0f),  new Vector2f(0.0f, 0.0f),   new Vector2f(0.0f, 0.0f)   },
				/* Dawn      */                     { new Vector2f(0.0f,  0.80f), new Vector2f(0.0f, 0.65f),  new Vector2f(0.0f, 0.15f)  },
				/* Morning   */                     { new Vector2f(0.80f, 1.20f), new Vector2f(0.65f, 1.20f), new Vector2f(0.15f, 1.20f) },
				/* Noon      */                     { new Vector2f(1.20f, 1.20f), new Vector2f(1.20f, 1.20f), new Vector2f(1.20f, 1.20f) },
				/* Afternoon */                     { new Vector2f(1.20f, 1.00f), new Vector2f(1.20f, 0.20f), new Vector2f(1.20f, 0.00f) },
				/* Twilight  */                     { new Vector2f(1.00f, 0.0f),  new Vector2f(0.20f, 0.0f),  new Vector2f(0.00f, 0.0f)  } 
											  	  };
	
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
		Vector2f targetColorRed = COLORS[dayPart.index][RED_COMPONENT]; 
		Vector2f targetColorBlue = COLORS[dayPart.index][BLUE_COMPONENT]; 
		Vector2f targetColorGreen = COLORS[dayPart.index][GREEN_COMPONENT]; 
		color.x = targetColorRed.x + (clock.getClock().toSeconds() - dayPart.start.toSeconds())*(targetColorRed.y-targetColorRed.x)/(dayPart.end.toSeconds()-dayPart.start.toSeconds());
		color.y = targetColorBlue.x + (clock.getClock().toSeconds() - dayPart.start.toSeconds())*(targetColorBlue.y-targetColorBlue.x)/(dayPart.end.toSeconds()-dayPart.start.toSeconds());
		color.z = targetColorGreen.x + (clock.getClock().toSeconds() - dayPart.start.toSeconds())*(targetColorGreen.y-targetColorGreen.x)/(dayPart.end.toSeconds()-dayPart.start.toSeconds());
		//System.out.print("\t"+color);
	}

}
