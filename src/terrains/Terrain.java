package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class Terrain {

	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MIN_HEIGHT = -40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;  /* 3 color channels */
	
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	// WARNING:
	// make sure that your camera is in a position that allows you to see the 
	// terrain! make sure the camera's y position is above 0, and make sure 
	// that the terrain is in front of the camera (either move the camera back,
	// rotate the camera around, or choose negative gridx & gridz values when 
	// calling the terrain constructor).
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
		
		
		int count = vertexCount * vertexCount;	// a squared terrain having vertex_count vertices per side
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)]; // two triangles per tile; each tile 6 vertices (2 triangles)
		int vertexPointer = 0;
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;	// distribute vertices along terrain side; SIZE is the size of each tile side
				vertices[vertexPointer*3+1] = getHeight(j, i, image);
				vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
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
	
	
	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x > image.getHeight() || z < 0 || z > image.getHeight()) {
			return 0; // out of bounds.
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR/2f;  // height range becomes [-MAX/2, MAX/2]
		height /= MAX_PIXEL_COLOR/2f;  // height range becomes [-1, 1]
		height *= MAX_HEIGHT;  // height range becomes [-MAX_HEIGHT, MAX_HEIGHT]
		return height;
	}

	
	
}
