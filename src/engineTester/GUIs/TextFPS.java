package engineTester.GUIs;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import renderEngine.Loader;

public class TextFPS {
	
	private String text;
	private GUIText guiText;
    
    private int fps; 			/** frames per second */
    private long lastFps; 		/** last fps time */
	
	public TextFPS(Loader loader) {
		lastFps = getTime();
		fps = 0;
		text = String.valueOf(lastFps);
		FontType font = new FontType(loader.loadFontTextureAtlas("fonts/candara"), "candara");
		guiText = new GUIText(text, 10, font, new Vector2f(0,0.75f), 1f, true);
		guiText.setColor(1.0f, 0.0f, 0.0f);
	}
	
    /**
     * Get the accurate system time
     * 
     * @return The system time in milliseconds
     */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
    
    /**
     * Update the FPS GUIText to the new FPS value
     * @param newFps
     */
	public void updateFps() {
	    if (getTime() - lastFps > 1000) {
	    	float r, g, b;
	    	if (fps > 60) {
	    		r = 0; g = 1.0f; b = 0;
	    	} else if (fps > 15) {
	    		r = 1.0f; g = 1.0f; b = 0;
	    	} else {
	    		r = 1.0f; g= 0; b = 0;
	    	}
	    	guiText.setColor(r, g, b);
	    	guiText.setTextString(String.valueOf(fps));
	        fps = 0; 			//reset the FPS counter and 
	        lastFps += 1000; 	//add one second
	    }
	    fps++;
	}

}
