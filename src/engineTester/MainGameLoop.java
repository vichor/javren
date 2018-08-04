package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {


	public static void main(String[] args) {

		// RENDER SYSTEM CREATION
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Camera camera = new Camera(new Vector3f(0,10,0), 0, 10, 0);
		MasterRenderer renderer = new MasterRenderer();
		
		// ENTITIES DEFINITION
		
		// Entities containers and tools
        List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		
		// ENTITIES
		
		// Trees
        RawModel model = OBJLoader.loadObjModel("tree/tree", loader);
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree/tree")));
        for(int i=0;i<500;i++){
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
        }
		
		// TERRAIN
		Terrain terrain1 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("terrains/grass")));
		Terrain terrain2 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("terrains/grass")));
		
		// ENVIRONMENT
		Light sun = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
		
		// GAME LOOP
		while(!Display.isCloseRequested() ) {
			// some game logic
			camera.move();
			
			// push terrains and entities into render system
			renderer.processTerrain(terrain1);
			renderer.processTerrain(terrain2);
			for (Entity entity:entities) {
				renderer.processEntity(entity);
			}

			// call the render engine
			renderer.render(sun, camera);
			DisplayManager.updateDisplay();			
		}
		
		// closing
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
