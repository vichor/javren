#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 fromLightVector;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;

uniform float moveFactor;
uniform vec3 lightColor;

const float WAVESTRENGTH = 0.02;
const float WATER_REFLECTIVITY = 10.0;

const float LIGHT_SHINEDAMPER = 20.0;
const float LIGHT_REFLECTIVITY = 0.6; // light reflectivity, no water reflectivity

void main(void) {

	// WATER EFFECT

	// Get coordinates in device normalized space.
	// The coordinates we have from the reflection/refraction textures are in
	// clip space, but we need the device normalized space to match the ones
	// used by the rendered scene and get the proper pixel from the
	// reflection/refraction texture
	// We do this by applying perspective division which is dividing by w
	// Then we have to change from GL coordinate system ( (0,0) in the center)
	// to texture coordinates ( (0,0) in the bottom left corner)=> /2 and +0.5.
	// Finally, get the xy coordinates and in case of the reflection, invert the
	// y coordinate because... well, it's a reflection
	vec2 ndc = (clipSpace.xy / clipSpace.w) /2.0 + 0.5;
	vec2 refractTexCoords = vec2(ndc.x, ndc.y);
	vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);

	// Use the DuDv map to get a distortion value, then use it to calculate the
	// distorted coordinates that will be used to get the final distortion from
	// the DuDv map. In more detail:
	// Get the pixel from the dudv map applying a time-changing offset (the move factor).
	// As the DuDv map is made out of red2green tones, take only these
	// color coordinates. This will be the distorted texture coordinates offset
	// which will need to be applied to the theoretical texture coordinates.
	// This whole calculation gives the texture coordinates to use but having
	// applying a distortion from DuDvMap + a distortion function of time.
	// The final distortion value will be extracted from the pixel of the DuDv map
	// on that distortion coordinate applying a distortion strength modifier and
	// making the value between -1 and 1 (*2-1).
	vec2 distortedTexCoordsOffset = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
	vec2 distortedTexCoords = textureCoords + vec2(distortedTexCoordsOffset.x, distortedTexCoordsOffset.y+moveFactor);
	vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * WAVESTRENGTH;

	// Apply the distortion to the refracted and reflected image
	refractTexCoords += totalDistortion;
	reflectTexCoords += totalDistortion;

	// A glitch appears at the bottom of the screen due to the projection values
	// of that area which is something close to 0 and due to the distortion
	// apply, these values may reach values below 0 and this causes overflow on
	// texture coordinate system. Fixing this needs clamping the coordinates
	refractTexCoords = clamp(refractTexCoords, 0.0, 1.0);
	reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
	reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);


	// Get the reflection and refraction textures
	vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
	vec4 refractColor = texture(refractionTexture, refractTexCoords);

	// Fresnel effect
	// When looked from upward, water is 100% transparent and 0% reflective
	// When looked from the side, water is 0% transparent and 100% reflective
	// Depending on camera position, the refracted and reflected factors change
	// so when the camera is looking upsidedown to the water, refraction is applied
	// and reflection is not (no objects reflected); when the camera is looking from
	// let's say, player's head, objects are reflected on its surface while it's more
	// difficult to see the refracted image.
	// This is calculated by checking the dot product of the vector pointing from the
	// water to the camera (calculated at vertex shader) with the normal of the water
	// which right now it's asumed to be (0,1,0).
	// Then we can apply a reflectivity modifier to configure how reflective the water is
	vec3 viewVector = normalize(toCameraVector);
	float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));
	refractiveFactor = pow(refractiveFactor, WATER_REFLECTIVITY);

	// Specular lighting: get the normal and do specular lights calculations

	// Get the normal from the distorted coordinates calculated above
	// The normal is calculated by extracting the color from the normal map texture
	// and then using the blue color (which is the dominant color on the texture) as
	// the y coordinate of the normal, while using the red and green colors as the
	// x and z coordinates. Besides, the y coordinate we want to be always positive
	// (normal always facing the sky) but the x and z we want it to be from -1 to 1
	// so we will * 2.0 - 1.0 to get that values.
	vec4 normalMapColor = texture(normalMap, distortedTexCoords);
	vec3 normal = vec3(normalMapColor.r*2.0 - 1.0, normalMapColor.b, normalMapColor.g * 2.0 - 1.0);
	normal = normalize(normal);

	// Specular light calculation
	vec3 reflectedLightVector = reflect(normalize(fromLightVector), normal);
	float specular = max(dot(reflectedLightVector, viewVector), 0.0);
	specular = pow(specular, LIGHT_SHINEDAMPER);
	vec3 specularHighlights = lightColor * specular * LIGHT_REFLECTIVITY;


	// Mix the textures using the refraction factor calculated by Fesnel effect
	out_Color = mix(reflectColor, refractColor, refractiveFactor);
	// Add blue tint and specular light (specular light with 0.0 alpha)
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0.0);


}
