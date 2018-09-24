#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4]; // Vector pointing to light sources has to be an array if we have multiple ligths affecting our scene
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4]; // light position has to be an array if we have multiple ligths affecting our scene
uniform float useFakeLighting;
uniform float numberOfRows;
uniform vec2 offset;
uniform vec4 plane;

const float density = 0.00;//35;
const float gradient = 5.0;


void main(void) {


	// Positioning
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	// Clip plane management
	gl_ClipDistance[0] = dot(worldPosition, plane);

	// Texturing extracted from the texture atlas
	pass_textureCoords = (textureCoords / numberOfRows) + offset;

	// LIGHTING
	// Applying light affects color of the fragments and thus is calculated
	// by the fragment shader. The calculations, though, need vectors
	// extracted from vertex, lights and camera positions and these vectors
	// are calculated here, at the vertex shader.

	// Fake Lighting
	// Vegetation may be modeled using two intersected quads. The normals of
	// these quads point horizontally with respect the terrain and one quad
	// differ 90 degrees respect the other. This causes an unreal effect on
	// the leaves of the vegetation, causing one side to be lighted and the
	// other being in the shadows. This can be fixed by faking the normals of
	// the surface so that all of them point in the up direction when needed.
	// This effect can be also used in other situations. For instance, a lamp
	// has a light source positioned inside the bulb. The glass of the lamp
	// is expected to be seen as the color of the light source, but it's not
	// the case because the visible face are not receiving light. Using
	// fake lighting will modify the normals so that the faces of the lamp face
	// up and thus receive light.

	vec3 actualNormal = normal;
	if (useFakeLighting > 0.5){
		actualNormal = vec3(0.0, 1.0, 0.0);
	}

	// Difuse lighting
	// We need the surface normal vector which is obtained from the vertex
	// normal vector applying to it the transformation
	// Also needed the vectors pointing to the light sources and this is
	// obtained subtracting the vertex world position from the light
	// source positions.

	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	for (int i = 0; i < 4; i++){
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
