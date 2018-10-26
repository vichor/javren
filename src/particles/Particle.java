package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import renderEngine.DisplayManager;

public class Particle {
	
	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private float elapsedTime = 0;

	private ParticleTexture texture;
	
	private Vector2f texOffset1 = new Vector2f(); // (x,y) textures coordinates in the texture atlas for current state
	private Vector2f texOffset2 = new Vector2f(); // (x,y) textures coordinates in the texture atlas for next state
	private float blend;
	
	private float distance; // distance from the camera
	
	private Vector3f change = new Vector3f();  // to avoid creating a vector3f in each instance update, define one at particle object level and reuse it. See update method
	
	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation,
			float scale) {
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		ParticleMaster.addParticle(this);
	}


	public ParticleTexture getTexture() {
		return texture;
	}
	
	
	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public float getDistance() {
		return distance;
	}
	
	protected boolean update(Camera camera) {
		velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		change.set(velocity);  // velocity change. Object is Vector3f but reused from call to call to update to save CPU time on creating the object.
		change.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(change,  position, position);
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared(); // we use the squared length for greater resolution on distance (more efficient)
		updateTextureCoordInfo();
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		return elapsedTime < lifeLength;
	}


	public Vector2f getTexOffset1() {
		return texOffset1;
	}


	public Vector2f getTexOffset2() {
		return texOffset2;
	}
	
	
	public float getBlend() {
		return blend;
	}


	private void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;       // the int part of this float will be the index of current texture in the atlas; the decimal part will be the progress so far to reach the next texture (and will be used for the blend factor between current texture and next texture)
		int index1 = (int) Math.floor(atlasProgression);		// current texture index in atlas
		int index2 = (index1 < stageCount -1) ? index1 + 1 : index1;   // next texture index in atlas; do not allow to go beyond the max
		blend = atlasProgression % 1; // see comment in atlasProgression, 3 lines above
		setTextureOffset(texOffset1, index1); // calculate the texture atlas offset coordinates for current texture image
		setTextureOffset(texOffset2, index2); // calculate the texture atlas offset coordinates for next texture image
	}
	
	
	private void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}


}
