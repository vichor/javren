package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {


	public static void main(String[] args) {

		// engine init
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		// Cube model (vertices, texture coordinates and indices)

		
		// Entity definition
		Entity entity = new Entity(
				new TexturedModel( 													// textured model
						OBJLoader.loadObjModel("stall", loader),					// 		raw model obtained from OBJ file
						new ModelTexture(loader.loadTexture("stallTexture")) ),		// 		texture
				new Vector3f(0,0,-25), 												// position
				0, 0, 0,															// rotation 
				1);																	// scale
		Camera camera = new Camera();
		

		// game loop
		while(!Display.isCloseRequested() ) {
			// some game logic
			//entity.increaseRotation(0, 1, 0);
			entity.move();
			entity.roll();
			camera.move();
			camera.roll();
			
			// call the render engine
			renderer.prepare();
			shader.start();
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
