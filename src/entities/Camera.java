package entities;

import org.lwjgl.util.vector.Vector3f;

public class Camera {

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
