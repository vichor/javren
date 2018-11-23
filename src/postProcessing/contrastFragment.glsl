#version 140

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;

const float constrast = 0.25;

void main(void){

	out_Color = texture(colorTexture, textureCoords);
	
	// Contrast
	// make the colors component between 0 and 0.5 and then it's scaled up 
	// using the contrast value so that the bringht colors get brighter and
	// the dark ones,darker; and make it up to between 0 to 1
	out_Color.rgb = (out_Color.rgb - 0.5) * (1.0 + constrast) + 0.5; 

}