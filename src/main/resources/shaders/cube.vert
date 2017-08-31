#version 330

layout(location = 0) in vec4 vPosition;

uniform mat4 uVP;
uniform vec3 uPosition;

void main() {
    gl_Position = uVP*vec4(vPosition.xyz + uPosition.xyz, vPosition.w);
}

