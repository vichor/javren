#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor;

void main(void) {

	// Difuse Light:
	// Calculate how much light is receiving the fragment by doing the dot 
	// product of the normalized surface normal vector with the normalized
	// vector pointing from the surface to the light

	float nDotProd = dot(normalize(surfaceNormal), normalize(toLightVector));
	float brightness = max(nDotProd, 0.0);	// assure light is above 0.0
	vec3 diffuseLight = brightness * lightColor;
	
	// Fragment color:
	// Calculate the final color of the fragment mixing up the texture
	// data with the light calculated before

	out_Color = vec4(diffuseLight, 1.0) * texture(textureSampler, pass_textureCoords);

}