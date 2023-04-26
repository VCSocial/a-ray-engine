#version 330 core
out vec4 result;

in vec4 colorCoordinates;
//in vec2 uvCoordinates;

//uniform sampler2D texture0;

void main() {
    //FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
    result = colorCoordinates;
    //result = texture2D(texture0, uvCoordinates);
}
