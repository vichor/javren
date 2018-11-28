#version 150

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;

float getLumaBrightness(vec4 texel);



float getLumaBrightness(vec4 texel){
	// gets the Luma conversion: https://en.wikipedia.org/wiki/Grayscale#Luma_coding_in_video_systems
	return (texel.r * 0.2126) + (texel.g * 0.7152) + (texel.b * 0.0722);
}


void main(void){
	vec4 color = texture(colorTexture, textureCoords);
	float brightness = getLumaBrightness(color);
	out_Color = color * brightness;
}
