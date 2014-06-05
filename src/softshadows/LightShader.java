package softshadows;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.util.vector.Vector4f;

public class LightShader extends Shader {

    public LightShader() {
        this.loadPixelShader("light.frag");
        this.loadVertexShader("light.vert");
    }

    public void setState(Vector4f color) {
        glUniform4f(glGetUniformLocation(this.getShaderProgramID(), "color"), color.x, color.y, color.z, color.w);
    }
}
