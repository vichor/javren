package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;

public class MousePicker {
	
	
	private Vector3f currentRay;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	

	public MousePicker(Camera camera, Matrix4f projectionMatrix) {
		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = Maths.createViewMatrix(camera);
		
	}
	
	public Vector3f getCurrentRay() {
		return currentRay;
	}
	
	
	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
	}
	
	
	private Vector3f calculateMouseRay() {
		// This method has to revert from screen coordinates to world coordinates
		// normal transformation does:
		// model --> world --> eye --> homogeneous clip --> normalized device --> viewport
		// we need to revert from viewport (screen coordinates) down to world space coordinates
		
		// viewport space
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		
		// normalized device space: simple equation based on window size
		// these are already between -1 and 1
		Vector2f normalizedCoordinates = getNormalizedDeviceCoordinates(mouseX, mouseY);
		
		// homogeneous clip space: add Z == -1 and W component (1)
		Vector4f clipCoordinates = new Vector4f(normalizedCoordinates.x, normalizedCoordinates.y, -1f, 1f);
		
		// eye space: inverse projection matrix
		Vector4f eyeCoordinates = toEyeCoordinates(clipCoordinates);
		
		// world space: inverse view matrix
		Vector3f worldRay = toWorldCoordinates(eyeCoordinates);
		
		return worldRay;
	}
	
	
	private Vector2f getNormalizedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2f*mouseX) / Display.getWidth() - 1;
		float y = (2f*mouseY) / Display.getHeight() - 1;
		return new Vector2f(x,y);
	}
	
	
	private Vector4f toEyeCoordinates(Vector4f clipCoordinates) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null); // get transformation matrix
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoordinates, null); // apply transformation
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}
	
	
	private Vector3f toWorldCoordinates(Vector4f eyeCoordinates) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null); // get transformation matrix
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoordinates, null); // apply transformation
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}
	

}
