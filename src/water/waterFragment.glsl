#version 400 core

in vec4 clipSpace;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;

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


	// Get the reflection and refraction textures
	vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
	vec4 refractColor = texture(refractionTexture, refractTexCoords);
	// Mix the textures using 0.5 factor (mix them equally)
	out_Color = mix(reflectColor, refractColor, 0.5);

}
