package particles;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.RawModel;
import renderEngine.Loader;
import toolbox.Maths;

public class ParticleRenderer {
	
	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	private static final int MAX_INSTANCES = 50000;
	private static final int INSTANCE_DATA_LENGTH = 21; // amount of data(floats) for each particle: 16 for modelViewMatrix, 4 for texture information, 1 for blend factor
	
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES*INSTANCE_DATA_LENGTH);
	
	
	private RawModel quad;
	private ParticleShader shader;

	private Loader loader;
	private int vbo;
	private int pointer = 0;
	
	
	protected ParticleRenderer(Loader loader, Matrix4f projectionMatrix){
		this.loader = loader;
		this.vbo = loader.createEmptyVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		quad = loader.loadToVAO(VERTICES, 2);
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);    //  modelViewMatrix column 1
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);    //  modelViewMatrix column 2
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);    //  modelViewMatrix column 3
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);   //  modelViewMatrix column 4
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);   // texture offsets
		loader.addInstancedAttribute(quad.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);   // blend factor
		shader = new ParticleShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();

	}
	
	protected void render(Map<ParticleTexture, List<Particle>> particles, Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		prepare();
		for (ParticleTexture texture : particles.keySet()) {
			// configure alpha blending, bind textures and load uniforms 
			bindTexture(texture);
			// prepare indexed data through vbos
			List<Particle> particleList = particles.get(texture);
			pointer = 0;
			float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];
			for (Particle particle : particles.get(texture)) {
				updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), viewMatrix, vboData);
				updateTexCoordInfo(particle, vboData);
			}
			// load indexed data
			loader.updateVbo(vbo, vboData, buffer);
			//draw
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particleList.size());

		}
		finishRendering();
	}

	protected void cleanUp(){
		shader.cleanUp();
	}
	
	private void updateTexCoordInfo(Particle particle, float[] data) {
		data[pointer++] = particle.getTexOffset1().x;
		data[pointer++] = particle.getTexOffset1().y;
		data[pointer++] = particle.getTexOffset2().x;
		data[pointer++] = particle.getTexOffset2().y;
		data[pointer++] = particle.getBlend();
	}
	
	private void bindTexture(ParticleTexture texture) {
		// Configure alpha blending
		if (texture.usesAdditiveAlphaBlending()) {
			// use additive alpha blending: to get a pixel color, just add all the colors that pixel is receiving 
			// instead of putting an entity in front of another. This is good for fireworks, fire and similar effects, 
			// but not so good for smoke.
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); 
		}else {
			// use standard alpha blending
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); 
		}
		// bind particle texture 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
		// Uniform loading
		shader.loadNumberOfRows(texture.getNumberOfRows());
	}
	
	private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix, float[] vboData) {
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		// We want no rotation to be applied. This means that the resulting 4x4 matrix shall have (1,0,0)|(0,1,0)|(0,0,1) 
		// as the 3x3 submatrix starting in position (0,0) of the 4x4 matrix. To do so, we have to multiply by the transposed
		// submatrix. 
		//  +-          -+     +-          -+    +-          -+     
		//  | a  b  c  m |     | a  d  g  m |    | 1  0  0  n |
		//  | d  e  f  m |     | b  e  h  m |    | 0  1  0  n |
		//  | g  h  i  m |  *  | c  f  i  m | =  | 0  0  1  n |
		//  | m  m  m  m |     | m  m  m  m |    | n  n  n  n |
		//  +-          -+     +-          -+    +-          -+     
		// We create below this transposed submatrix:
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		
		Matrix4f.rotate((float) Math.toRadians(rotation),  new Vector3f(0,0,1), modelMatrix, modelMatrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
		storeMatrixData(modelViewMatrix, vboData);
		
	}
	
	private void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00;
		vboData[pointer++] = matrix.m01;
		vboData[pointer++] = matrix.m02;
		vboData[pointer++] = matrix.m03;
		vboData[pointer++] = matrix.m10;
		vboData[pointer++] = matrix.m11;
		vboData[pointer++] = matrix.m12;
		vboData[pointer++] = matrix.m13;
		vboData[pointer++] = matrix.m20;
		vboData[pointer++] = matrix.m21;
		vboData[pointer++] = matrix.m22;
		vboData[pointer++] = matrix.m23;
		vboData[pointer++] = matrix.m30;
		vboData[pointer++] = matrix.m31;
		vboData[pointer++] = matrix.m32;
		vboData[pointer++] = matrix.m33;
	}
	
	private void prepare(){
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);   // model vertex
		GL20.glEnableVertexAttribArray(1);   // modelViewMatrix column 1
		GL20.glEnableVertexAttribArray(2);   // modelViewMatrix column 2
		GL20.glEnableVertexAttribArray(3);   // modelViewMatrix column 3
		GL20.glEnableVertexAttribArray(4);   // modelViewMatrix column 4
		GL20.glEnableVertexAttribArray(5);   // texture information (offset1 and 2)
		GL20.glEnableVertexAttribArray(6);   // blend factor
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
	}
	
	private void finishRendering(){
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
		shader.stop();
		

	}

}
