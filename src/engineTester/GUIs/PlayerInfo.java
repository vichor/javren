package engineTester.GUIs;

import org.lwjgl.util.vector.Vector2f;

import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import renderEngine.Loader;

public class PlayerInfo {
	
	private GUIText guiTextHeadingMaster;
	private GUIText guiTextHeadingPosition;
	private GUIText guiTextHeadingRotation;
	private GUIText guiTextInfoPosition;
	private GUIText guiTextInfoRotation;
	private Player player;
    
	public PlayerInfo(Loader loader, Player player) {
		FontType font = new FontType(loader.loadFontTextureAtlas("fonts/candara"), "candara");
		guiTextHeadingMaster = new GUIText("Player information", 1.1f, font, new Vector2f(0.005f,0.0005f), 1f, false);
		guiTextHeadingPosition = new GUIText("position: ", 1.1f, font, new Vector2f(0.005f,0.0285f), 1f, false);
		guiTextHeadingRotation = new GUIText("rotation: ", 1.1f, font, new Vector2f(0.005f,0.0565f), 1f, false);
		guiTextHeadingMaster.setColor(0.5f, 0.6f, 0.7f);
		guiTextHeadingPosition.setColor(0.5f, 0.6f, 0.7f);
		guiTextHeadingRotation.setColor(0.5f, 0.6f, 0.7f);
		
		this.player = player;
		String position = "(" + player.getPosition().x + ", " + player.getPosition().y + ", " + player.getPosition().z + ")";
		String rotation = "(" + player.getRotX() + ", " + player.getRotY() + ", " + player.getRotZ() + ")";
		guiTextInfoPosition = new GUIText(position, 1.1f, font, new Vector2f(0.065f,0.0285f), 1f, false);
		guiTextInfoRotation = new GUIText(position, 1.1f, font, new Vector2f(0.065f,0.0565f), 1f, false);
		guiTextInfoPosition.setColor(0.5f, 0.6f, 0.7f);
		guiTextInfoRotation.setColor(0.5f, 0.6f, 0.7f);
	}

	
	public void update() {
		String position = "(" + player.getPosition().x + ", " + player.getPosition().y + ", " + player.getPosition().z + ")";
		String rotation = "(" + player.getRotX() + ", " + player.getRotY() + ", " + player.getRotZ() + ")";
	    guiTextInfoPosition.setTextString(position);
	    guiTextInfoRotation.setTextString(rotation);
	}

}
