#version 330

struct Light {
    vec4 color;
    float intensity;
};

struct DirectionalLight {
    Light base;
    vec3 direction;
};

in vec2 passTexCoords;
in vec3 passNormal;

out vec4 finalColor;

uniform Light uAmbient;
uniform DirectionalLight uSunLight;
uniform sampler2D sampler;

void main() {
    float light = max(0.0, dot(-normalize(uSunLight.direction), passNormal));
    vec3 color = (uAmbient.color.rgb*uAmbient.intensity + light*uSunLight.base.color.rgb*uSunLight.base.intensity)*texture2D(sampler, passTexCoords).rgb;
    finalColor = vec4(color, 1.0);
}
