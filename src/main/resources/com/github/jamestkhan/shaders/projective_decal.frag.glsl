#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

uniform sampler2D u_decalTexture;
uniform vec2 u_decalCameraClipping;// x = near, y = far
uniform float u_scrollSpeed;
uniform bool u_stretchToFrustum;
uniform vec3 u_lightDirection;
uniform vec3 u_ambientColor;

varying vec4 v_projectedCoords;
varying vec3 v_surfaceNormal;

void main() {
    vec3 ndc = v_projectedCoords.xyz / v_projectedCoords.w; // normalized device coordinates

    // Check if the fragment is inside the decal camera's clipping planes
    float nearNDC = 2.0 * u_decalCameraClipping.x / (u_decalCameraClipping.y - u_decalCameraClipping.x) - (u_decalCameraClipping.y + u_decalCameraClipping.x) / (u_decalCameraClipping.y - u_decalCameraClipping.x);
    float farNDC = 2.0 * u_decalCameraClipping.y / (u_decalCameraClipping.y - u_decalCameraClipping.x) - (u_decalCameraClipping.y + u_decalCameraClipping.x) / (u_decalCameraClipping.y - u_decalCameraClipping.x);
    if (ndc.z < nearNDC || ndc.z > farNDC) discard;

    // Check if the fragment is inside the decal camera's frustum
    if (ndc.x < -1.0 || ndc.x > 1.0 || ndc.y < -1.0 || ndc.y > 1.0)
    discard;

    // Calculate texture coordinates directly from NDC, mapping [-1, 1] to [0, 1]
    vec2 texCoords;

    if (u_stretchToFrustum) {
        // Stretch texture to fill the frustum
        texCoords = (ndc.xy + vec2(1.0)) * 0.5;
    } else {
        // Keep the texture square in its original form
        texCoords = ndc.xy + vec2(0.5);
    }

    // Scroll the texture
    texCoords += vec2(0.0, u_scrollSpeed);
    // Wrap coordinates around
    texCoords.y = mod(texCoords.y, 1.0);

    vec4 decalColor = texture2D(u_decalTexture, texCoords);

    // Clip transparent pixels
    if (decalColor.a < 0.01) discard;

    // Calculate lighting
    vec3 lightDirection = normalize(u_lightDirection);
    float diffuse = max(dot(v_surfaceNormal, -lightDirection), 0.0);

    // Apply lighting to the decal color
    decalColor.rgb *= diffuse + u_ambientColor;

    // Fade the decal out near the edges, small fade can help with edge artifacts
    // on mipmapped repeating textures
    float fadeDistance = 0.02;  // higher = bigger fade
    // Calculate distance to the nearest edge
    float distToEdge = min(min(texCoords.x, 1.0 - texCoords.x), min(texCoords.y, 1.0 - texCoords.y));
    float alphaMultiplier = clamp(distToEdge / fadeDistance, 0.0, 1.0);

    decalColor.a *= alphaMultiplier;

    gl_FragColor = decalColor;
}