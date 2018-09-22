/*
 * render engine changes for water tutorial
 * 		MasterRenderer far plane set to 5000
 * 		entity and terrain shaders fog density set to 0
 * 		skyboxrenderer size set to 2500
 * 		skybox renderer blend function forces always to use day texture and avoids night texture
 * 		skybox fragment shader blend factor between skybox texture and fog set to 1.0
 * 		camera POINT_TO_PLAYER_FORWARD_OFFSET set to 0
 */
package engineTester;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engineTester.gameEntities.Fern;
import engineTester.gameEntities.FirTree;
import engineTester.gameEntities.Flower;
import engineTester.gameEntities.GameEntity;
import engineTester.gameEntities.Grass;
import engineTester.gameEntities.Lamp;
import engineTester.gameEntities.Tree;
import entities.Camera;
import entities.Entity;
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
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {


	public static void main(String[] args) {
		
		// SETUP PLATFORM ENVIRONMENT
		Library.LibraryPathConfiguration(Paths.get("lib", "natives"));

		// RENDER SYSTEM CREATION
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);
		
		
		// TERRAIN
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrains/grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrains/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrains/pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrains/path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("maps/blendMap"));
        
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "maps/heightMap2");
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		
		
		// ENTITIES DEFINITION
		
		// Entities containers and tools
        List<GameEntity> gameEntities = new ArrayList<GameEntity>();
		Random random = new Random();
		
		// Game entities instances
		GameEntity.setLoader(loader);
        for(int i=0;i<500;i++){
        	if (i%10 == 0) {
        		Vector3f position = getNewPosition(random, terrain);
                gameEntities.add(new FirTree(position));
        	}
            if (i%30 == 0) {
        		Vector3f position = getNewPosition(random, terrain);
                gameEntities.add(new Tree(position));
            }
            if (i%5==0) {
        		Vector3f position = getNewPosition(random, terrain);
                gameEntities.add(new Grass(position));
            }
            if (i%7==0) {
        		Vector3f position = getNewPosition(random, terrain);
                gameEntities.add(new Flower(position));
            }
            if(i%5==0) {
        		Vector3f position = getNewPosition(random, terrain);
                gameEntities.add(new Fern(position));
            }
        }
        
        // Lamps
		Lamp lamp1 = new Lamp(new Vector3f(120, terrain.getHeightOfTerrain(120,  -70), -70), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f));
		Lamp lamp2 = new Lamp(new Vector3f(170, terrain.getHeightOfTerrain(170, -90), -90),  new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f));
		Lamp lamp3 = new Lamp(new Vector3f(200, terrain.getHeightOfTerrain(200, -120), -120), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f));
		gameEntities.add(lamp1);
		gameEntities.add(lamp2);
		gameEntities.add(lamp3);
        

        // PLAYER
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("players/person", loader), 
        		new ModelTexture(loader.loadTexture("players/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        
        
        // CAMERA
        Camera camera = new Camera(player);
		
		// ENVIRONMENT / LIGHTS
		Light sun = new Light(new Vector3f(0,10000,20000),new Vector3f(1,1,1));
		//Light sun = new Light(new Vector3f(0,10000,20000),new Vector3f(1.0f,1.0f,1.0f));
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun);
		lights.add(lamp1.getLightSource());
		lights.add(lamp2.getLightSource());
		lights.add(lamp3.getLightSource());
		
		
		// GUI
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		//GuiTexture gui = new GuiTexture(loader.loadTexture("gui/socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.15f, 0.15f));
		//GuiTexture gui2 = new GuiTexture(loader.loadTexture("gui/health"), new Vector2f(0.4f, 0.48f), new Vector2f(0.15f, 0.15f));
		//guis.add(gui);
		//guis.add(gui2);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		

		// WATER
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix());
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(400, -400, 0));
		
		// DEMO FBOs
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		GuiTexture fboDemoGui = new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f,0.25f));
		guis.add(fboDemoGui);


		// MOUSE PICKER
		MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		
		
		// GAME LOOP
		
        /* get list of render entities needed for render engine */
        List<Entity> entities = new ArrayList<Entity>();
        for (GameEntity gameEntity : gameEntities) {
        	entities.add(gameEntity.getRenderEntity());
        }
        entities.add(player);

		while(!Display.isCloseRequested() ) {

			player.move(terrain);
			camera.move();
			mousePicker.update();

			// Some app layer stuff: move lamp1 with mouse and manage wireframe mode
			//GameEntityMouseMover.update(mousePicker, lamp1);
			WireframeToggler.checkAndToggle();
			System.out.println(player.getPosition());

			// FBO demos
			fbos.bindReflectionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera);
			fbos.unbindCurrentFrameBuffer();

			// Rendering
			renderer.renderScene(entities, terrains, lights, camera);
			waterRenderer.render(waters, camera);
			guiRenderer.render(guis);

			DisplayManager.updateDisplay();			
		}
		
		// closing
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	
	private static Vector3f getNewPosition(Random random, Terrain terrain) {
		boolean done = false;
		float x=0,y=0,z=0;
		while (!done) {
        	x = random.nextFloat()*800; 
        	z = random.nextFloat()*-800; 
        	// avoid water area by hardcoded coordinates check
        	// water areas: 
        	// z 638, 315	638, 196
        	// x 670, 498	498,205
        	if ( x<205 ) { 
        		done = true; 
        	} else if ( x < 498 ) {
        		if ( z>-196 || z<-638 ) { 
        			done = true; 
        		}
        	} else if ( x<670 ) {
        		if ( z>-315 || z<-638 ) {
        			done = true;
        		}
        	} else if (z<-638) {
        		done = true;
        	} else if ( x > 670 ) {
        		done = true;
        	}
		}
       	y = terrain.getHeightOfTerrain(x, z);
		return new Vector3f(x,y,z);
	}

}
