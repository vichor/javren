package engineTester;

import java.nio.file.Paths;
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
import platform.Library;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {


	public static void main(String[] args) {
		
		// SETUP PLATFORM ENVIRONMENT
		Library.LibraryPathConfiguration(Paths.get("lib", "natives"));

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
		
		// Textured models
        RawModel model = OBJLoader.loadObjModel("vegetation/tree", loader);
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("vegetation/tree")));
        
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("vegetation/grassmodel", loader), 
        		new ModelTexture(loader.loadTexture("vegetation/grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("vegetation/fern", loader), 
        		new ModelTexture(loader.loadTexture("vegetation/fern")));
        fern.getTexture().setHasTransparency(true);
        
		// Instances
        for(int i=0;i<500;i++){
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800-400, 0, random.nextFloat()*-600),0,0,0,5));
            entities.add(new Entity(grass,       new Vector3f(random.nextFloat()*800-400, 0, random.nextFloat()*-600),0,0,0,1));
            entities.add(new Entity(fern,        new Vector3f(random.nextFloat()*800-400, 0, random.nextFloat()*-600),0,0,0,1));
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
