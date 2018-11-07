package engineTester.gameEntities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import toolbox.MousePicker;

public class GameEntityMouseMover {
	
	private static boolean nKey = false;
	private static boolean move = false;
	
	public static void update(MousePicker mousePicker, GameEntity entity) {
		updateStatus();
		if (move) {
			Vector3f terrainPoint = mousePicker.getCurrentTerrainPoint();
			if (terrainPoint != null) {
				entity.setPosition(terrainPoint);
			}
		}
	}
	
	private static void updateStatus() {
		if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
			if (!nKey) {
				nKey = true;
				move = !move;
			}
		} else {
			nKey = false;
		}
	}

}
