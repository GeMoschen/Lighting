package softshadows;

import static org.lwjgl.opengl.GL11.*;

public class ShadowFin {

    private static PenumbraShader shader = new PenumbraShader();

    private Vector2D rootPosition, inner, outer;
    private float penumbraIntensity = 1.0f, umbraIntensity = 0.0f, z = 0f;
    private int index = -1;

    public ShadowFin(Vector2D rootPosition) {
        this.rootPosition = rootPosition;
        this.inner = null;
        this.outer = null;
    }

    public void setInner(Vector2D inner) {
        this.inner = inner;
    }

    public void setOuter(Vector2D outer) {
        this.outer = outer;
    }

    public void setPenumbraIntensity(float penumbraIntensity) {
        this.penumbraIntensity = penumbraIntensity;
    }

    public void setUmbraIntensity(float umbraIntensity) {
        this.umbraIntensity = umbraIntensity;
    }

    public float getAngle() {
        Vector2D uv = this.inner.copy();
        Vector2D pv = this.outer.copy();
        uv.normalize();
        pv.normalize();
        return (float) Math.acos(Vector2D.dot(uv, pv));
    }

    public void render() {
        shader.bind();
        shader.setState(this.rootPosition, this.getAngle(), this.inner, this.umbraIntensity, this.penumbraIntensity);
        glBegin(GL_TRIANGLES);
        {
            glVertex3f(this.rootPosition.getX(), this.rootPosition.getY(), this.z);
            glVertex3f(this.rootPosition.getX() + this.outer.getX(), this.rootPosition.getY() + this.outer.getY(), this.z);
            glVertex3f(this.rootPosition.getX() + this.inner.getX(), this.rootPosition.getY() + this.inner.getY(), this.z);
        }
        glEnd();
        shader.unbind();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Vector2D getInner() {
        return inner;
    }

    public Vector2D getOuter() {
        return outer;
    }
}
