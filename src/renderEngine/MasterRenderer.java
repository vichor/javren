package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;

public class MasterRenderer {

	// Fog
	private static final float SKYCOLOR_RED = 0.5444f;
	private static final float SKYCOLOR_GREEN = 0.62f;
	private static final float SKYCOLOR_BLUE = 0.69f;
	
	// Projection matrix 
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 5000;
	private Matrix4f projectionMatrix;

	// Entity renderer
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private StaticShader entityShader;
	private EntityRenderer entityRenderer;
	
	// Terrain renderer
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	
	// Skybox renderer
	private SkyboxRenderer skyboxRenderer;
	
	// Wireframe
	static boolean wireframeMode = false;
	
	
	public MasterRenderer(Loader loader) {
		// don't render back faces
		enableCulling();
		
		// setup projection matrix
		createProjectionMatrix();	

		// Entity renderer setup
		this.entityShader = new StaticShader();
		this.entityRenderer = new EntityRenderer(entityShader, projectionMatrix);

		// Terrain renderer setup
		this.terrainShader = new TerrainShader();
		this.terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);

		// Skybox renderer setup
		this.skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}
	
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}


	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(SKYCOLOR_RED, SKYCOLOR_GREEN, SKYCOLOR_BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		configureWireframeMode();
	}
	
	
	public void renderScene(List<Entity> entities, List<Terrain> terrains, 
			List<Light> lights, Camera camera, Vector4f clipPlane) {
		for(Terrain terrain : terrains) {
			processTerrain(terrain);
		}
		for(Entity entity : entities) {
			processEntity(entity);
		}
		render(lights, camera, clipPlane);
	}
	
	
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		// A new render cycle is about to start
		prepare();

		// Render entities
		// each cycle, the entities to be rendered change, so they are removed from the hash map.
		// the application shall push them again each cycle.
		entityShader.start();
		entityShader.loadClipPlane(clipPlane);
		entityShader.loadSkyColor(SKYCOLOR_RED, SKYCOLOR_GREEN, SKYCOLOR_BLUE);
		entityShader.loadLights(lights);
		entityShader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		entityShader.stop();
		entities.clear();
		
		// Render terrain
		// each cycle, the terrain to be rendered change, so they are removed from the list.
		// the application shall push them again each cycle.
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadLights(lights);
		terrainShader.loadSkyColor(SKYCOLOR_RED, SKYCOLOR_GREEN, SKYCOLOR_BLUE);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		terrains.clear();
		
		// Render skybox
		skyboxRenderer.render(camera, SKYCOLOR_RED, SKYCOLOR_GREEN, SKYCOLOR_BLUE);
	}
	
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	

	public void processEntity(Entity entity) {
		// this method pushes a new entity on the has map to be rendered on next render call
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null) {
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	

	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	

	public void cleanUp() {
		entityShader.cleanUp();
		terrainShader.cleanUp();
	}
	
	
	public static void toggleWireframeMode() {
		wireframeMode = !wireframeMode;
	}
	
	
	private void configureWireframeMode() {
		if (wireframeMode) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}else{
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}
	

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
