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
	float c;
	if (diffuse < toonLow)
		c = 0.2;
	else if (diffuse > toonHigh)
		c = 0.8;
	else
		c = 0.45;
		
	vec4 color = gl_FrontLightProduct[0].ambient + gl_FrontLightProduct[0].diffuse * c;
	
	if (specular > toonHigh)
		return vec4(0.9, 0.9, 0.8, 1.0);
	else
		return color;
}

void main()
{
    if (useFragShader == 0) {
        // Pass through
        gl_FragColor = gl_Color;
    } else {
        
    	//vec4 _pos = normalize(pos);
    	vec4 _pos = pos;
    	vec3 _norm = normalize(norm);
   	
    	vec4 l = normalize(gl_LightSource[0].position - _pos);
    	vec4 camera = vec4(0.0, 0.0, 0.0, 1.0);
    	
    	vec4 v = normalize(camera - _pos);
    	vec4 h = normalize((l + v) / length(l + v));
    	
    	float n_dot_l = dot(_norm, l);
    	float n_dot_h = dot(_norm, h);
    	float d = max(0.0, n_dot_l);
    	float s = pow(max(0.0, n_dot_h), gl_FrontMaterial.shininess);
        
        if (toon == 1) {
            gl_FragColor = toonShade(d, s);
        } else {

        	vec4 color = gl_FrontLightProduct[0].ambient + gl_FrontLightProduct[0].diffuse * d;
    		
    		if (n_dot_l > 0.0)
    			color = color + gl_FrontLightProduct[0].specular * s;
    		
    		clamp(color[0], 0.0, 1.0);
    		clamp(color[1], 0.0, 1.0);
    		clamp(color[2], 0.0, 1.0);
    		clamp(color[3], 0.0, 1.0);
    		
    		gl_FragColor = color;
        }
    }
}
