package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {

	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;  /* 3 color channels */
	
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	private float[][] heights;
	
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, 
			TerrainTexture blendMap, String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
		
	}


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


	private RawModel generateTerrain(Loader loader, String heightMap){
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/"+heightMap+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int vertexCount = image.getHeight();
	    heights = new float[vertexCount][vertexCount];
		
		int count = vertexCount * vertexCount;	// a squared terrain having vertex_count vertices per side
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)]; // two triangles per tile; each tile 6 vertices (2 triangles)
		int vertexPointer = 0;
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				heights[j][i] = getHeight(j, i, image);
				vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;	// distribute vertices along terrain side; SIZE is the size of each tile side
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<vertexCount-1;gz++){
			for(int gx=0;gx<vertexCount-1;gx++){
				int topLeft = (gz*vertexCount)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*vertexCount)+gx;
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
	
	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0; // out of bounds.
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR/2f;  // height range becomes [-MAX/2, MAX/2]
		height /= MAX_PIXEL_COLOR/2f;  // height range becomes [-1, 1]
		height *= MAX_HEIGHT;  // height range becomes [-MAX_HEIGHT, MAX_HEIGHT]
		return height;
	}
	
	
	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x-1, z, image); //left
		float heightR = getHeight(x+1, z, image); //right
		float heightD = getHeight(x, z-1, image); //down
		float heightU = getHeight(x, z+1, image); //up
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}

	
	
}
