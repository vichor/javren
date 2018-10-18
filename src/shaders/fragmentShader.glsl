#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[5];
in vec3 toCameraVector;
in float visibility;


out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor[5];
uniform vec3 lightAttenuation[5];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {


	// Vector normalization for light calculations below
	vec3 unitSurfaceNormal  = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

	// Diffuse and Specular light calculation. One for each light sources.
	// Also deal with the attenuation of the light
	vec3 totalDiffuseLight = vec3(0.0);
	vec3 totalSpecularLight = vec3(0.0);
	for (int i = 0; i < 5; i++){
		// Attenuation:
		// Defined as 3 component values (stored in a vec3) which depends from the distance
		// The final brightness of the light will be inverse of the attenuation 
		// (brightness=brightness/attenuationFactor)
		float distance = length(toLightVector[i]);
		float attenuationFactor = lightAttenuation[i].x + (lightAttenuation[i].y * distance) + (lightAttenuation[i].z * distance * distance);

		// Difuse Light:
		// Calculate how much light is receiving the fragment by doing the dot 
		// product of the normalized surface normal vector with the normalized
		// vector pointing from the surface to the light. Keep in mind that now
		// we have 5 light sources.
		vec3 unitVectorToLight  = normalize(toLightVector[i]);
		float nDotProd = dot(unitSurfaceNormal, unitVectorToLight);
		float brightness = max(nDotProd, 0.0);	
		totalDiffuseLight = totalDiffuseLight + (brightness * lightColor[i])/attenuationFactor;
	
	
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

		vec3 lightDirection = -unitVectorToLight;
		vec3 reflectedLightDirection = reflect(lightDirection, unitSurfaceNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float damperFactor = pow(specularFactor, shineDamper);
		totalSpecularLight = totalSpecularLight + (damperFactor * reflectivity * lightColor[i])/attenuationFactor;
	}
	
	// Apply ambient lighting to the calculated diffuse light
	//totalDiffuseLight = max(totalDiffuseLight, 0.02); // Ambient light made by ensuring difuse light is above 0.02


	// Texture Transparency management
	// High grass, ferns and other vegetation may be modeled as quads and use
	// a texture png file containing the drawing of the vegetation with a background
	// color. When rendering, this background color also gets rendered, as OpenGL 
	// has no way of knowing it's a background to be discarded. We can easily fix 
	// this by checking the color pulled from the texture file and command here
	// to discard it if one of its components do not reach a specific value.
	vec4 textureColor = texture(textureSampler, pass_textureCoords);
	if (textureColor.a < 0.5){
		discard;
	}
	

	// Fragment color:
	// Calculate the final color of the fragment mixing up the texture
	// data with the light calculated before

	vec4 fragmentColor = vec4(totalDiffuseLight, 1.0) * textureColor + vec4(totalSpecularLight, 1.0);


	// Fog
	// As far the fragment is in Z axis, the less visible it will be. Use the 
	// visibility factor to blur the final color of the fragment from the
	// calculated color (based on light and texture) to the sky color.
	out_Color = mix(vec4(skyColor, 1.0), fragmentColor, visibility);
}
