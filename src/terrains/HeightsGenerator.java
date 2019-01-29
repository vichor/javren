package terrains;

import java.util.Random;

public class HeightsGenerator {
	
	// Perlin noise as in http://freespace.virgin.net/hugo.elias/models/m_perlin.htm
	// See also https://en.wikipedia.org/wiki/Perlin_noise for canonical definition of Perlin noise and implementation
	
	private static final float AMPLITUDE = 1200f; // how big the terrain can be
	private static final int OCTAVES = 6;	// smoother noise factors to use
	private static final float ROUGHNESS = 0.348f;
	private static final float OFFSET = 0.0f;//55f; // terrain height offset in percentage. Heights will go in range [(AMPLITUDE-OFFSET%), (AMPLITUDE+OFFSET%)]
	                                           // for current values, amplitude will go from [(70-2.1),(70+2.1)]
	
	private Random random = new Random();
	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;
	
	
	public HeightsGenerator() {
		this.seed = random.nextInt(1000000000);
	}
	
	public HeightsGenerator(int gridX, int gridZ, int vertexCount, int seed) {
		this.seed = seed;
		xOffset = gridX * (vertexCount-1);
		zOffset = gridZ * (vertexCount-1);
	}
	
	
	public float generateHeight(int x, int z) {
		float total = 0;
		float d = (float)Math.pow(2,  OCTAVES-1);
		for(int i=0; i<OCTAVES; i++){
			float freq = (float)(Math.pow(2,  i)/d);
			float amp = (float)Math.pow(ROUGHNESS, i)*AMPLITUDE;
			total += getInterpolatedNoise((x+xOffset)*freq, (z+zOffset)*freq) * amp;
		}
		return total;
	}
	
	
	private float getSmoothNoise(int x, int z) {
		// neighbor vertices
		float corners = (getNoise(x-1, z-1) + getNoise(x+1, z-1) + getNoise(x-1, z+1) + getNoise(x+1, z+1)) / 16f;
		float sides = (getNoise(x-1, z) + getNoise(x+1, z) + getNoise(x, z-1) + getNoise(x, z+1)) / 8f;
		// vertex
		float center = getNoise(x, z) / 4f;
		// result
		return corners + sides + center;

	}

	private float getNoise(int x, int z) {
		// to force getting always the same number for an (x,z) pair. Each time the game executes, 
		// values can be different, but it must be consistent during a game execution.
		// besides, random generator yields very similar values for consecutive inputs, so we will
		// have a multiplier to compensate this.
		random.setSeed( (x*49632) + (z*325176) + seed); 
		return ((random.nextFloat()+OFFSET) *2f - 1f); // between -1 and 1
		
	}	
	
	
	private float getInterpolatedNoise(float x, float z) {
		int intX = (int)x;
		int intZ = (int)z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX+1, intZ);
		float v3 = getSmoothNoise(intX, intZ+1);
		float v4 = getSmoothNoise(intX+1, intZ+1);
		
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
		
		
	}
	
	private float interpolate(float a, float b, float blend) {
		// cosine (as in cos(alpha) ) interpolation function
		double theta = blend * Math.PI;
		float factor = (float)(1f - Math.cos(theta)) * 0.5f;
		return a*(1f - factor) + b*factor;
	}

}
