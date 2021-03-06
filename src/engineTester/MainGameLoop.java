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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engineTester.WorldTimeManager.DayPart;
import engineTester.WorldTimeManager.WorldClock;
import engineTester.gameEntities.Dragon;
import engineTester.gameEntities.Fern;
import engineTester.gameEntities.FirTree;
import engineTester.gameEntities.Flower;
import engineTester.gameEntities.GameEntity;
import engineTester.gameEntities.Grass;
import engineTester.gameEntities.Lamp;
import engineTester.gameEntities.Sun;
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
        	//if (i%30 == 0) {
        	//	Vector3f position = getNewPosition(random, 800, terrain);
            //    gameEntities.add(new Dragon(position));
        	//}
        	if (i%1  == 0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new FirTree(position));
        	}
            if (i%1  == 0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Tree(position));
            }
            if (i%5==0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Grass(position));
            }
            if (i%7==0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Flower(position));
            }
            if(i%5==0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Fern(position));
            }
        }
        gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -320),-320)));
        gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -330),-330)));
        gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -340),-340)));
        gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -350),-350)));
        gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -360),-360)));
        gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -370),-370)));
        
        // Lamps
		Lamp lamp1 = new Lamp(new Vector3f(120, terrain.getHeightOfTerrain(120,  -70), -70), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f));
		Lamp lamp2 = new Lamp(new Vector3f(170, terrain.getHeightOfTerrain(170, -90), -90),  new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f));
		Lamp lamp3 = new Lamp(new Vector3f(200, terrain.getHeightOfTerrain(200, -120), -120), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f));
		gameEntities.add(lamp1);
		gameEntities.add(lamp2);
		gameEntities.add(lamp3);
        

        // PLAYER
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("players/person", loader), //players/person", loader), 
        		new ModelTexture(loader.loadTexture("players/playertexture"))); //players/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(200, terrain.getHeightOfTerrain(200, -250), -250), 0, 120, 0, 0.6f);
        
        
        // CAMERA
        Camera camera = new Camera(player);
		
		// ENVIRONMENT / LIGHTS
		Sun sun = new Sun();
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

		// Frame Buffers
		// Create the frame buffer object which is linked with a texture. 
		WaterFrameBuffers frameBufferObjects = new WaterFrameBuffers();

		// Render system
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), frameBufferObjects);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(400, -400, 0);
		waters.add(water);


		// MOUSE PICKER
		MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		
		
		// GAME LOOP
		
        /* get list of render entities needed for render engine */
        List<Entity> entities = new ArrayList<Entity>();
        for (GameEntity gameEntity : gameEntities) {
        	entities.add(gameEntity.getRenderEntity());
        }
        entities.add(player);

        // Clip planes
        // Plane formula is Ax + By + Cz + D = 0
        //		(A,B,C) normal of the plane
        //		D is the distance from the origin
        // As we want an horizontal plane for water things
        //		(0, -1, 0) for clipping what's below the plane
        //		(0,  1, 0) for clipping what's above the plane
        //		D = height, choose a value depending on the height of the water
        // We also apply an offset to soften the effect once the water depth effect is applied.
        // do not apply so much an offset or reflection will be get in places it shouldn't
		Vector4f masterClipPlane = new Vector4f(0, -1, 0, 10000);
		Vector4f clipPlaneReflection = new Vector4f(0,  1, 0, -water.getHeight()+1f);
		Vector4f clipPlaneRefraction = new Vector4f(0, -1, 0, water.getHeight());

		WorldClock worldClock = WorldClock.get();
		//worldClock.getClock().hour=12;
		while(!Display.isCloseRequested() ) {

			worldClock.step(40);
			System.out.print("It is " + worldClock + " [" + 100.0f*worldClock.getDayPartProgress() + "% completed] --> ");

			sun.update();
			player.move(terrain);
			camera.move();
			mousePicker.update();

			// Refraction/Reflection needs clipping planes:
			//    - we want to refract what it's below water level
			//    - we want to reflect what it's above water level
			// So we will define a clipping plane at water level to filter out 
			// what it's not needed on each case. We need only one clipping 
			// plane (0).
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			// Some app layer stuff: move lamp1 with mouse and manage wireframe mode
			//GameEntityMouseMover.update(mousePicker, lamp1);
			WireframeToggler.checkAndToggle();

			// FRAME BUFFER OBJECTS RENDERING
			// render the scene into a frame buffer object. The gui using
			// the texture from this buffer object will present an inverted image of
			// the rendered scene (inverted because the texture coordinates are inverted
			// when compared with render coordinates)
			
			// render reflection texture
			frameBufferObjects.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitchAndRoll();
			renderer.renderScene(entities, terrains, lights, camera, clipPlaneReflection);
			camera.invertPitchAndRoll();
			camera.getPosition().y += distance;

			// render refraction texture
			frameBufferObjects.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, clipPlaneRefraction);

			// unbind fbo
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);  // drivers may ignore this call so the masterClipPlane has to define a really high height
			frameBufferObjects.unbindCurrentFrameBuffer();

			// DEFAULT FRAME BUFFER RENDERING
			renderer.renderScene(entities, terrains, lights, camera, masterClipPlane);
			waterRenderer.render(waters, camera, sun);
			guiRenderer.render(guis);

			DisplayManager.updateDisplay();			
		}
		
		// closing
		frameBufferObjects.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	
	private static Vector3f getNewPosition(Random random, float radius, Terrain terrain) {
		boolean done = false;
		float x=0,y=0,z=0;
		while (!done) {
        	x = random.nextFloat()*radius; 
        	z = random.nextFloat()*(-radius); 
        	y = terrain.getHeightOfTerrain(x, z);
        	if (y>0) {
        		done = true;
        	}
		}
		return new Vector3f(x,y,z);
	}

}
