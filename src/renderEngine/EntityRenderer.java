package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class EntityRenderer {


	private StaticShader shader;
	
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		// This method renders as many instances of each of the available 3D models as found in the list.
		// The map associates each 3D model with its list of entity instances
		// So, for each 3D model, we load the model (VAO/VBO) and for each of its instances, we load the
		// transformation matrix and render.
		// When done, the VAO/VBO gets unbind.
		for(TexturedModel model:entities.keySet()) {
			prepareTexturedModel(model);
			int vertexCount = model.getRawModel().getVertexCount();
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch) {
				// Render the entity
				prepareEntityInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	

	private void prepareTexturedModel(TexturedModel model) {
		// Bind VAO and contained VBOs
		GL30.glBindVertexArray(model.getRawModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Manage texture material properties (specular light) and enable texturing
		ModelTexture texture = model.getTexture();
		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		// Manage texture atlas (the texture file contains several textures for the same model)
		shader.loadNumberOfRows(texture.getNumberOfRows());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
		
		// Specular maps
		shader.loadUsesSpecularMap(texture.hasSpecularMap());
		if(texture.hasSpecularMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap());
		}
	}
	
	
	private void prepareEntityInstance(Entity entity) {
		// Send to shader the entity transformation matrix 
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(),
				entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		// Send to shader the texture coordinates to use (handling of texture atlas)
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	
	
	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		// Unbind VBOs and then the VAO
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	

	
}