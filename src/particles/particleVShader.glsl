#version 140

in vec2 position;

uniform mat4 modelViewMatrix;
uniform vec4 texOffsets;
uniform mat4 projectionMatrix;

out vec2 textureCoords1;  // current texture atlas stage
out vec2 textureCoords2;  // next texture atlas stage
out float blend;          // blend factor between stages

uniform float blendFactor;
uniform float numberOfRows;

void main(void){

	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y; // texture coordinate system has y axis inverted
	textureCoords /= numberOfRows;
	textureCoords1 = textureCoords + texOffsets.xy;
	textureCoords2 = textureCoords + texOffsets.zw;
	blend = blendFactor;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}
