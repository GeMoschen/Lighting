package shadows;

import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class ConvexHull {

    private static final Random random = new Random();

    private Vector3f position, color;
    private List<Vector3f> points;

    public ConvexHull(Vector3f position) {
        this.position = position;
        this.points = new ArrayList<Vector3f>();
        this.color = new Vector3f(random.nextFloat() + 0.2f, random.nextFloat() + 0.2f, random.nextFloat() + 0.2f);
        this.color = new Vector3f(1, 1, 1);
        System.out.println(this.position + " > " + this.color);
    }

    public ConvexHull(Vector3f position, Vector3f... points) {
        this(position);
        this.append(points);
    }

    public void setColor(Vector3f color) {
        this.color.set(color.x, color.y, color.z);
    }

    public void setColor(int r, int g, int b) {
        this.color.set((float) (r / 255f), (float) (g / 255f), (float) (b / 255f));
    }

    public void append(Vector3f... points) {
        for (Vector3f point : points) {
            Vector3f newVector = new Vector3f();
            Vector3f.add(this.position, point, newVector);
            this.points.add(newVector);
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void move(Vector3f movement) {
        Vector3f.add(this.position, movement, this.position);
        for (Vector3f point : this.points) {
            Vector3f.add(point, movement, point);
        }
    }

    public void move(float x, float y) {
        this.move(x, y, 0);
    }

    public void move(float x, float y, float z) {
        this.move(new Vector3f(x, y, z));
    }

    public void render() {
        glPushMatrix();
        {
            glColor4f(this.color.x, this.color.y, this.color.z, 1.0f);
            glBegin(GL_POLYGON);
            {
                for (Vector3f point : this.points) {
                    glVertex3f(point.getX(), point.getY(), point.getZ());
                }
            }
            glEnd();

            glClear(GL_DEPTH_BUFFER_BIT);
            glColor4f(0.5f, 0.5f, 0.5f, 1f);
            glBegin(GL_POLYGON);
            {
                for (Vector3f point : this.points) {
                    glVertex3f(point.getX(), point.getY(), point.getZ());
                }
            }
            glEnd();

        }
        glPopMatrix();
    }
}
