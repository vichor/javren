#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[5];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

// To use blend maps we need different textures and thus one sampler for
// each of the texture is needed.
uniform sampler2D backgroundTexture;	// Texture to use by default or when the pixel in the blend map is black
uniform sampler2D rTexture;				// Texture to use when the pixel in the blend map is red
uniform sampler2D gTexture;				// Texture to use when the pixel in the blend map is green
uniform sampler2D bTexture;				// Texture to use when the pixel in the blend map is blue
uniform sampler2D blendMap;				// ... and the blend map texture


uniform vec3 lightColor[5];
uniform vec3 lightAttenuation[5];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {

	// MULTITEXTURING THE TERRAIN

	// Read the color of the pixel from the blend map
	vec4 blendMapColor = texture (blendMap, pass_textureCoords);

	// Calculate how much background color has the fragment.
	// This is 1 minus the sum of the components of the blend map pixel color
	// All of the color components of pixel from the blend map sums up 1. The
	// background color is the color when there is no other thing, so if we
	// sum up the amount of r,g,b from the blend map we can use that value to
	// know how much background is needed.
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);

	// Tiling the texture
	// Calculate the texture coordinates using the "overflow coordinates" method
	// This has been used earlier in the vertex shader. The texture coordinates
	// gets multiplied by 40 so that there is an overflow when getting a
	// coordinate (they range [0,1]). This has the consequence of having the
	// texture repeated once and over again, a nice trick to get tiling of the
	// textures.
	vec2 tiledTextureCoords = pass_textureCoords * 40.0;

	// Colors from each of the textures used in the blend map getting the pixel
	// from the calculated coordinate and multiplying by the factor extracted
	// from the blend map
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledTextureCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture, tiledTextureCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture, tiledTextureCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture, tiledTextureCoords) * blendMapColor.b;

	// Final terrain color is the mix of each of the colors from previous calculation
	vec4 blendedColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

	// Vector normalization for light calculations below
	vec3 unitSurfaceNormal  = normalize(surfaceNormal);

	// Diffuse and Specular light calculation. One for each light sources.
	vec3 totalDiffuseLight = vec3(0.0);
	vec3 totalSpecularLight = vec3(0.0);
	for(int i=0; i<5; i++){
		// Vector normalization depending on light source
		vec3 unitVectorToLight  = normalize(toLightVector[i]);

		// Attenuation:
		// Defined as 3 component values (stored in a vec3) which depends from the distance
		// The final brightness of the light will be inverse of the attenuation
		// (brightness=brightness/attenuationFactor)
		float distance = length(toLightVector[i]);
		float attenuationFactor = lightAttenuation[i].x + (lightAttenuation[i].y * distance) + (lightAttenuation[i].z * distance * distance);

		// Diffuse light:
		// Calculate how much light is receiving the fragment by doing the dot
		// product of the normalized surface normal vector with the normalized
		// vector pointing from the surface to the light
		float nDotProd = dot(unitSurfaceNormal, unitVectorToLight);
		float brightness = max(nDotProd, 0.2);	// Ambient light made by ensuring diffuse light is above 0.2
		totalDiffuseLight = totalDiffuseLight + (brightness * lightColor[i])/attenuationFactor;

		// Specular light:
		// This light is the reflected light from a fragment towards the camera.
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
		totalSpecularLight = totalSpecularLight + (damperFactor * reflectivity * lightColor[i])/attenuationFactor;
	}


	// FRAGMENT COLOR:
	// Calculate the final color of the fragment mixing up the texture
	// data with the light calculated before

	vec4 fragmentColor = vec4(totalDiffuseLight, 1.0) * blendedColor + vec4(totalSpecularLight, 1.0);


	// FOG
	out_Color = mix(vec4(skyColor, 1.0), fragmentColor, visibility);

}
