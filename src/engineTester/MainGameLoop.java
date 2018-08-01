package engineTester;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {


	public static void main(String[] args) {

		// Engine init
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		// Entities definition
		Entity entity = new Entity(
				new TexturedModel( 													// textured model
						OBJLoader.loadObjModel("dragon/dragon", loader),				// raw model obtained from OBJ file
						new ModelTexture(loader.loadTexture("dragon/skin")) ),			// texture
				new Vector3f(0,0,-25), 												// position
				0, 0, 0,															// rotation 
				1);																	// scale

		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1,1,1));		// White light placed in front of starting point of model

		Camera camera = new Camera();
		
		// Define material properties of the texture
		ModelTexture texture = entity.getModel().getTexture();
		texture.setReflectivity(1);
		texture.setShineDamper(10);
		

		// Game loop
		boolean paused = false;
		while(!Display.isCloseRequested() ) {
			// some game logic
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				paused = !paused;
			}
			if (!paused) {
				entity.increaseRotation(0, 1, 0);
			}
			entity.move();
			entity.roll();
			camera.move();
			camera.roll();
			
			// call the render engine
			renderer.prepare();
			shader.start();
			shader.loadLight(light);
			shader.loadViewMatrix(camera);
			renderer.render(entity, shader);
			shader.stop();
			DisplayManager.updateDisplay();			
		}
		
		// closing
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
