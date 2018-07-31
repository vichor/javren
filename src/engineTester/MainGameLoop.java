package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
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
		
		// Modeling & entities
		float[] vertices = {
				-0.5f, 0.5f, 0f,	// V0
				-0.5f, -0.5f, 0f,	// V1
				0.5f, -0.5f, 0f,	// V2
				0.5f, 0.5f, 0f		// V3
		};
		int[] indices = {
				0, 1, 3, // top left triangle
				3, 1, 2  // bottom right triangle
		};
		float[] textureCoords = {
				0, 0,	// V0
				0, 1,	// V1
				1, 1,	// V2
				1, 0	// V3
		};
		Entity entity = new Entity(
				new TexturedModel( 													// textured model
						loader.loadToVAO(vertices,  textureCoords,  indices),		//		raw model 
						new ModelTexture(loader.loadTexture("image")) ), 			//		texture
				new Vector3f(0,0,-1), 												// position
				0, 0, 0,															// rotation 
				1);																	// scale
		Camera camera = new Camera();
		

		// game loop
		while(!Display.isCloseRequested() ) {
			// some game logic
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
