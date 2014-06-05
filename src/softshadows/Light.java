package softshadows;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Light {

    private static LightShader shader = new LightShader();
    private static final Random random = new Random();

    private Vector2D position;
    private Vector3f color;
    private float radius, sourceRadius, z;

    public Light(Vector2D position) {
        this.position = position;
        this.z = 0;
        this.radius = 600;
        this.sourceRadius = 5;
        this.color = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y);
        this.z = z;
    }

    public Vector2D innerVector(Vector2D edge, int step) {
        Vector2D cv = new Vector2D(this.position.getX() - edge.getX(), this.position.getY() - edge.getY());

        boolean useNegative = false;
        if (this.position.getX() < edge.getX()) {
            useNegative = true;
        }

        Vector2D perpVector = new Vector2D(this.position.getX() - edge.getX(), this.position.getY() - edge.getY());
        perpVector.normalize();

        if (step == 1) {
            if (useNegative) {
                perpVector = perpVector.mul(-this.sourceRadius);
                perpVector.rotate((float) -(Math.PI * 2f / 4f));
            } else {
                perpVector.rotate((float) (Math.PI * 2f / 4f));
                perpVector = perpVector.mul(this.sourceRadius);
            }
        } else {
            if (useNegative) {
                perpVector = perpVector.mul(-this.sourceRadius);
                perpVector.rotate((float) (Math.PI * 2f / 4f));
            } else {
                perpVector = perpVector.mul(this.sourceRadius);
                perpVector.rotate((float) -(Math.PI * 2f / 4f));
            }
        }

        cv = new Vector2D(this.position.getX() + perpVector.getX() - edge.getX(), this.position.getY() + perpVector.getY() - edge.getY());
        cv = cv.mul(-1f);
        cv.normalize();
        return cv.mul(((float) this.radius) * 10f);
    }

    public Vector2D outerVector(Vector2D edge, int step) {
        Vector2D cv = new Vector2D(this.position.getX() - edge.getX(), this.position.getY() - edge.getY());

        boolean useNegative = false;
        if (this.position.getX() < edge.getX()) {
            useNegative = true;
        }

        Vector2D perpVector = new Vector2D(this.position.getX() - edge.getX(), this.position.getY() - edge.getY());
        perpVector.normalize();

        if (step == 1) {
            if (useNegative) {
                perpVector = perpVector.mul(-this.sourceRadius);
                perpVector.rotate((float) (Math.PI * 2f / 4f));
            } else {
                perpVector = perpVector.mul(this.sourceRadius);
                perpVector.rotate((float) -(Math.PI * 2f / 4f));
            }
        } else {
            if (useNegative) {
                perpVector = perpVector.mul(-this.sourceRadius);
                perpVector.rotate((float) -(Math.PI * 2f / 4f));
            } else {
                perpVector.rotate((float) (Math.PI * 2f / 4f));
                perpVector = perpVector.mul(this.sourceRadius);
            }
        }

        cv = new Vector2D(this.position.getX() + perpVector.getX() - edge.getX(), this.position.getY() + perpVector.getY() - edge.getY());
        cv = cv.mul(-1f);
        cv.normalize();
        return cv.mul(((float) this.radius) * 10f);
    }

    public void renderSource() {
        glBegin(GL_TRIANGLE_FAN);
        {
            glColor4f(1.0f, 1.0f, 0f, 1.0f);
            glVertex3f(this.position.getX(), this.position.getY(), this.z);

            float angle = 0f;
            while (angle <= Math.PI * 2f) {
                glVertex3f((float) (this.sourceRadius * Math.cos(angle) + this.position.getX()), (float) (this.sourceRadius * Math.sin(angle) + this.position.getY()), this.z);
                angle += Math.PI * 2f / 12f;
            }
            glVertex3f(this.position.getX() + this.sourceRadius, this.position.getY(), this.z);
        }
        glEnd();
    }

    public void render(float intensity) {
        shader.bind();
        shader.setState(new Vector4f(this.color.x, this.color.y, this.color.z, intensity));

        glPushMatrix();
        {
            glTranslatef(this.position.getX(), this.position.getY(), 0);
            glScalef(this.sourceRadius, this.sourceRadius, 0);
            glBegin(GL_QUADS);
            {
                glVertex3f(-1f, -1f, z);
                glVertex3f(+1f, -1f, z);
                glVertex3f(+1f, +1f, z);
                glVertex3f(-1f, +1f, z);
            }
            glEnd();
        }
        glPopMatrix();

        shader.unbind();
        this.renderSource();
    }
}
