package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import renderEngine.Loader;

public abstract class GameEntity {

	protected static Loader loader ;
	protected Entity entity;
	

	public static void setLoader(Loader loader) {
		GameEntity.loader = loader;
	}
	

	public Entity getRenderEntity() {
		return entity;
	}


	public void setPosition(Vector3f position) {
		entity.setPosition(position);
	}
}
