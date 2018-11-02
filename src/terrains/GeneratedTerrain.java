package terrains;


import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class GeneratedTerrain extends Terrain {

	private static final float SIZE = 800;
	private static final int SEED = new Random().nextInt(1000000000);
	private static final int VERTEX_COUNT = 256;

	private HeightsGenerator generator;
	
	public GeneratedTerrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, 
			TerrainTexture blendMap) {
		super(gridX, gridZ, loader, texturePack, blendMap);
		generator = new HeightsGenerator(gridX, gridZ, VERTEX_COUNT, SEED);
		this.model = generateTerrain(loader);
	}


	protected RawModel generateTerrain(Loader loader){
		
	    heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		int count = VERTEX_COUNT * VERTEX_COUNT;	// a squared terrain having vertex_count vertices per side
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)]; // two triangles per tile; each tile 6 vertices (2 triangles)
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				heights[j][i] = getHeight(j, i, generator);
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;	// distribute vertices along terrain side; SIZE is the size of each tile side
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, generator);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
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
	
	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}
	
	
	private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
		float heightL = getHeight(x-1, z, generator); //left
		float heightR = getHeight(x+1, z, generator); //right
		float heightD = getHeight(x, z-1, generator); //down
		float heightU = getHeight(x, z+1, generator); //up
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}
	
}
