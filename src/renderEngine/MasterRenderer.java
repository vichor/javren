package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

public class MasterRenderer {

	// Fog
	private static final float SKYCOLOR_RED = 0.0f;
	private static final float SKYCOLOR_GREEN = 0.85f*0.25f;
	private static final float SKYCOLOR_BLUE = 0.95f*0.25f;
	
	// Projection matrix 
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private Matrix4f projectionMatrix;

	// Entity renderer
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private StaticShader entityShader;
	private EntityRenderer entityRenderer;
	
	// Terrain renderer
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	
	
	public MasterRenderer() {
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
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(SKYCOLOR_RED, SKYCOLOR_GREEN, SKYCOLOR_BLUE, 1);
	}
	
	
	public void render(List<Light> lights, Camera camera) {
		// A new render cycle is about to start
		prepare();

		// Render entities
		// each cycle, the entities to be rendered change, so they are removed from the hash map.
		// the application shall push them again each cycle.
		entityShader.start();
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
		terrainShader.loadLights(lights);
		terrainShader.loadSkyColor(SKYCOLOR_RED, SKYCOLOR_GREEN, SKYCOLOR_BLUE);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		terrains.clear();
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
}
