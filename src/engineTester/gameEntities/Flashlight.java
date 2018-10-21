package engineTester.gameEntities;

import org.lwjgl.input.Keyboard;
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
	private float distanceFromEntity = 0.8f;
	private float heightFromPlayer = 2.35f;
	
	private static TexturedModel texturedModel = null;
	private Light lightSource;
	private Vector3f lightColor;
	private boolean lightOnKey;
	
	public Flashlight(Player player, Vector3f color, Vector3f attenuation) {
		if (texturedModel == null) {
	        texturedModel = new TexturedModel(OBJLoader.loadObjModel("lights/flashlight2", loader),
	        		new ModelTexture(loader.loadTexture("lights/Flashlight")));
		}

		this.player = player;
		entity = new Entity(texturedModel, new Vector3f(), player.getRotX(), player.getRotY(), player.getRotZ(), 0.15f);
		this.lightColor = color;
		lightOnKey = false;
		lightSource = new Light(new Vector3f(), lightColor, attenuation);
		
		// force one move step to update entity and lightsource position and rotation against player ones
		move();
	}
	
	public Light getLightSource() {
		return lightSource;
	}
	
	
	@Override
	public void setPosition(Vector3f position) {
		entity.setPosition(position);
		// place the light source following the flashlight entity. See below move method to get a glimpse
		// of the maths behind this.
		float lightSourceOffsetX = distanceFromEntity * (float)Math.sin(Math.toRadians(entity.getRotY()+5));
		float lightSourceOffsetZ = distanceFromEntity * (float)Math.cos(Math.toRadians(entity.getRotY()+5));
		Vector3f lightPosition = new Vector3f(
				position.x + lightSourceOffsetX, 
				position.y, 
				position.z + lightSourceOffsetZ);
		lightSource.setPosition(lightPosition);
	}
	
	public void setRotation(float rotX, float rotY, float rotZ) {
		entity.setRotX(rotX);
		entity.setRotY(rotY);
		entity.setRotZ(rotZ);
	}
	
	public void move() {
		checkInputs();
		/*
			Z
			|      *
			|    / |new X = sin(alpha)*rad; new Z = cos(alpha)*rad
			|  /   |     
			|/ |alpha (angle the player has rotated)
			+------|-----X
			player
			position
		*/
		// position shall follow players rotation. If done so, the flashlight would be put in the back 
		// of the player. To place it in the hand, apply 90deegrees offset.
		float positionOffsetX = distanceFromPlayer * (float)Math.sin(Math.toRadians(player.getRotY()+90));
		float positionOffsetZ = distanceFromPlayer * (float)Math.cos(Math.toRadians(player.getRotY()+90));
		Vector3f newPosition = new Vector3f(0,0,0);
		newPosition.x = player.getPosition().x - positionOffsetX;
		newPosition.y = player.getPosition().y + heightFromPlayer; 
		newPosition.z = player.getPosition().z - positionOffsetZ;
		setPosition(newPosition);
		setRotation(player.getRotX(), player.getRotY(), player.getRotZ());
	}
	
	
	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {	
			if (!lightOnKey) {
				toggleLightOn();
				lightOnKey = true;
			}
		} else {
			lightOnKey = false;
		}
	}
	
	private void toggleLightOn() {
		Vector3f currentColor = lightSource.getColor();
		if (currentColor.x == 0 && currentColor.y==0 && currentColor.z==0) {
			lightSource.setColor(lightColor);
		} else {
			lightSource.setColor(new Vector3f(0,0,0));
		}
	}
	
}
