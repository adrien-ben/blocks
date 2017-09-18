#version 330

in vec2 passTexCoords;
in vec3 passNormal;

out vec4 finalColor;

uniform sampler2D sampler;

vec3 ambient = vec3(0.3, 0.3, 0.3);
vec3 lightDirection = normalize(vec3(1.2, -0.8, 3.0));
vec3 sunLight = vec3(0.8, 0.8, 0.8);

void main() {
    float light = max(0.0, dot(-lightDirection, passNormal));
    vec3 color = (ambient + light*sunLight)*texture2D(sampler, passTexCoords).rgb;
    finalColor = vec4(color, 1.0);
}
