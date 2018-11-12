#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[5];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[5];
uniform vec4 plane;
uniform mat4 toShadowMapSpace;

const float density = 0.00;//35;
const float gradient = 5.0;
const float SHADOWDISTANCE = 150.0; // better uniform as it has to match the shadow distance in shadowbox class
const float TRANSITIONDISTANCE = 10.0;

void main(void) {

	// Positioning
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	// Shadow calculations
	// Application program calculates the matrix to transform into shadow
	// map space and we just need to multiply it to the position
	shadowCoords = toShadowMapSpace * worldPosition;

	// Clip plane management
	gl_ClipDistance[0] = dot(worldPosition, plane);

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
	// source position. As we have 5 lights, we will need to do these
	// calculations for all of them.

	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	for (int i=0; i<5; i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}


	// Specular lighting
	// We need a vector towards the camera and this means we need the camera
	// position, but this information is not available.The ViewMatrix contains
	// the inverse of the camera position, so we just invert it and multiply
	// by (0,0,0,1) to extract the camera position from it. Then it's just
	// Subtracting from it the vertex position in the world.

	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;


	// FOG
	// Fog increases following an exponential formula depending on a density
	// and a gradient. These two parameters simulates how thick the fog is.
	float distance = length(positionRelativeToCamera.xyz); // distance of this vertex from the camera
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);

	// Fog effect on shadows as well to prevent magically appearing/disappearing of shadows
	// depending on shadow box dimensions
	float transitionPeriodDistance = distance - (SHADOWDISTANCE - TRANSITIONDISTANCE); // how far the vertex is in the transition period
	transitionPeriodDistance = transitionPeriodDistance / TRANSITIONDISTANCE; // normalize: 0 will be at the start of the transition period; 1 will be at the end
	shadowCoords.w = clamp(1.0-transitionPeriodDistance, 0.0, 1.0); // store the transition period progress on w component of the shadow coords
}
