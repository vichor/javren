package engineTester.gameEntities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objconverter.OBJLoader;
import textures.ModelTexture;

public class Fern extends GameEntity {

	private static TexturedModel texturedModel=null;
	
	
	public Fern(Vector3f position) {
		if (texturedModel == null) {
			RawModel rawModel = OBJLoader.loadObjModel("vegetation/fern", loader);
	        // The fern model uses atlas textures
	        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("vegetation/fern"));
	        fernTextureAtlas.setNumberOfRows(2);
	        texturedModel = new TexturedModel(rawModel, fernTextureAtlas);
	        texturedModel.getTexture().setHasTransparency(true);	
		}
		// Atlas texture: use alternate entity constructor to choose which of the textures from 
		// the atlas shall be used
		Random random = new Random();
		entity = new Entity(texturedModel, random.nextInt(4), position, 0, 0, 0, 1);	
	}
}
