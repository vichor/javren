package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 0;	// rotation X
	private float yaw = 0;		// rotation Y
	private float roll = 0;		// rotation Z
	
	private Player player;
	private float distanceFromPlayer =50;
	private float angleAroundPlayer = 0;
	
	private static final float POINT_TO_PLAYER_BODY_OFFSET = 5;		// This causes to zoom towards the body, not the feet
	private static final float POINT_TO_PLAYER_FORWARD_OFFSET = 15; // this is to place the player in the lower area of the viewport
	

	public Camera(Player player) {
		initCameraPlayer(player);
	}

	public Camera(Player player, Vector3f position, float pitch, float yaw, float roll) {
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		initCameraPlayer(player);
	}
	
	private void initCameraPlayer(Player player) {
		this.player = player;
	}
	
	
	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
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
	
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float playerCameraAngle = angleAroundPlayer + player.getRotY();
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(playerCameraAngle)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(playerCameraAngle)));
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticalDistance + POINT_TO_PLAYER_BODY_OFFSET;
		position.z = player.getPosition().z - offsetZ;
	}
	
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch+POINT_TO_PLAYER_FORWARD_OFFSET)));
	}
	

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch+POINT_TO_PLAYER_FORWARD_OFFSET)));
	}
	
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		// Limit zoom to avoid getting too close to player
		if (distanceFromPlayer < 5) {
			distanceFromPlayer = 5;
		}
	}
	
	
	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			// Limit the pitch to avoid looking "below" the terrain and "above" player top-view
			if (pitch < -10) { 
				pitch = -10;
			} else if (pitch > 90) { 
				pitch = 90;
			}
		}
	}
	
	
	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}

	
}
