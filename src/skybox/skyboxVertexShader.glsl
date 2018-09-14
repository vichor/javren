#version 400

in vec3 position;
out vec3 textureCoords; // texture coordinates are 3-coord system because it is pointing into a cube map

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){

	gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
	textureCoords = position;

}
