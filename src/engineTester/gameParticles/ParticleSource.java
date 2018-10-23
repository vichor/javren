package engineTester.gameParticles;

import org.lwjgl.util.vector.Vector3f;

import particles.ParticleSystem;

public class ParticleSource {

	private ParticleSystem system;
	private Vector3f position;
	
	public ParticleSource (ParticleSystem system, Vector3f position) {
		this.system = system;
		this.position = position;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void step() {
		system.generateParticles(position);
	}
}
