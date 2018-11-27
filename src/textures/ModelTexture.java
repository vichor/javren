package textures;

public class ModelTexture {
	
	private int textureID;
	private int normalMap;
	private int specularMap;

	private float reflectivity = 0;
	private float shineDamper = 1;
	
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean useSpecularMap = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}
	
	
	public void setSpecularMap(int sm) {
		specularMap = sm;
		useSpecularMap = true;
	}
	
	
	public boolean hasSpecularMap() {
		return useSpecularMap;
	}
	
	
	public int getSpecularMap() {
		return specularMap;
	}
	

	public int getNumberOfRows() {
		return numberOfRows;
	}


	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	
	public int getNormalMap() {
		return normalMap;
	}


	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}


	public int getID() {
		return this.textureID;
	}


	public float getReflectivity() {
		return reflectivity;
	}


	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}


	public float getShineDamper() {
		return shineDamper;
	}


	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}


	public boolean isHasTransparency() {
		return hasTransparency;
	}


	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}


	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}


	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
	
	
}
