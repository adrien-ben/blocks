#version 330

layout(location = 0) in ivec2 vPosition;

uniform mat4 uVP;
uniform vec3 uOffset;
uniform int uWaterLevel;

out vec3 passPosition;

void main() {
    passPosition = vec3(vPosition.x + uOffset.x, uWaterLevel - 0.1, vPosition.y + uOffset.z);
    gl_Position = uVP*vec4(passPosition, 1.0);
}
