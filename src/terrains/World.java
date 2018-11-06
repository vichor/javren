package terrains;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class World {
	
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private static final int WORLDSIZEX = 8;
	private static final int WORLDSIZEY = 2;
	
	public World(Loader loader) {
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrains/grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrains/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrains/pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrains/path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("maps/blendMap"));
        
		//Terrain hTerrain = new HeightmapTerrain(0, 1, loader, texturePack, blendMap, "maps/heightMap3");//free_mountain_lake_heightmap");
        for (int i=0; i<WORLDSIZEX; i++) {
        	for (int j=0; j<WORLDSIZEY; j++) {
        		System.out.println("Generating terrain");
        		terrains.add(new GeneratedTerrain(i, j, loader, texturePack, blendMap));
        	}
        }
	}
	
	
	public static Vector2f getWorldSideSize() {
		return new Vector2f(WORLDSIZEX, WORLDSIZEY);
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
			xMax = (xMax > terrain.getX()+terrain.getSize()) ? xMax : terrain.getX()+terrain.getSize();
			zMax = (zMax > terrain.getZ()+terrain.getSize()) ? zMax : terrain.getZ()+terrain.getSize();
		}
		return new Vector2f(xMax, zMax);
	}
	
	public Vector3f getMaxHeightCoordinate() {
		Vector3f maxHeightCoordinate = new Vector3f();
		for(Terrain terrain: terrains) {
			Vector3f terrainMaxHeightCoordinate = terrain.getMaxHeightCoordinate();
			if (terrainMaxHeightCoordinate.y > maxHeightCoordinate.y) {
				maxHeightCoordinate = terrainMaxHeightCoordinate;
			}
		}
		return maxHeightCoordinate;
		
	}

	
	public float getHeight(float x, float z) {
		Terrain terrain = getTerrain(x,z);
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(x, z);
		}
		return height;
	}
	
	
	public Vector3f createPosition(float x, float z) {
		return new Vector3f(x, getHeight(x,z), z);
	}
	
}
