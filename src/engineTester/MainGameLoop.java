package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {


	public static void main(String[] args) {

		// RENDER SYSTEM CREATION
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		
		// ENTITIES DEFINITION
		
		// Entities containers and tools
		List<Entity> dragons = new ArrayList<Entity>();
		List<Entity> stalls = new ArrayList<Entity>();
		Random random = new Random();
		
		// Dragon entities 
		RawModel rawModelDragon = OBJLoader.loadObjModel("dragon/dragon", loader);
		ModelTexture textureDragon = new ModelTexture(loader.loadTexture("dragon/skin"));
		textureDragon.setReflectivity(1);	// texture material properties
		textureDragon.setShineDamper(10);
		TexturedModel modelDragon = new TexturedModel(rawModelDragon, textureDragon);
		for (int i = 0; i < 50; i++) {
			Vector3f position = new Vector3f(random.nextFloat()*100-50, random.nextFloat()*100,  random.nextFloat()*-300); 												// position
			Vector3f rotation = new Vector3f(random.nextFloat()*180f,   random.nextFloat()*180f, 0f);
			dragons.add(new Entity(modelDragon, position, rotation.x, rotation.y, rotation.z, 1));
		}
		
		// Stall entities
		RawModel rawModelStall = OBJLoader.loadObjModel("stall/stall", loader);
		ModelTexture textureStall = new ModelTexture(loader.loadTexture("stall/stallTexture"));
		textureStall.setReflectivity(0.1f);
		textureStall.setShineDamper(0.2f);
		TexturedModel modelStall = new TexturedModel(rawModelStall, textureStall);
		for (int i = 0; i < 50; i++) {
			Vector3f position = new Vector3f(random.nextFloat()*100-50, random.nextFloat()*100,  random.nextFloat()*-300); 												// position
			Vector3f rotation = new Vector3f(random.nextFloat()*180f,   random.nextFloat()*180f, 0f);
			stalls.add(new Entity(modelStall, position, rotation.x, rotation.y, rotation.z, 1));
		}
		
		// ENVIRONMENT
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1,1,1));		// White light placed in front of starting point of model
		
		// GAME LOOP
		while(!Display.isCloseRequested() ) {
			// some game logic
			camera.move();
			
			// push entities into render system
			for (Entity dragon:dragons) {
				renderer.processEntity(dragon);
			}
			for (Entity stall:stalls) {
				renderer.processEntity(stall);
			}

			// call the render engine
			renderer.render(light, camera);
			DisplayManager.updateDisplay();			
		}
		
		// closing
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
