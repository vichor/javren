package engineTester.gameEntities;

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
}
