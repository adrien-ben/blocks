#version 330

layout(location = 0) in ivec4 vPosition;
layout(location = 1) in vec2 vTexCoords;
layout(location = 2) in uint vNormalIndex;

out vec2 passTexCoords;
out vec3 passNormal;

uniform mat4 uVP;

vec3 normals[6] = vec3[6](
    vec3(0.0, 0.0, 1.0),
    vec3(1.0, 0.0, 0.0),
    vec3(0.0, 0.0, -1.0),
    vec3(-1.0, 0.0, 0.0),
    vec3(0.0, 1.0, 0.0),
    vec3(0.0, -1.0, 0.0));

void main() {
    passNormal = normals[vNormalIndex];
    passTexCoords = vTexCoords*0.5 + 0.5;
    gl_Position = uVP*vPosition;
}

