package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import toolbox.MousePicker;

public class GameEntityMouseMover {
	
	public static void update(MousePicker mousePicker, GameEntity entity) {
		Vector3f terrainPoint = mousePicker.getCurrentTerrainPoint();
		if (terrainPoint != null) {
			entity.setPosition(terrainPoint);
		}
	}

}
