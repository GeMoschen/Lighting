package shadows;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

public class Main {

    public final int width = 800;
    public final int height = 600;

    public ArrayList<ConvexHull> hulls = new ArrayList<ConvexHull>();

    private int fragmentShader;
    private int shaderProgram;

    private void render() {
        this.updateLights();

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        for (ConvexHull hull : this.hulls) {
            hull.render();
        }
        Display.update();
        Display.sync(60);
    }

    private void updateLights() {
        // lights.get(0).location.x = Mouse.getX();
        // lights.get(0).location.y = height - Mouse.getY();
        // lights.get(0).intensity = 1f;
        // lights.get(0).red = 10f;
        // lights.get(0).green = 0f;
        // lights.get(0).blue = 0f;
    }

    private void setUpObjects() {
        int lightCount = 1;
        int blockCount = 5;

        Random random = new Random();
        for (int i = 1; i <= blockCount; i++) {
            int x = 100 + i * 30;
            int y = 100;
            this.hulls.add(new ConvexHull(new Vector3f(x, y, 0), new Vector3f(x - 10, y - 10, 0), new Vector3f(x + 10, y - 10, 0), new Vector3f(x + 10, y + 10, 0), new Vector3f(x - 10, y + 10, 0)));
        }
    }

    private void initialize() {
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.setTitle("2D Lighting");
            Display.create(new PixelFormat(0, 16, 1));
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        //
        // shaderProgram = glCreateProgram();
        // fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        // StringBuilder fragmentShaderSource = new StringBuilder();
        //
        // try {
        // String line;
        // BufferedReader reader = new BufferedReader(new
        // FileReader("spotLight.frag"));
        // while ((line = reader.readLine()) != null) {
        // fragmentShaderSource.append(line).append("\n");
        // }
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        //
        // glShaderSource(fragmentShader, fragmentShaderSource);
        // glCompileShader(fragmentShader);
        // if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
        // System.err.println("Fragment shader not compiled!");
        // }
        //
        // glAttachShader(shaderProgram, fragmentShader);
        // glLinkProgram(shaderProgram);
        // glValidateProgram(shaderProgram);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        glClearColor(1, 0, 0, 0);
    }

    private void cleanup() {
        glDeleteShader(fragmentShader);
        glDeleteProgram(shaderProgram);
        Display.destroy();
    }

    public static void main(String[] args) {
        Main main = new Main();

        main.setUpObjects();
        main.initialize();

        while (!Display.isCloseRequested()) {
            main.render();
        }

        main.cleanup();
    }
}
