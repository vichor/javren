package skybox;
 
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngine.DisplayManager;
import shaders.ShaderProgram;
import toolbox.Maths;
 
public class SkyboxShader extends ShaderProgram{
 
    private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.glsl";
    
    private static final float ROTATE_SPEED = 1f;
     
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColor;
     
    private float rotation = 0f;
    
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }
 
    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        // Apply translation so that the skybox gets always placed at absolute coordinates 0,0,0
        // this way we avoid "reaching the sky" effect (when walking, we can reach sky position)
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        // skybox rotation around the camera. We do this by using the view matrix instead of
        // the transformation matrix, taking profit of the fact that the view matrix is used
        // just to center the skybox within the camera and the rest of the fields are unused
        // so far. This will improve performance (avoid using another matrix).
        rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,1,0), matrix, matrix);
        super.loadMatrix(location_viewMatrix, matrix);
    }
     
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColor = super.getUniformLocation("fogColor");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    
    
    public void loadFogColor(float r, float g, float b) {
    	super.loadVector(location_fogColor, new Vector3f(r,g,b));
    }
 
}