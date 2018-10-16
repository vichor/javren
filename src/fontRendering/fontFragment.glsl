#version 330

in vec2 pass_textureCoords;

out vec4 out_color;

uniform vec3 color;
uniform sampler2D fontAtlas;

// Character WIDTH is the point counting from the middle of the character stroke
// (the drawn part of the character) to the end of the character.
// From that point forward, it starts a fading to transparent of the character.
// This fading (or blurry effect) width is defined by EDGE.
// TODO: EDGE shall be smaller for big font characters to avoid big blurry
// characters. It will shall be greater for smaller ones. Font size can be
// defined as a uniform and make this EDGE calculated based on that.
const float WIDTH = 0.5;
const float EDGE = 0.1;

// GLOWING EFFECT
// These width and edge below are used for outlined text. It defines a second
// edge of the character where the outlined will be drawn and blur to background
// As above, the BORDERWIDTH will be used to define the width of the opaque outline
// while the BORDEREDGE willbe used to define the transition-to-transparent outline
// (NOTE: this transition-to-transparent is called antialiasing)
// TODO: Set borderwidth as uniform to be able to configure if glowing is desired or not
const float BORDERWIDTH = 0.3; // set to 0 if glowing is not desired
const float BORDEREDGE = 0.4;

// DROP SHADOW
// TODO: do OFFSET as uniform to be able to configure if shadow is desired or not
// (NOTE: Shadow needs glowing!!)
const vec2 OFFSET = vec2(0.005, 0.005); // set to 0 if shadow is not desired

// GLOWING/SHADOW COLOR
// Shadow shall use (0.2, 0.2, 0.2) for a good effect
// Glowing may use whatever it's preferred. Nice effect is using invert color
// (1-color.r, 1-color.g, 1-color.b).
// TODO: calculate automatically outline color or allow its configuration through uniform
const vec3 OUTLINECOLOR = vec3(0.2, 0.2, 0.2);

void main(void){

	float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
	// smoothstep is creating the blurring from absolute transparent to
	// absolute opaque. As we want the inverse transition, we do 1 - smoothstep
	float alpha = 1.0 - smoothstep(WIDTH, WIDTH+EDGE, distance);

	float outlineDistance = 1.0 - texture(fontAtlas, pass_textureCoords + OFFSET).a;
	float outlineAlpha = 1.0 - smoothstep(BORDERWIDTH, BORDERWIDTH+BORDEREDGE, outlineDistance);

	float overallAlpha = alpha + (1.0-alpha)*outlineAlpha;
	vec3 overallColor = mix(OUTLINECOLOR, color, alpha / overallAlpha);

	out_color = vec4(overallColor, overallAlpha);

}
