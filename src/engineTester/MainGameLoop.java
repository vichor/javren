package engineTester;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engineTester.gameEntities.Box;
import engineTester.gameEntities.Fern;
import engineTester.gameEntities.FirTree;
import engineTester.gameEntities.Flower;
import engineTester.gameEntities.GameEntity;
import engineTester.gameEntities.Grass;
import engineTester.gameEntities.Tree;
import entities.Camera;
import entities.Light;
import entities.Player;
import guis.GuiTexture;
import models.TexturedModel;
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
        
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "maps/heightMap");
		
		
		// ENTITIES DEFINITION
		
		// Entities containers and tools
        List<GameEntity> entities = new ArrayList<GameEntity>();
		Random random = new Random();
		
		// Game entities instances
		GameEntity.setLoader(loader);
        for(int i=0;i<500;i++){
        	if (i%10 == 0) {
        		float x = random.nextFloat()*800; 
        		float z = random.nextFloat()*-800; 
        		float y = terrain.getHeightOfTerrain(x, z);
               entities.add(new FirTree(new Vector3f(x,y,z)));
        	}
            if (i%30 == 0) {
        		float x = random.nextFloat()*800; 
        		float z = random.nextFloat()*-800; 
        		float y = terrain.getHeightOfTerrain(x, z);
               entities.add(new Tree(new Vector3f(x, y, z)));
            }
            if (i%5==0) {
        		float x = random.nextFloat()*800; 
        		float z = random.nextFloat()*-800; 
        		float y = terrain.getHeightOfTerrain(x, z);
               entities.add(new Grass(new Vector3f(x, y, z)));
            }
            if (i%7==0) {
        		float x = random.nextFloat()*800; 
        		float z = random.nextFloat()*-800; 
        		float y = terrain.getHeightOfTerrain(x, z);
               entities.add(new Flower(new Vector3f(x, y, z)));
            }
            if(i%5==0) {
        		float x = random.nextFloat()*800; 
        		float z = random.nextFloat()*-800; 
        		float y = terrain.getHeightOfTerrain(x, z);
               entities.add(new Fern(new Vector3f(x, y, z)));
            }
        }
        

        // PLAYER
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("players/person", loader), 
        		new ModelTexture(loader.loadTexture("players/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        
        // Add boxes close to player
        for (int i = 0; i < 10; i++) {
           float x = (float) (player.getPosition().x-(20*i)); // left to the player but getting closer to draw a diagonal line
           float z = player.getPosition().z-(20*i);
           Vector3f boxPosition = new Vector3f(x, terrain.getHeightOfTerrain(x, z)+7, z);
           entities.add(new Box(boxPosition));
        }
        
        // CAMERA
        Camera camera = new Camera(player);
		
		// ENVIRONMENT / LIGHTS
		Light sun = new Light(new Vector3f(0,10000,20000),new Vector3f(0.4f,0.4f,0.4f));
		Light red = new Light(new Vector3f(-200,10,-200), new Vector3f(1,0,0));
		Light blue = new Light(new Vector3f(200,10,200), new Vector3f(0,0,1)); 
		Light yellow = new Light(new Vector3f(0,10,1000), new Vector3f(0,1,1)); 
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun);
		lights.add(red);
		lights.add(blue);
		
		// GUI
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("gui/socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.15f, 0.15f));
		GuiTexture gui2 = new GuiTexture(loader.loadTexture("gui/health"), new Vector2f(0.4f, 0.48f), new Vector2f(0.15f, 0.15f));
		guis.add(gui);
		guis.add(gui2);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		// GAME LOOP
		float sunAngle = 90f;
		boolean keyAttended = false;
		while(!Display.isCloseRequested() ) {
			// some game logic
			player.move(terrain);
			camera.move();
			
			Vector3f sunPos = sun.getPosition();
			sunPos.x = (float) (800*Math.cos(Math.toRadians(sunAngle)));
			sunPos.y = (float) (10000*Math.sin(Math.toRadians(sunAngle)));
			sun.setPosition(sunPos);
			sunAngle+=0.1f;
			if (sunAngle >360f) {
				sunAngle = 0f;
			}
			System.out.println(sun.getPosition()+ " " + sunAngle);
			// Add/remove yellow light when pressing L key
			boolean toggleYellow = false;
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
				if (!keyAttended) {
					toggleYellow = true;
					keyAttended = true;
				}
			} else {
				keyAttended = false;
			}
			if (toggleYellow) {
				if (!lights.contains(yellow)) {
					lights.add(yellow);
				} else {
					lights.remove(yellow);
				}
			}
			
			// push players, terrains and entities into render system
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (GameEntity entity:entities) {
				renderer.processEntity(entity.getRenderEntity());
			}

			// call the render engine
			renderer.render(lights, camera);
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
