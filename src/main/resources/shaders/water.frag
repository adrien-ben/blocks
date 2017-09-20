#version 330

struct Light {
    vec4 color;
    float intensity;
};

struct DirectionalLight {
    Light base;
    vec3 direction;
};

in vec3 passPosition;

out vec4 finalColor;

uniform Light uAmbient;
uniform DirectionalLight uSunLight;
uniform vec3 uCameraPosition;

vec3 up = vec3(0.0, 1.0, 0.0);
vec3 waterColor = vec3(49.0/255.0, 149.0/255.0, 237.0/255.0);

void main() {
    vec3 toLight = -normalize(uSunLight.direction);
    vec3 toEye = normalize(uCameraPosition - passPosition);
    vec3 halfVector = normalize(toEye + toLight);

    float specular = pow(max(0.0, dot(halfVector, up)), 32);
    float diffuse = max(0.0, dot(toLight, up));

    vec3 color = (uAmbient.color.rgb*uAmbient.intensity + diffuse*uSunLight.base.color.rgb*uSunLight.base.intensity)*waterColor;
    finalColor = vec4(color + specular*uSunLight.base.color.rgb*uSunLight.base.intensity, 0.7);
}
