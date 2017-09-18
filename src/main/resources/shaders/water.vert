#version 330

layout(location = 0) in ivec2 vPosition;

uniform mat4 uVP;
uniform vec3 uOffset;
uniform int uWaterLevel;

void main() {
    gl_Position = uVP*vec4(vPosition.x + uOffset.x, uWaterLevel - 0.1, vPosition.y + uOffset.z, 1.0);
}
