#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

void main(void) {

	// Difuse Light:
	// Calculate how much light is receiving the fragment by doing the dot 
	// product of the normalized surface normal vector with the normalized
	// vector pointing from the surface to the light

	vec3 unitSurfaceNormal  = normalize(surfaceNormal);
	vec3 unitVectorToLight  = normalize(toLightVector);
	float nDotProd = dot(unitSurfaceNormal, unitVectorToLight);
	float brightness = max(nDotProd, 1);	// Ambient light made by ensuring difuse light is above 0.2
	vec3 diffuseLight = brightness * lightColor;
	
	
	// Specular Light:
	// this light is the reflected light from a fragment towards the camera.
	// Depending where the camera is located and where the fragment is pointing
	// to, the camera will receive more or less specular light.
	// It is calculated with the dot product of the fragment-to-camera vector 
	// and the reflected-light-vector.
	// - The fragment-to-camera vector is an input from the vertex shader
	// - The reflected light for the fragment is obtained using the GLSL reflect 
	// function using the light direction and the surface normal.
	// 		The light direction, or the vector poiting from light to fragment, 
	// 		is the negative of the vector pointing to the light source.
	// Then the material damping has to be applied by powering the calculated
	// specular factor to the shine damper (obtained from an uniform variable).
	// The final specular light will get the shine damping value multiplied by the 
	// reflectivity and the light color.

	vec3 unitVectorToCamera = normalize(toCameraVector);
	vec3 lightDirection = -unitVectorToLight;
	vec3 reflectedLightDirection = reflect(lightDirection, unitSurfaceNormal);

	float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
	specularFactor = max(specularFactor, 0.0);

	float damperFactor = pow(specularFactor, shineDamper);
	
	vec3 specularLight = damperFactor * reflectivity * lightColor;


	// Fragment color:
	// Calculate the final color of the fragment mixing up the texture
	// data with the light calculated before

	out_Color = vec4(diffuseLight, 1.0) * texture(textureSampler, pass_textureCoords) + vec4(specularLight, 1.0);

}
