package softshadows;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;

public class PenumbraShader extends Shader {

    public PenumbraShader() {
        this.loadPixelShader("penumbra.frag");
        this.loadVertexShader("penumbra.vert");
    }

    public void setState(Vector2D origin, float angle, Vector2D innerVector, float innerIntensity, float outerIntensity) {
        glUniform2f(glGetUniformLocation(this.getShaderProgramID(), "origin"), origin.getX(), origin.getY());
        glUniform1f(glGetUniformLocation(this.getShaderProgramID(), "angle"), angle);
        glUniform2f(glGetUniformLocation(this.getShaderProgramID(), "inner"), innerVector.getX(), innerVector.getY());
        glUniform1f(glGetUniformLocation(this.getShaderProgramID(), "inner_intensity"), innerIntensity);
        glUniform1f(glGetUniformLocation(this.getShaderProgramID(), "outer_intensity"), outerIntensity);
    }
}
