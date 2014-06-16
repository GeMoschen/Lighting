package mdesl.test.shadertut;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.test.Util;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ShaderLesson6 extends SimpleGame {

    public static void main(String[] args) throws LWJGLException {
        Game game = new ShaderLesson6();
        game.setDisplayMode(800, 600, false);
        game.start();
    }

    // our texture to blur
    Texture rock, rockNormals;

    // we'll use a single batch for everything
    SpriteBatch batch;

    // our lighting shader
    ShaderProgram shader;

    public static final float DEFAULT_LIGHT_Z = 0.075f;

    public static final Vector3f LIGHT_POS = new Vector3f(0f, 0f, DEFAULT_LIGHT_Z);

    // Light RGB and intensity (alpha)
    public static final Vector4f LIGHT_COLOR = new Vector4f(1f, 0.8f, 0.6f, 1f);

    // Ambient RGB and intensity (alpha)
    public static final Vector4f AMBIENT_COLOR = new Vector4f(0.6f, 0.6f, 1f, 0.2f);

    // Attenuation coefficients for light falloff
    public static final Vector3f FALLOFF = new Vector3f(.4f, 3f, 20f);

    protected void create() throws LWJGLException {
        super.create();

        try {
            // load our texture with linear filter
            rock = new Texture(Util.getResource("res/brick.png"), Texture.LINEAR);
            rockNormals = new Texture(Util.getResource("res/brick_normal.png"), Texture.LINEAR);
        } catch (IOException e) {
            throw new RuntimeException("couldn't decode texture");
        }

        // load our shader program and sprite batch
        try {
            // our basic pass-through vertex shader
            final String VERT = Util.readFile(Util.getResourceAsStream("lesson6.vert"));

            // our fragment shader, which does the blur in one direction at a
            // time
            final String FRAG = Util.readFile(Util.getResourceAsStream("lesson6.frag"));

            // create our shader program
            ShaderProgram.setStrictMode(false);
            shader = new ShaderProgram(VERT, FRAG, SpriteBatch.ATTRIBUTES);

            // Good idea to log any warnings if they exist
            if (shader.getLog().length() != 0)
                System.out.println(shader.getLog());

            // always a good idea to set up default uniforms...
            shader.use();

            // our normal map
            shader.setUniformi("u_normals", 1); // GL_TEXTURE1

            // light/ambient colors
            shader.setUniformf("LightColor", LIGHT_COLOR);
            shader.setUniformf("AmbientColor", AMBIENT_COLOR);
            shader.setUniformf("Falloff", FALLOFF);

            batch = new SpriteBatch(shader);
        } catch (Exception e) {
            // simple exception handling...
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected void render() throws LWJGLException {
        super.render();

        float shine = 0f;
        renderBatch(0, 0, shine);
        renderBatch(300, 0, shine);
        renderBatch(0, 300, shine);
        renderBatch(300, 300, shine);

        // bind normal map to texture unit 1
        glActiveTexture(GL_TEXTURE1);
        rockNormals.bind();

        // bind diffuse color to texture unit 0
        glActiveTexture(GL_TEXTURE0);
        rock.bind();
    }

    public void renderBatch(float posX, float posY, float shininess) {
        batch.begin();

        // shader will now be in use...

        // update light position, normalized to screen resolution
        float x = Mouse.getX() / (float) Display.getWidth();
        float y = Mouse.getY() / (float) Display.getHeight();
        // LIGHT_POS.z = 0.075f;

        float intens = 3f;
        ArrayList<Vector4f> lightColor = new ArrayList<Vector4f>();
        lightColor.add(new Vector4f(1, 1, 0, intens));
        lightColor.add(new Vector4f(1, 0, 0, intens));
        lightColor.add(new Vector4f(0, 1, 0, intens));
        lightColor.add(new Vector4f(0, 0, 1, intens));
        lightColor.add(new Vector4f(1f, 1f, 1f, intens));

        shader.setUniformi("lightCount", 1);
        shader.setUniformf("shininess", shininess);

        shader.setUniformf("LightPos[0]", new Vector3f(x, y, LIGHT_POS.z));
        shader.setUniformf("LightColor[0]", lightColor.get(4));

        shader.setUniformf("LightPos[1]", new Vector3f(300f / 800f, ((600f - 300f) / 600f), LIGHT_POS.z));
        shader.setUniformf("LightColor[1]", lightColor.get(0));

        shader.setUniformf("LightPos[2]", new Vector3f(500f / 800f, ((600f - 100f) / 600f), LIGHT_POS.z));
        shader.setUniformf("LightColor[2]", lightColor.get(1));

        shader.setUniformf("LightPos[3]", new Vector3f(100f / 800f, ((600f - 500f) / 600f), LIGHT_POS.z));
        shader.setUniformf("LightColor[3]", lightColor.get(2));

        shader.setUniformf("LightPos[4]", new Vector3f(500f / 800f, ((600f - 500f) / 600f), LIGHT_POS.z));
        shader.setUniformf("LightColor[4]", lightColor.get(3));

        shader.setUniformf("AmbientColor", 255f / 255f, 255f / 255f, 255f / 255f, .05f);

        // shader.setUniformf("Falloff[1]", new Vector3f(0.1f, 1f, 32f));
        // shader.setUniformf("Falloff[1]", new Vector3f(1f, 1f, 0.1f));
        // shader.setUniformf("Falloff[2]", new Vector3f(2f, 4f, 8f));
        // shader.setUniformf("Falloff[3]", new Vector3f(2f, 4f, 8f));
        // shader.setUniformf("Falloff[4]", new Vector3f(0.1f, 1f, 32f));
        // shader.setUniformf("Falloff[5]", new Vector3f(0.1f, 1f, 32f));
        for (int i = 0; i < 5; i++) {
            shader.setUniformf("Falloff[" + i + "]", new Vector3f(0.01f, 4f, 64f));
        }

        // bind normal map to texture unit 1
        glActiveTexture(GL_TEXTURE1);
        rockNormals.bind();

        // bind diffuse color to texture unit 0
        glActiveTexture(GL_TEXTURE0);
        rock.bind();

        // draw the texture unit 0 with our shader effect applied
        batch.draw(rock, posX, posY, 400, 400);
        batch.end();

    }

    public void mousePressed(int x, int y, int button) {
        LIGHT_POS.z = DEFAULT_LIGHT_Z;
        System.out.println("New light Z: " + LIGHT_POS.z);
        if (button == 1) {
            // load our shader program and sprite batch
            try {
                // our basic pass-through vertex shader
                final String VERT = Util.readFile(Util.getResourceAsStream("lesson6.vert"));

                // our fragment shader, which does the blur in one direction at
                // a
                // time
                final String FRAG = Util.readFile(Util.getResourceAsStream("lesson6.frag"));

                // create our shader program
                ShaderProgram.setStrictMode(false);
                shader = new ShaderProgram(VERT, FRAG, SpriteBatch.ATTRIBUTES);

                // Good idea to log any warnings if they exist
                if (shader.getLog().length() != 0)
                    System.out.println(shader.getLog());

                // always a good idea to set up default uniforms...
                shader.use();

                // our normal map
                shader.setUniformi("u_normals", 1); // GL_TEXTURE1

                // light/ambient colors
                shader.setUniformf("LightColor", LIGHT_COLOR);
                shader.setUniformf("AmbientColor", AMBIENT_COLOR);
                shader.setUniformf("Falloff", FALLOFF);

                batch = new SpriteBatch(shader);
                this.resize();
            } catch (Exception e) {
                // simple exception handling...
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void mouseWheelChanged(int delta) {
        LIGHT_POS.z = Math.max(0f, LIGHT_POS.z + (delta * 0.005f));
        System.out.println("New light Z: " + LIGHT_POS.z);
    }

    // called to resize the display
    protected void resize() throws LWJGLException {
        super.resize();
        shader.use();
        shader.setUniformf("Resolution", Display.getWidth(), Display.getHeight());
        batch.resize(Display.getWidth(), Display.getHeight());
    }
}