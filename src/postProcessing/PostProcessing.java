package postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import postProcessing.gaussianBlur.HorizontalBlur;
import postProcessing.gaussianBlur.VerticalBlur;
import models.RawModel;
import renderEngine.Loader;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	// two stages of blurring... the first one will use a smaller image (for performance reasons) 
	// an even smaller image. Using 2 stages is fancy for creating huge blurring without 
	// modifying the blur class and shaders and not compromisong performance by making them use
	// lots of pixels.
	private static HorizontalBlur hBlur1;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur1;
	private static VerticalBlur vBlur2;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		hBlur1 = new HorizontalBlur(Display.getWidth()/8, Display.getHeight()/8);
		hBlur2 = new HorizontalBlur(Display.getWidth()/2, Display.getHeight()/2);
		vBlur1 = new VerticalBlur(Display.getWidth()/8, Display.getHeight()/8);
		vBlur2 = new VerticalBlur(Display.getWidth()/2, Display.getHeight()/2);
	}
	
	public static void doPostProcessing(int colorTexture){
		start();
		hBlur2.render(colorTexture);
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur1.render(vBlur2.getOutputTexture());
		vBlur1.render(hBlur1.getOutputTexture());
		contrastChanger.render(vBlur1.getOutputTexture());
		end();
	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		hBlur1.cleanUp();
		vBlur1.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
