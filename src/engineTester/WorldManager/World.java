package engineTester.WorldManager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import terrains.GeneratedTerrain;
import terrains.HeightmapTerrain;
import renderEngine.Loader;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class World {
	
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	public World(Loader loader) {
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrains/grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrains/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrains/pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrains/path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("maps/blendMap"));
        
		Terrain gTerrain = new GeneratedTerrain(0, 0, loader, texturePack, blendMap); 
		Terrain hTerrain = new HeightmapTerrain(0, 1, loader, texturePack, blendMap, "maps/heightMap3");//free_mountain_lake_heightmap");
		
		terrains.add(gTerrain);
		terrains.add(hTerrain);
	}
	

	public Terrain getTerrain(float x, float z) {
		Terrain foundTerrain = null;
		for (Terrain terrain: terrains) {
			float terrainX = terrain.getX();
			float terrainZ = terrain.getZ();
			if ( (x >= terrainX) && (z >= terrainZ) &&
				 (x < terrainX+terrain.getSize()) && (z < terrainZ+terrain.getSize()) ) {
				foundTerrain = terrain;
				break;
			}
		}
		return foundTerrain;
	}
	
	
	public List<Terrain> getTerrains() {
		return terrains;
	}
	
	
	public Vector2f getMinCoordinates() {
		float xMin = 0;
		float zMin = 0;
		for(Terrain terrain: terrains) {
			xMin = (xMin < terrain.getX()) ? xMin : terrain.getX();
			zMin = (zMin < terrain.getZ()) ? zMin : terrain.getZ();
		}
		return new Vector2f(xMin, zMin);
	}
	
	
	public Vector2f getMaxCoordinates() {
		float xMax = 0;
		float zMax = 0;
		for(Terrain terrain: terrains) {
			xMax = (xMax > terrain.getX()) ? xMax : terrain.getX();
			zMax = (zMax > terrain.getZ()) ? zMax : terrain.getZ();
		}
		return new Vector2f(xMax, zMax);
	}

	
	public float getHeight(float x, float z) {
		return getTerrain(x,z).getHeightOfTerrain(x, z);
	}
	
	
	public Vector3f createPosition(float x, float z) {
		return new Vector3f(x, getHeight(x,z), z);
	}

}
