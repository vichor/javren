#version 400

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMap;
uniform vec3 fogColor;

const float lowerLimit = 0.0;
const float upperLimit = 30.0;

void main(void){
	// Skybox color will come from the skybox texture and will blend into
	// the fog color to make a seamless transition and help in having a
	// nice fog effect on the scene. A factor depending on upper and lower
	// limits will be used to define the blending process.
    vec4 textureColor = texture(cubeMap, textureCoords);
    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
    factor = clamp(factor, 0.0, 1.0); // factor between 0 and 1
    out_Color = mix(vec4(fogColor, 1.0), textureColor, factor);

}
