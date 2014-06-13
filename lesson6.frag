//attributes from vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
uniform sampler2D u_normals;   //normal map

//values used for shading algorithm...
uniform int lightCount;
uniform vec2 Resolution;      //resolution of screen
uniform vec3 LightPos[125];        //light position, normalized
uniform vec4 LightColor[125];      //light RGBA -- alpha is intensity
uniform vec4 AmbientColor;    //ambient RGBA -- alpha is intensity 
uniform vec3 Falloff;         //attenuation coefficients

void main() {	
	//RGBA of our diffuse color
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);
		
	//RGB of our normal map
	vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;		
		
	//pre-multiply ambient color with intensity
	vec3 Ambient = AmbientColor.rgb * AmbientColor.a;		
	
	vec3 sum = vec3(0,0,0);	
	
	for(int i = 0; i < lightCount; i++) {	
		//The delta position of light
		vec3 LightDir = vec3(LightPos[i].xy - (gl_FragCoord.xy / Resolution.xy), LightPos[i].z);
		
		//Correct for aspect ratio
		LightDir.x *= Resolution.x / Resolution.y;
		
		//Determine distance (used for attenuation) BEFORE we normalize our LightDir
		float D = length(LightDir);
		
		//normalize our vectors
		vec3 N = normalize(NormalMap * 2.0 - 1.0);
		vec3 L = normalize(LightDir);
		
		//Pre-multiply light color with intensity
		//Then perform "N dot L" to determine our diffuse term
		vec3 Diffuse = (LightColor[i].rgb * LightColor[i].a * lightCount) * max(dot(N, L), 0.0);
		
		//calculate attenuation
		float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D));
		
		//the calculation which brings it all together
		vec3 Intensity = (Ambient + Diffuse * Attenuation) / lightCount;
		vec3 FinalColor = DiffuseColor.rgb * Intensity;
		sum += FinalColor;
    }
	gl_FragColor = vec4(sum, DiffuseColor.a);
}