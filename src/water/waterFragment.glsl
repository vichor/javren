#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform float moveFactor;

const float waveStrength = 0.002;

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

	// Calculate distortion of water based on DuDv map texture
	// Take the pixel from the texture coordinate. Take only red and green colors
	// from the pixel as this is how the DuDv map is created. To get positive and
	// negative numbers (colors go from 0 to 1.0) multiply by 2 and substract 1
	// (and we get numbers from -1 to 1). This will result in a heavy distortion
	// so now we can apply a distortion limitator which we call wave strength.
	// Apply the distortion to the calculated refraction and reflection coords.
	// We calculate another distortion in another direction to add more realism.
	vec2 distortion1 = ( texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 2.0 - 1.0 ) * waveStrength;
	vec2 distortion2 = ( texture(dudvMap, vec2(-textureCoords.x + moveFactor, textureCoords.y + moveFactor)).rg * 2.0 - 1.0 ) * waveStrength;
	vec2 distortion = distortion1 + distortion2;
	refractTexCoords += distortion;
	reflectTexCoords += distortion;

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
	// Mix the textures using 0.5 factor (mix them equally)
	out_Color = mix(reflectColor, refractColor, 0.5);

	// Add blue tint
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2);

}
