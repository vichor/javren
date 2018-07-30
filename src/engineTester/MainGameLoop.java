package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.ShaderProgram;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {


	public static void main(String[] args) {

		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();
		
		// OpenGL expects vertices to be defined counter clockwise by default
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
		
		/*
		RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
		ModelTexture texture = new ModelTexture(loader.loadTexture("image"));
		TexturedModel staticModel = new TexturedModel(model, texture);
		*/
		
		Entity entity = new Entity(
				new TexturedModel( 													// textured model
						loader.loadToVAO(vertices,  textureCoords,  indices),		//		raw model 
						new ModelTexture(loader.loadTexture("image")) ), 			//		texture
				new Vector3f(-1,0,0), 												// position
				0, 0, 0,															// rotation 
				1);																	// scale
		
		while(!Display.isCloseRequested() ) {
			entity.increasePosition(0.002f, 0, 0);
			entity.increaseRotation(0, 1, 0);
			renderer.prepare();
			shader.start();
			renderer.render(entity, shader);
			shader.stop();
			DisplayManager.updateDisplay();			
		}
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
