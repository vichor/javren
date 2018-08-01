package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {

	
	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}
	

	public TexturedModel getModel() {
		return model;
	}
	
	
	public void setModel(TexturedModel model) {
		this.model = model;
	}
	
	
	public Vector3f getPosition() {
		return position;
	}
	
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	
	public float getRotX() {
		return rotX;
	}
	
	
	public void setRotX(float rotX) {
		this.rotX = rotX;
	}
	
	
	public float getRotY() {
		return rotY;
	}
	
	
	public void setRotY(float rotY) {
		this.rotY = rotY;
	}
	
	
	public float getRotZ() {
		return rotZ;
	}
	
	
	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	
	
	public float getScale() {
		return scale;
	}
	
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	
	// ENTITY MOVEMENT -- Only for test, to be removed
	private static final float INC_POSITION = 0.1f;
	public void move() {
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			position.y -= INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			position.y += INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			position.x -= INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			position.x += INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7)) {
			position.z -= INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD9)) {
			position.z += INC_POSITION;
		}
	}
	private static final float INC_ROTATION = 1f;
	public void roll() {
		if (Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
			this.rotX -= INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
			this.rotX += INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
			this.rotY -= INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
			this.rotY += INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
			this.rotZ -= INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT)) {
			this.rotZ += INC_ROTATION;
		}	
	}
	
}
