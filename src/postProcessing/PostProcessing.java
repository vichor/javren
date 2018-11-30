package postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import postProcessing.bloom.BrightFilter;
import postProcessing.bloom.CombineFilter;
import postProcessing.gaussianBlur.HorizontalBlur;
import postProcessing.gaussianBlur.VerticalBlur;
import models.RawModel;
import renderEngine.Loader;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static CombineFilter combine;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		brightFilter = new BrightFilter(Display.getWidth()/2, Display.getHeight()/2);
		hBlur = new HorizontalBlur(Display.getWidth()/5, Display.getHeight()/5);
		vBlur = new VerticalBlur(Display.getWidth()/5, Display.getHeight()/5);
		combine = new CombineFilter();
	}
	
	public static void doPostProcessing(int colorTexture, int brightTexture){
		startPostProcess();
		hBlur.render(brightTexture);
		vBlur.render(hBlur.getOutputTexture());
		combine.render(colorTexture, vBlur.getOutputTexture());
		endPostProcess();
	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		brightFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		combine.cleanUp();
	}
	
	private static void startPostProcess(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void endPostProcess(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
