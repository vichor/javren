package particles;

public class ParticleTexture {
	
	private int textureId;
	private int numberOfRows;
	private boolean additiveAlphaBlending;
	
	
	public ParticleTexture(int textureId, int numberOfRows, boolean alpha) {
		this.textureId = textureId;
		this.numberOfRows = numberOfRows;
		this.additiveAlphaBlending = alpha;
	}
	public ParticleTexture(int textureId, int numberOfRows) {
		this.textureId = textureId;
		this.numberOfRows = numberOfRows;
		this.additiveAlphaBlending = false;
	}


	public int getTextureId() {
		return textureId;
	}


	public int getNumberOfRows() {
		return numberOfRows;
	}
	
	
	public boolean usesAdditiveAlphaBlending() {
		return additiveAlphaBlending;
	}
	
	
}
