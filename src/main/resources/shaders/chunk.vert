#version 330

layout(location = 0) in ivec4 vPosition;
layout(location = 1) in vec2 vTexCoords;

out vec2 passTexCoords;

uniform mat4 uVP;

void main() {
    passTexCoords = vTexCoords;
    gl_Position = uVP*vPosition;
}

