package engineTester.gameEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objconverter.OBJLoader;
import textures.ModelTexture;

public class Flashlight extends GameEntity {
	private Player player;
	private float distanceFromPlayer = 1.250f;
	private float heightFromPlayer = 2.35f;
	
	private static TexturedModel texturedModel = null;
	private Light lightSource;
	private Vector3f lightSourceOffset;
	
	public Flashlight(Player player, Vector3f color, Vector3f attenuation) {
		if (texturedModel == null) {
	        texturedModel = new TexturedModel(OBJLoader.loadObjModel("lights/Flashlight", loader),
	        		new ModelTexture(loader.loadTexture("lights/Flashlight")));
	        //texturedModel.getTexture().setUseFakeLighting(true);
		}

		this.player = player;
		entity = new Entity(texturedModel, new Vector3f(0,0,0), player.getRotX(), player.getRotY(), player.getRotZ(), 0.15f);

		lightSourceOffset = new Vector3f(1.2f, 2.0f, 0.5f);
		Vector3f lightPosition = new Vector3f(
				entity.getPosition().x + lightSourceOffset.x, 
				entity.getPosition().y + lightSourceOffset.y, 
				entity.getPosition().z + lightSourceOffset.z);
		lightSource = new Light(lightPosition, color, attenuation);
		
		// force one move step to update position, rotation and lightsource to player's position
		move();
	}
	
	public Light getLightSource() {
		return lightSource;
	}
	
	
	@Override
	public void setPosition(Vector3f position) {
		entity.setPosition(position);
		Vector3f lightPosition = new Vector3f(
				position.x + lightSourceOffset.x, 
				position.y + lightSourceOffset.y, 
				position.z + lightSourceOffset.z);
		lightSource.setPosition(lightPosition);
	}
	
	public void setRotation(float rotX, float rotY, float rotZ) {
		entity.setRotX(rotX);
		entity.setRotY(rotY);
		entity.setRotZ(rotZ);
	}
	
	public void move() {
		/*
			Z
			|      *
			|    / |new X = sin(alpha)*rad; new Z = cos(alpha)*rad
			|  /   |     
			|/ |alpha
			+------|-----X
		*/
		// position shall follow players rotation. If done so, the flashlight would be put in the back 
		// of the player. To place it in the hand, apply 90deegrees offset.
		float offsetx = distanceFromPlayer * (float)Math.sin(Math.toRadians(player.getRotY()+90));
		float offsetz = distanceFromPlayer * (float)Math.cos(Math.toRadians(player.getRotY()+90));
		Vector3f newPosition = new Vector3f(0,0,0);
		newPosition.x = player.getPosition().x - offsetx;
		newPosition.y = player.getPosition().y + heightFromPlayer; 
		newPosition.z = player.getPosition().z - offsetz;
		setPosition(newPosition);
		setRotation(player.getRotX(), player.getRotY(), player.getRotZ());
	}
	
}
