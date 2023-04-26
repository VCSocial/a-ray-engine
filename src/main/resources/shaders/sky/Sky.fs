#version 330 core
out vec4 result;

in vec2 uvCoordinates;

uniform sampler2D texture0;

void main() {
    result = texture(texture0, uvCoordinates);
}
