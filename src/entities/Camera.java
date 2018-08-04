package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private static final float INC_POSITION = 0.5f;
	private static final float INC_ROTATION = 0.5f;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 0;	// rotation X
	private float yaw = 0;		// rotation Y
	private float roll = 0;		// rotation Z
	
	public Camera() {}
	public Camera(Vector3f position, float pitch, float yaw, float roll) {
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	public void move() {
		// translation of camera
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			position.y -= INC_POSITION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			position.y += INC_POSITION;
		}
	
		// rotation of camera
		if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
			pitch += INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
			pitch -= INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
			yaw -= INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
			yaw += INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
			roll -= INC_ROTATION;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			roll += INC_ROTATION;
		}
	}


	public Vector3f getPosition() {
		return position;
	}


	public float getPitch() {
		return pitch;
	}


	public float getYaw() {
		return yaw;
	}


	public float getRoll() {
		return roll;
	}

	
}
