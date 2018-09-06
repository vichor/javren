package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "src/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	
	private Light loadedLights[];
	private Light zeroLight;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		loadedLights = new Light[MAX_LIGHTS];
		zeroLight = new Light(new Vector3f(0,0,0), new Vector3f(0,0,0));
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "poisitions");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		for (int i=0; i<MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");
			location_lightColor[i] = super.getUniformLocation("lightColor["+i+"]");
			
		}
	}
	
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}


	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.craeteViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	
	public void loadLights(List<Light> lights) {
		for (int i=0; i<MAX_LIGHTS; i++) {
			if(i<lights.size()) {
				Light light = lights.get(i);
				if (!lightLoaded(light)) {
					super.loadVector(location_lightPosition[i], light.getPosition());
					super.loadVector(location_lightColor[i], light.getColor());
				}
				loadedLights[i] = light;
			}else {
				if (!lightLoaded(zeroLight)){
					super.loadVector(location_lightPosition[i], zeroLight.getPosition());
					super.loadVector(location_lightColor[i], zeroLight.getColor());
				}
				loadedLights[i] = zeroLight;
			}
		}
	}


	private boolean lightLoaded(Light light) {
		for (int i=0; i<MAX_LIGHTS; i++) {
			if (loadedLights[i] == light) {
				return true;
			}
		}
		return false;
	}
	
	
	public void loadShineVariables(float shineDamper, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	
	public void loadFakeLightingVariable(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r,g,b));
	}
	
	
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	
	public void loadOffset(float x, float y) {
		super.load2DVector(location_offset, new Vector2f(x, y));
	}
	
}
