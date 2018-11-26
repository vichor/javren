#version 150

in vec2 position;

// Blur is done by averaging all of the pixels from the values of neighbour pixels.
// We will apply apply this average from pixels found horizontally and vertically 
// (so, making a cross) from the pixel to blurrify (this is for performance reasons)
// This makes out a total of 11 horizontal pixels (as we take 5 pixels for each side
// + the pixel to blurrify)
out vec2 blurTextureCoords[11];

uniform float targetWidth;

void main(void){

	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTexCoords = position * 0.5 + 0.5;
	float pixelSize = 1.0 / targetWidth; // size of the pixel in the blur system
	
	for (int i =-5; i< 5; i++){
	    blurTextureCoords[i+5] = centerTexCoords + vec2(pixelSize * i, 0.0);
	}
}