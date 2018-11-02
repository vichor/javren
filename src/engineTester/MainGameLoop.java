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

import java.io.File;
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

import engineTester.WorldTimeManager.WorldClock;
import engineTester.gameEntities.Barrel;
import engineTester.gameEntities.Fern;
import engineTester.gameEntities.FirTree;
import engineTester.gameEntities.Flashlight;
import engineTester.gameEntities.Flower;
import engineTester.gameEntities.GameEntity;
import engineTester.gameEntities.Grass;
import engineTester.gameEntities.Lamp;
import engineTester.gameEntities.Rocks;
import engineTester.gameEntities.Sun;
import engineTester.gameEntities.Tree;
import engineTester.gameParticles.ParticleSource;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiTexture;
import models.TexturedModel;
import objconverter.OBJLoader;
import particles.ComplexParticleSystem;
import particles.ParticleMaster;
import particles.ParticleTexture;
import particles.SimpleParticleSystem;
import platform.Library;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.GeneratedTerrain;
import terrains.HeightmapTerrain;
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
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		
		
		// FONT SYSTEM
		TextMaster.init(loader);
		FontType font = new FontType(loader.loadFontTextureAtlas("fonts/candara"), new File("res/fonts/candara.fnt"));
		GUIText text = new GUIText("This is a test text!", 1, font, new Vector2f(0,0.75f), 1f, true);
		text.setColor(1.0f, 0.0f, 0.0f);
		
		
		// TERRAIN
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrains/grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrains/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrains/pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrains/path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("maps/blendMap"));
        
		Terrain terrain = new GeneratedTerrain(0, 0, loader, texturePack, blendMap); 
		//Terrain terrain = new HeightmapTerrain(0, -1, loader, texturePack, blendMap, "maps/heightMap3");//free_mountain_lake_heightmap");
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		

        // PLAYER
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("players/person", loader), //players/person", loader), 
        		new ModelTexture(loader.loadTexture("players/playerTexture"))); //players/playerTexture")));
        Player player = new Player(playerModel, new Vector3f(75, terrain.getHeightOfTerrain(75, -50), -50), 0, 0, 0, 0.6f);
        //Player player = new Player(playerModel, new Vector3f(348, terrain.getHeightOfTerrain(348, -380), -380), 0, 120, 0, 0.6f);
        //Player player = new Player(playerModel, new Vector3f(0, terrain.getHeightOfTerrain(0, 0), 0), 0, 0, 0, 0.6f);
        
        

		// ENTITIES DEFINITION
		
		// Entities containers and tools
        List<GameEntity> gameEntities = new ArrayList<GameEntity>();
        List<GameEntity> gameNormalMappedEntities = new ArrayList<GameEntity>();
		Random random = new Random();
		
		// Game entities instances
		GameEntity.setLoader(loader);
        for(int i=0;i<500;i++){
        	//if (i%30 == 0) {
        	//	Vector3f position = getNewPosition(random, 800, terrain);
            //    gameEntities.add(new Dragon(position));
        	//}
        	if (i%3  == 0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new FirTree(position));
        	}
            if (i%4  == 0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Tree(position));
            }
            if (i%2==0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Grass(position));
            }
            if (i%3==0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Flower(position));
            }
            if(i%3==0) {
        		Vector3f position = getNewPosition(random, 800, terrain);
                gameEntities.add(new Fern(position));
            }
        }
        //gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -320),-320)));
        //gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -330),-330)));
        //gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -340),-340)));
        //gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -350),-350)));
        //gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -360),-360)));
        //gameEntities.add(new FirTree(new Vector3f(205,terrain.getHeightOfTerrain(205, -370),-370)));
        
        Rocks rocks = new Rocks();
        gameEntities.add(rocks);
        
        // Normal mapped entities
        //GameEntity barrel = new Barrel(new Vector3f(player.getPosition().x, 30, player.getPosition().z));
        GameEntity barrel = new Barrel(new Vector3f(378, 30, -300));
        GameEntity standardBarrel = new Barrel(new Vector3f(378-40, 30, -300));
        //GameEntity standardBarrel = new Barrel(new Vector3f(player.getPosition().x-40, 30, player.getPosition().z));
        gameNormalMappedEntities.add(barrel);
        gameEntities.add(standardBarrel);
        
        // Lamps
		Lamp lamp1 = new Lamp(new Vector3f(120, terrain.getHeightOfTerrain(120,  -70), -70), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f));
		Lamp lamp2 = new Lamp(new Vector3f(170, terrain.getHeightOfTerrain(170, -90), -90),  new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f));
		Lamp lamp3 = new Lamp(new Vector3f(200, terrain.getHeightOfTerrain(200, -120), -120), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f));
		gameEntities.add(lamp1);
		gameEntities.add(lamp2);
		gameEntities.add(lamp3);
		
		// Player flashlight
        Flashlight flashlight = new Flashlight(player, new Vector3f(2.0f, 2.0f, 0.0f), new Vector3f(1.5f, 0.01f, 0.0012f));
        gameEntities.add(flashlight);
        GameEntity lightbarrel = new Barrel(flashlight.getLightSource().getPosition());
        lightbarrel.getRenderEntity().setScale(0.01f);
        gameEntities.add(lightbarrel);
        
        
        // CAMERA
        Camera camera = new Camera(player);
		
		// ENVIRONMENT / LIGHTS
		Sun sun = new Sun();
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun);
		lights.add(lamp1.getLightSource());
		lights.add(lamp2.getLightSource());
		lights.add(lamp3.getLightSource());
		lights.add(flashlight.getLightSource());
		
		
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
		
		
		// PARTICLE SOURCES

		// Particle textures
		ParticleTexture starParticleTexture = new ParticleTexture(loader.loadTexture("particles/particleStar"), 1);
		ParticleTexture fireParticleTexture = new ParticleTexture(loader.loadTexture("particles/fire"), 8, true);
		ParticleTexture smokeParticleTexture = new ParticleTexture(loader.loadTexture("particles/smoke"), 5); // TODO: This texture atlas is not square and thus it won't work properly
		ParticleTexture particleAtlasTexture = new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4); 

		// Define the particle systems
		// TODO:this can be a geyser entity, using smoke particle texture and appropriate particle system attributes and configuration
		ComplexParticleSystem smokeParticleSystem = new ComplexParticleSystem(smokeParticleTexture, 10, 25, 0.3f, 4, 1);
		smokeParticleSystem.randomizeRotation();
		smokeParticleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
		smokeParticleSystem.setLifeError(0.1f);
		smokeParticleSystem.setSpeedError(0.4f);
		smokeParticleSystem.setScaleError(0.8f);

		// TODO: this can be a volcano entity
		ComplexParticleSystem fireParticleSystem = new ComplexParticleSystem(fireParticleTexture, 50, 0.01f, 0.001f, 1.5f, 25);
		fireParticleSystem.randomizeRotation();
		fireParticleSystem.setDirection(new Vector3f(0, 1, 0), 0.01f);
		fireParticleSystem.setLifeError(0.1f);
		fireParticleSystem.setSpeedError(0.05f);
		fireParticleSystem.setScaleError(0.01f);

		// Create the particle sources (system+position)
		// TODO: ParticleSources to be included in geyser/volcano entities
		//ParticleSource particleSourceGeyser = new ParticleSource(smokeParticleSystem, new Vector3f(50,terrain.getHeightOfTerrain(50, -50)+5,-50));
		ParticleSource particleSourceVolcano = new ParticleSource(fireParticleSystem, new Vector3f(50,terrain.getHeightOfTerrain(50, -50)+5,-50));
		ParticleSource particleSourceOnPlayer = new ParticleSource(new SimpleParticleSystem(starParticleTexture, 50, 25, 0.3f, 4), player.getPosition());
		ParticleSource particleSourceVolcano2 = new ParticleSource(new SimpleParticleSystem(particleAtlasTexture, 500, 5.5f, 0.08f, 6.6f), new Vector3f(50,40,-25));
		
		// create the list of particle sources
		List<ParticleSource> particleSources = new ArrayList<ParticleSource>();
		//particleSources.add(particleSourceOnPlayer);
		//particleSources.add(particleSourceGeyser);
		particleSources.add(particleSourceVolcano);
		particleSources.add(particleSourceVolcano2);
		

		// PREPARE RENDER ENTITIES
		
        /* get list of render entities needed for render engine */
        List<Entity> entities = new ArrayList<Entity>();
        for (GameEntity gameEntity : gameEntities) {
        	entities.add(gameEntity.getRenderEntity());
        }
        entities.add(player);

        /* get list of normal mapped render entities */
        List<Entity> normalMappedEntities = new ArrayList<Entity>();
        for (GameEntity gameEntity : gameNormalMappedEntities) {
        	normalMappedEntities.add(gameEntity.getRenderEntity());
        }
        

        // Clip planes
        // Plane formula is Ax + By + Cz + D = 0
        //		(A,B,C) normal of the plane
        //		D is the distance from the origin
        // As we want an horizontal plane for water things
        //		(0, -1, 0) for clipping what's below the plane
        //		(0,  1, 0) for clipping what's above the plane
        //		D = height, choose a value depending on the height of the water
        // We also apply an offset to soften the effect once the water depth effect is applied.
        // do not apply so much an offset or reflection/refraction will be get in places it shouldn't
		Vector4f masterClipPlane = new Vector4f(0, -1, 0, 10000);
		Vector4f clipPlaneReflection = new Vector4f(0,  1, 0, -water.getHeight()+1f);
		Vector4f clipPlaneRefraction = new Vector4f(0, -1, 0, water.getHeight()+1f);

		// GAME TIME

		WorldClock worldClock = WorldClock.get();
		worldClock.getClock().hour=12;
		//worldClock.getClock().minute=0;
		//worldClock.getClock().second=0;

		// GAME LOOP

		while(!Display.isCloseRequested() ) {

			//worldClock.step(15);
			//System.out.print("It is " + worldClock + " [" + 100.0f*worldClock.getDayPartProgress() + "% completed] --> ");

			sun.update();
			player.move(terrain);
			flashlight.move();
			lightbarrel.setPosition(flashlight.getLightSource().getPosition());
			camera.move();
			mousePicker.update();
			
			// Particles
			particleSourceOnPlayer.setPosition(player.getPosition());
			for (ParticleSource particleSource : particleSources) {
				particleSource.step();
			}
			ParticleMaster.update(camera);

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
			renderer.renderScene(entities, normalMappedEntities, terrains, lights, camera, clipPlaneReflection);
			camera.invertPitchAndRoll();
			camera.getPosition().y += distance;

			// render refraction texture
			frameBufferObjects.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMappedEntities, terrains, lights, camera, clipPlaneRefraction);

			// unbind fbo
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);  // drivers may ignore this call so the masterClipPlane has to define a really high height
			frameBufferObjects.unbindCurrentFrameBuffer();

			// DEFAULT FRAME BUFFER RENDERING
			
			renderer.renderScene(entities, normalMappedEntities, terrains, lights, camera, masterClipPlane);
			waterRenderer.render(waters, camera, sun);
			ParticleMaster.renderParticles(camera);
			guiRenderer.render(guis);
			TextMaster.render();

			DisplayManager.updateDisplay();			
		}
		
		// closing
		frameBufferObjects.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		ParticleMaster.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	
	private static Vector3f getNewPosition(Random random, float radius, Terrain terrain) {
		boolean done = false;
		float x=0,y=0,z=0;
		while (!done) {
        	x = random.nextFloat()*radius; 
        	z = random.nextFloat()*(radius); 
        	y = terrain.getHeightOfTerrain(x, z);
        	if (y>0.05f) {
        		done = true;
        	}
		}
		return new Vector3f(x,y,z);
	}

}
