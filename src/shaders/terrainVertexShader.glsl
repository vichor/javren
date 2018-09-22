#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];

const float density = 0.00;//35;
const float gradient = 5.0;

void main(void) {

	// Positioning
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	// Texturing & tiling
	// To use blend maps, the texture coordinate to pass to the fragment
	// shader is directly extracted from the texture. Tiling will be managed
	// lately by the fragment shader.
	pass_textureCoords = textureCoords;

	// LIGHTING
	// Applying light affects color of the fragments and thus is calculated
	// by the fragment shader. The calculations, though, need vectors
	// extracted from vertex, lights and camera positions and these vectors
	// are calculated here, at the vertex shader.

	// Diffuse lighting
	// We need the surface normal vector which is obtained from the vertex
	// normal vector applying to it the transformation
	// Also needed a vector pointing to the light source and this is
	// obtained subtracting the vertex world position from the light
	// source position. As we have 4 lights, we will need to do these
	// calculations for all of them.

	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	for (int i=0; i<4; i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}


	// Specular lighting
	// We need a vector towards the camera and this means we need the camera
	// position, but this information is not available.The ViewMatrix contains
	// the inverse of the camera position, so we just invert it and multiply
	// by (0,0,0,1) to extract the camera position from it. Then it's just
	// Subtracting from it the vertex position in the world.

	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;


	// Fog
	// Fog increases following an exponential formula depending on a density
	// and a gradient. These two parameters simulates how thick the fog is.
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
