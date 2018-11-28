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
uniform vec3 lightColor[5];
uniform vec3 attenuation[5];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

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
		totalDiffuse = totalDiffuse + (brightness * lightColor[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	
	vec4 textureColor = texture(modelTexture,pass_textureCoordinates);
	if(textureColor.a<0.5){
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
	    
	    // glow effect
	    // It will use green component of the specular map, so if there is green above
	    // a given value (0.5) the diffuse light will be set to full brightness (1,1,1) 
	    if (mapInfo.g > 0.5){
	    	// instead of using full brightness, we use a glow using the average of 
	    	// current light color 
	    	//float color = (lightColor[0].r + lightColor[0].g + lightColor[0].b)/3.0;
			//totalDiffuse = vec3(color); 
			totalDiffuse = vec3(1.0);
	    }
	}
	
	out_Color =  vec4(totalDiffuse,1.0) * textureColor + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColor,1.0),out_Color, visibility);

}
