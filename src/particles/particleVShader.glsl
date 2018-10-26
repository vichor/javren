#version 140

in vec2 position;           // global VAO data from vbo 0

in mat4 modelViewMatrix;    // from instance data from vbos 1 to 4
in vec4 texOffsets;         // from instance data from vbo 5
in float blendFactor;       // from instance data from vbo 6

out vec2 textureCoords1;  // current texture atlas stage
out vec2 textureCoords2;  // next texture atlas stage
out float blend;          // blend factor between stages

uniform mat4 projectionMatrix;
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
