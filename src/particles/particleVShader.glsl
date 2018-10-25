#version 140

in vec2 position;

out vec2 textureCoords1;  // current texture atlas stage
out vec2 textureCoords2;  // next texture atlas stage
out float blend;          // blend factor between stages

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

uniform vec2 texOffset1;
uniform vec2 texOffset2;
uniform vec2 texCoordInfo;   // texCoordInfo.x will contain the number of rows; .y will contain the blend factor

void main(void){

	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y; // texture coordinate system has y axis inverted
	textureCoords /= texCoordInfo.x;
	textureCoords1 = textureCoords + texOffset1;
	textureCoords2 = textureCoords + texOffset2;
	blend = texCoordInfo.y;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}
