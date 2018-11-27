#version 400 core

in vec2 pass_textureCoordinates;
in vec3 toLightVector[5];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform sampler2D normalMapTexture;
uniform sampler2D specularMap;
uniform float usesSpecularMap;
uniform vec3 lightColour[5];
uniform vec3 attenuation[5];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void){

	vec4 normalMap = texture(normalMapTexture, pass_textureCoordinates) * 2.0 - 1.0;  // to get values [-1,1] instead of [0,1]

	vec3 unitNormal = normalize(normalMap.rgb);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i=0;i<5;i++){
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);	
		float nDotl = dot(unitNormal,unitLightVector);
		float brightness = max(nDotl,0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor,shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	
	vec4 textureColour = texture(modelTexture,pass_textureCoordinates);
	if(textureColour.a<0.5){
		discard;
	}

	// Specular map
	// Specular maps indicate how shiny each of the pixel on a model is. The fragment
	// shader uses it to modify the specular light that a specific fragment receives.
	// Specular light information is stored in the red component of the specular map
	// (see the files *Specular.png)
	if (usesSpecularMap > 0.5){
	    vec4 mapInfo = texture(specularMap, pass_textureCoordinates);
	    totalSpecular *= mapInfo.r;
	}
	
	out_Color =  vec4(totalDiffuse,1.0) * textureColour + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColour,1.0),out_Color, visibility);

}
