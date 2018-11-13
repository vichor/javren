#version 330

in vec2 textureCoords;

out vec4 out_colour;


uniform sampler2D modelTexture; // transparency in textures.

void main(void){

	// deal with transparent textures: discard fragment if the alpha channel is below 0.5
	float alpha = texture(modelTexture, textureCoords).a;
	if (alpha < 0.5){
		discard;
	}

	out_colour = vec4(1.0);
	
}