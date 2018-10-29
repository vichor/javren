package terrains;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;


public abstract class Terrain {

	protected static final float SIZE = 800;
	
	protected float x;
	protected float z;
	protected RawModel model;
	protected TerrainTexturePack texturePack;
	protected TerrainTexture blendMap;
	
	protected float[][] heights;
	
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, 
			TerrainTexture blendMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
	}


	protected abstract RawModel generateTerrain(Loader loader);


	public float getX() {
		return x;
	}


	public float getZ() {
		return z;
	}


	public RawModel getModel() {
		return model;
	}


	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}


	public TerrainTexture getBlendMap() {
		return blendMap;
	}


	public float getHeightOfTerrain (float worldX, float worldZ) {

		// get the coordinates with respect to the terrain (upper left terrain vertex is 0,0)
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;

		// terrain is composed of a grid of squares. Get which tile are the world coordinates placed on
		float gridSquareSize = SIZE / ((float)heights.length - 1); // the size of each grid square is the total size / the number of grid squares (== number of vertices - 1)
		int gridX = (int) Math.floor(terrainX / gridSquareSize); 
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		// check if resulting grid square coordinates are within the terrain or out of it.
		if (gridX >= heights.length-1 || gridZ >= heights.length-1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		// Where inside the grid square the world coordinates are. Get this value in [0,1] range
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		
		// Each tile is a square formed by two triangles. The diagonal of the tile is X=1-Z. X values lower than
		// 1-z are on the left triangle; x values > 1-z are on the right triangle. Then we will have X,Z coordinate,
		// and which triangle the world coordinates are placed. We know the height of each of the triangle vertices
		// (heights array). The height of the world coordinate will just be an interpolation (bary centric interpolation).
		float height = 0;
		if (xCoord < 1 - zCoord) {
			height = Maths.baryCentric( 
					new Vector3f(0,heights[gridX][gridZ], 0), 
					new Vector3f(1, heights[gridX+1][gridZ], 0), 
					new Vector3f(0, heights[gridX][gridZ+1],1), 
					new Vector2f(xCoord, zCoord));
		}else {
			height = Maths.baryCentric( 
					new Vector3f(0,heights[gridX+1][gridZ], 0), 
					new Vector3f(1, heights[gridX+1][gridZ+1], 0), 
					new Vector3f(0, heights[gridX][gridZ+1],1), 
					new Vector2f(xCoord, zCoord));
		}
		return height;
	}

	
}
