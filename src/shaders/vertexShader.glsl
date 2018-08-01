#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;

void main(void) {

	// Positioning
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	gl_Position = projectionMatrix * viewMatrix * worldPosition;

	// Texturing
	pass_textureCoords = textureCoords;

	// LIGHTING
	// Applying light affects color of the fragments and thus is calculated
	// by the fragement shader. The calculations, though, need vectors
	// extracted from vertex, lights and camera positions and these vectors
	// are calculated here, at the vertex shader.

	// Difuse lighting
	// We need the surface normal vector which is obtained from the vertex
	// normal vector applying to it the transformation
	// Also needed a vector pointing to the light source and this is
	// obtained subtracting the vertex world position from the light
	// source position.

	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	toLightVector = lightPosition - worldPosition.xyz;


	// Specular lighting
	// We need a vector towards the camera and this means we need the camera
	// position, but this information is not available.The ViewMatrix contains
	// the inverse of the camera position, so we just invert it and multiply
	// by (0,0,0,1) to extract the camera position from it. Then it's just
	// Subtracting from it the vertex position in the world.

	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;


}
