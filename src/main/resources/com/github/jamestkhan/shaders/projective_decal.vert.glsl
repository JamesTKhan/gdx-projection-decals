#version 120
attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_modelViewProjectionMatrix;  // Model-view-projection matrix for the main camera
uniform mat4 u_decalMatrix;  // Model-view-projection matrix for the decal camera
uniform mat4 u_worldTransform;  // World transform of the model

varying vec4 v_projectedCoords;
varying vec3 v_surfaceNormal; // Pass the surface normal to the fragment shader

void main() {
    vec4 worldPosition = u_worldTransform * vec4(a_position, 1.0);

    gl_Position = u_modelViewProjectionMatrix * worldPosition;

    v_projectedCoords = u_decalMatrix * worldPosition;  // Use the already computed world space position
    v_surfaceNormal = mat3(u_worldTransform) * a_normal; // Calculate the surface normal
}

