package engineTester.GUIs;

import org.lwjgl.util.vector.Vector2f;

import engineTester.WorldTimeManager.WorldClock;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import renderEngine.Loader;

public class ClockInfo {
	
	private GUIText guiTextClock;
	private GUIText guiTextDayPart;
    
	public ClockInfo(Loader loader) {
		FontType font = new FontType(loader.loadFontTextureAtlas("fonts/candara"), "candara");
		guiTextClock= new GUIText(WorldClock.get().getClock().toString(), 1.1f, font, new Vector2f(0.88f,0.0005f), 1f, false);
		guiTextClock.setColor(0.5f, 0.6f, 0.7f);

	    String daypart = WorldClock.get().getDayPart().toString().toLowerCase() + "(" + 100.0f*WorldClock.get().getDayPartProgress() + "%)";
		guiTextDayPart= new GUIText(daypart, 1.1f, font, new Vector2f(0.88f,0.0285f), 1f, false);
		guiTextDayPart.setColor(0.5f, 0.6f, 0.7f);
	}
			//System.out.print("It is " + worldClock + " [" + 100.0f*worldClock.getDayPartProgress() + "% completed] --> ");

	
	public void update() {
	    guiTextClock.setTextString(WorldClock.get().getClock().toString());
	    String daypart = WorldClock.get().getDayPart().toString().toLowerCase() + " (" + (int)(100*WorldClock.get().getDayPartProgress()) + "%)";
	    guiTextDayPart.setTextString(daypart);
	}

}
