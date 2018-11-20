package objconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;

public class OBJLoader {

	public static RawModel loadObjModel(String fileName, Loader loader) {

		InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream("/res/"+fileName+".obj"));
		BufferedReader reader = new BufferedReader(in);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals  = new ArrayList<Vector3f>();
		List<Integer>  indices  = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;

		try {
			// read sections v, vt, fn into vectors/arrays
			while(true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				}else if(line.startsWith("vt ")) {
					Vector2f vertex = new Vector2f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]));
					textures.add(vertex);
				}else if(line.startsWith("vn ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					normals.add(vertex);
				}else if(line.startsWith("f ")) {
					textureArray = new float[vertices.size()*2];
					normalsArray = new float[vertices.size()*3];
					// once section f is reached, all other sections are done
					break;
				}
			}
				
			// manage f section: match vertex data with texture and normals data
			while(line!=null) {
				if(!line.startsWith("f ")){
					line = reader.readLine();
					continue;
				}
				
				// find vertex data information references 
				String[] currentLine = line.split(" ");
				String[] vertex1_inforef = currentLine[1].split("/");
				String[] vertex2_inforef = currentLine[2].split("/");
				String[] vertex3_inforef = currentLine[3].split("/");
				
				// find texture and normals data and add it to arrays
				processVertex(vertex1_inforef, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex2_inforef, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex3_inforef, indices, textures, normals, textureArray, normalsArray);

				line = reader.readLine();
			}
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// Set vertices and indices data in array format
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		int vertexPointer = 0;
		for(Vector3f vertex:vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		for(int i=0; i<indices.size();i++) {
			indicesArray[i] = indices.get(i);
		}

		// ... and create the raw model from data extracted from obj file
		return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);

	}	
		

	private static void processVertex(String[] vertexDataInfoRef, List<Integer> indices, 
			List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, 
			float[] normalsArray) {

		int currentVertexPointer = Integer.parseInt(vertexDataInfoRef[0]) - 1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexDataInfoRef[1])-1);
		textureArray[currentVertexPointer*2] = currentTex.x;
		textureArray[currentVertexPointer*2+1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexDataInfoRef[2])-1);
		normalsArray[currentVertexPointer*3] = currentNorm.x;
		normalsArray[currentVertexPointer*3+1] = currentNorm.y;
		normalsArray[currentVertexPointer*3+2] = currentNorm.z;
	}

}
	
	
