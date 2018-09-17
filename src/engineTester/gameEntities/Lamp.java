package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import models.TexturedModel;
import objconverter.OBJLoader;
import textures.ModelTexture;

public class Lamp extends GameEntity {
	
	private static TexturedModel texturedModel = null;
	private Light lightSource;
	private static final float LIGHT_SOURCE_OFFSET_FROM_ENTITY = 13.6f;
	
	public Lamp(Vector3f position, Vector3f color, Vector3f attenuation) {
		if (texturedModel == null) {
	        texturedModel = new TexturedModel(OBJLoader.loadObjModel("lights/lamp", loader),
	        		new ModelTexture(loader.loadTexture("lights/lamp")));
	        texturedModel.getTexture().setUseFakeLighting(true);
		}
		entity = new Entity(texturedModel, position, 0, 0, 0, 1);
		float ligthY = position.y+LIGHT_SOURCE_OFFSET_FROM_ENTITY;
		Vector3f lightPosition = new Vector3f(position.x, ligthY, position.z);
		lightSource = new Light(lightPosition, color, attenuation);
	}
	
	public Light getLightSource() {
		return lightSource;
	}
	
	
	public void setPosition(Vector3f position) {
		entity.setPosition(position);
		float ligthY = position.y+LIGHT_SOURCE_OFFSET_FROM_ENTITY;
		Vector3f lightPosition = new Vector3f(position.x, ligthY, position.z);
		lightSource.setPosition(lightPosition);
	}
	
	
	

}
