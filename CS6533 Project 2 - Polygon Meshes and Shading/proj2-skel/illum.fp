/*
 * Illumination fragment shader: phong and toon shading
 */

// From vertex shader: position and normal (in eye coordinates)
varying vec4 pos;
varying vec3 norm;

// Do Phong specular shading (r DOT v) instead of Blinn-Phong (n DOT h)
uniform int phong;
// Do toon shading
uniform int toon;
// If false, then don't do anything in fragment shader
uniform int useFragShader;

// Toon shading parameters
uniform float toonHigh;
uniform float toonLow;

// Apply volume texture to diffuse term
//uniform int volTexture;
// Volume texture scale
//uniform float volRes;

// Compute toon shade value given diffuse and specular component levels
vec4 toonShade(float diffuse, float specular)
{
    // ... (placeholder)
    return gl_FrontLightProduct[0].ambient;
}

void main()
{
    if (useFragShader == 0) {
        // Pass through
        gl_FragColor = gl_Color;
    } else {
        // Do lighting computation...

        // ...
        
        if (toon == 1) {
            // ... (placeholder)
            gl_FragColor = toonShade(0.0, 0.0);
        } else {
            // ... (placeholder)
            gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        }
    }
}
