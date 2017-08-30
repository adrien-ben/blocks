#version 330

in vec2 passTexCoords;

out vec4 finalColor;

uniform sampler2D sampler;

void main() {
    finalColor = vec4(texture2D(sampler, passTexCoords).rgb, 1.0);
}