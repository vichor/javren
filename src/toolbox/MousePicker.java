package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engineTester.WorldManager.World;
import terrains.Terrain;
import entities.Camera;

public class MousePicker {

    // how many times the binary search is applied to find the intersection 
	// point with the terrain. Be careful! This means recursions of function.
	private static final int RECURSION_COUNT = 200; 

	private static final float RAY_RANGE = 600;  // how far the ray trace reaches

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	// if using pack of terrains, this has to be terrains[][] and the constructor
	// needs to be updated.
	private World world;
	private Vector3f currentTerrainPoint;


	public MousePicker(Camera camera, Matrix4f projection, World world) {
		this.camera = camera;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.world = world;
	}
	
	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		// Check intersection with terrain: find which is the ray point intersecting with th terrain
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);  
		} else {
			currentTerrainPoint = null;
		}
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

	private Vector3f toWorldCoordinates(Vector4f eyeCoordinates) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoordinates, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoordinates(Vector4f clipCoordinatess) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoordinatess, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalizedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / Display.getWidth() - 1f;
		float y = (2.0f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}
	
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		// check the middle point of the ray trace and see if it is above or 
		// below ground
		float half = start + ((finish - start) / 2f);
		// Limit the search to a specific amount of recursions
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		// if the point of the ray is above the ground, apply the search on the 
		// far segment of the ray; otherwise, apply search on the near segment
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Terrain getTerrain(float worldX, float worldZ) {
		// This method is here just in case we use a pack of terrains.
		// In that case we need to return the terrain specific with
		// something like
		// int x = worldX / Terrain.SIZE
		// int y = worldY / Terrain.SIZE
		// return terrains[x][z];
		return world.getTerrain(worldX, worldZ);
	}

}

