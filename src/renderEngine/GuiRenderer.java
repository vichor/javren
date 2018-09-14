package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import guis.GuiShader;
import guis.GuiTexture;
import models.RawModel;
import toolbox.Maths;

public class GuiRenderer {
	
	// For GUIs, there's no need to apply all the 3D techniques we are applying to 
	// entities or terrains (no zooming, no camera, no lighting, etc). We are just
	// rendering into quads and then applying to the quad the texture of the GUI
	// element to be rendered. Thus, we won't be needing to use vertex indices, 
	// normals or texture coordinates (as they will be 0,0/0,1/1,0/1,1).
	// Rendering quads were done on session 2 and we will be following similar
	// approach to generate the quad model.
	//
	// The rendering itself will be done using Triangle Strips which instead of
	// defining all the triangles of the model, it's just specified the first one
	// and then, each additional vertex define is creating a concatenated triangle.
	// The positions array for a quad is then just 4 elements long (instead of 6).
	
	private final RawModel quad;
	private GuiShader shader;
	
	public GuiRenderer(Loader loader) {
		float[] positions = { -1,1 , -1,-1, 1,1, 1,-1 };
		quad = loader.loadToVAO(positions, 2);		
		shader = new GuiShader();
	}
	
	public void render(List<GuiTexture> guis){
		shader.start();
		// Bind VAO
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		// enable GUI transparency (alpha blending, different from entities transparency) and disable the depth test for GUIs
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,  GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//render
		for(GuiTexture gui : guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		// Restore render system settings
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		// Unbind VAO
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	
	public void cleanUp() {
		shader.cleanUp();
	}

}
