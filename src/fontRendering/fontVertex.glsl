#version 330

in vec2 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform vec2 translation;

void main(void){

	gl_Position = vec4(position + translation * vec2(2.0, -2.0), 0.0, 1.0); // apply transformation matrix but keep in mind to transform to opengl coordinate system (-1,1)
	pass_textureCoords = textureCoords;

}
