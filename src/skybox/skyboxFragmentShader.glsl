#version 400

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMap;
uniform samplerCube cubeMap2;
uniform float blendFactor;
uniform vec3 fogColor;

const float lowerLimit = 0.0;
const float upperLimit = 30.0;

void main(void){
	// We use two texture cubes for the day and night skies. The factor for
	// the blending of both textures will be passed through uniform and be
	// depending on the day hour (12pm will use 100% of texture1; 12am will
	// use 100% of texture2, for instance).
	vec4 texture1 = texture(cubeMap, textureCoords);
	vec4 texture2 = texture(cubeMap2, textureCoords);

    vec4 textureColor = mix(texture1, texture2, blendFactor);

	// Skybox color will come from the skybox texture and will blend into
	// the fog color to make a seamless transition and help in having a
	// nice fog effect on the scene. A factor depending on upper and lower
	// limits will be used to define the blending process.
    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
    factor = clamp(factor, 0.0, 1.0); // factor between 0 and 1
    out_Color = mix(vec4(fogColor, 1.0), textureColor, factor);

}
