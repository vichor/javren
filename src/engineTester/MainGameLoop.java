package engineTester;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJFileLoader;
import objconverter.OBJLoader;
import platform.Library;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {


	public static void main(String[] args) {
		
		// SETUP PLATFORM ENVIRONMENT
		Library.LibraryPathConfiguration(Paths.get("lib", "natives"));

		// RENDER SYSTEM CREATION
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		
		
		// TERRAIN
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrains/grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrains/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrains/pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrains/path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("maps/blendMap"));
        
		Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap, "maps/heightMap");
		
		
		// ENTITIES DEFINITION
		
		// Entities containers and tools
        List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		
		// ENTITIES
		
		// Textured models
        RawModel firTreeRawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/tree"));
        TexturedModel firTreeModel = new TexturedModel(firTreeRawModel,new ModelTexture(loader.loadTexture("vegetation/tree")));
        RawModel treeRawModel = loader.loadToVAO(OBJFileLoader.loadOBJ("vegetation/lowpolytree"));
        TexturedModel treeModel = new TexturedModel(treeRawModel,new ModelTexture(loader.loadTexture("vegetation/lowpolytree")));
        
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("vegetation/grassmodel", loader), 
        		new ModelTexture(loader.loadTexture("vegetation/grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);

        TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("vegetation/grassmodel", loader), 
        		new ModelTexture(loader.loadTexture("vegetation/flower")));
        flower.getTexture().setHasTransparency(true);
        flower.getTexture().setUseFakeLighting(true);
        TexturedModel box = new TexturedModel(OBJLoader.loadObjModel("objects/box", loader),
        		new ModelTexture(loader.loadTexture("objects/box")));
        
        // Atlas textures
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("vegetation/fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("vegetation/fern", loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);
        
		// Instances
        for(int i=0;i<500;i++){
        	if (i%10 == 0) {
        		float x = random.nextFloat()*800-400; 
        		float z = random.nextFloat()*-600; 
        		float y = terrain.getHeightOfTerrain(x, z);
        		entities.add(new Entity(firTreeModel,new Vector3f(x, y, z),0,0,0,5));
        	}
            if (i%30 == 0) {
        		float x = random.nextFloat()*800-400; 
        		float z = random.nextFloat()*-600; 
        		float y = terrain.getHeightOfTerrain(x, z);
            	entities.add(new Entity(treeModel,new Vector3f(x, y, z),0,0,0,0.6f));
            }
            if (i%5==0) {
        		float x = random.nextFloat()*800-400; 
        		float z = random.nextFloat()*-600; 
        		float y = terrain.getHeightOfTerrain(x, z);
            	entities.add(new Entity(grass,new Vector3f(x, y, z),0,0,0,1));
            }
            if (i%7==0) {
        		float x = random.nextFloat()*800-400; 
        		float z = random.nextFloat()*-600; 
        		float y = terrain.getHeightOfTerrain(x, z);
            	entities.add(new Entity(flower,new Vector3f(x, y, z),0,0,0,2));
            }
            if(i%5==0) {
        		float x = random.nextFloat()*800-400; 
        		float z = random.nextFloat()*-600; 
        		float y = terrain.getHeightOfTerrain(x, z);
        		// create the entity using alternate constructor to choose which texture index is to be used
            	entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z),0,0,0,1));
            }
        }
        

        // PLAYER
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("players/person", loader), 
        		new ModelTexture(loader.loadTexture("players/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        
        // Add boxes close to player
        for (int i = 0; i < 10; i++) {
        	Vector3f boxPosition = new Vector3f(
        			(float) (player.getPosition().x-(20-(10*Math.sin(Math.toRadians(30+(5*i)))))), // left to the player but getting closer to draw a diagonal line
        			player.getPosition().y+8, 
        			player.getPosition().z-(20*i));
        	entities.add(new Entity(box, boxPosition,0,0,0,6));
        }
        
        // CAMERA
        Camera camera = new Camera(player);
		
		// ENVIRONMENT
		Light sun = new Light(new Vector3f(0,0,20000),new Vector3f(1,1,1));
		
		// GUI
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("gui/socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.15f, 0.15f));
		GuiTexture gui2 = new GuiTexture(loader.loadTexture("gui/health"), new Vector2f(0.4f, 0.48f), new Vector2f(0.15f, 0.15f));
		guis.add(gui);
		guis.add(gui2);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		// GAME LOOP
		float sunAngle = 90f;
		while(!Display.isCloseRequested() ) {
			// some game logic
			player.move(terrain);
			camera.move();
			Vector3f sunPos = sun.getPosition();
			sunPos.x = (float) (20000*Math.cos(Math.toRadians(sunAngle)));
			sunPos.y = (float) (40000*Math.sin(Math.toRadians(sunAngle)));
			sunAngle+=0.1f;
			if (sunAngle >360f) {
				sunAngle = 0f;
			}
			
			// push players, terrains and entities into render system
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (Entity entity:entities) {
				renderer.processEntity(entity);
			}

			// call the render engine
			renderer.render(sun, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();			
		}
		
		// closing
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
