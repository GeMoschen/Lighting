package softshadows;

import org.lwjgl.util.vector.Vector2f;

public class Vector2D {
    private Vector2f vector;

    public Vector2D(float x, float y) {
        this.vector = new Vector2f(x, y);
    }

    public Vector2f getVector() {
        return vector;
    }

    public void set(Vector2D position) {
        this.set(position.getX(), position.getY());
    }

    public void set(float x, float y) {
        this.vector.set(x, y);
    }

    @Override
    public String toString() {
        return "<Vector2D " + this.vector.toString() + ">";
    }

    public float getX() {
        return this.getVector().getX();
    }

    public float getY() {
        return this.getVector().getY();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Vector2D) {
            Vector2D other = (Vector2D) obj;
            return other == this || this.vector.equals(other.getVector());
        }
        return false;
    }

    public boolean nonZero() {
        return this.getX() == 0 && this.getY() == 0;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.getX() + other.getX(), this.getY() + other.getY());
    }

    public Vector2D sub(Vector2D other) {
        return new Vector2D(this.getX() - other.getX(), this.getY() - other.getY());
    }

    public Vector2D mul(float factor) {
        return new Vector2D(this.getX() * factor, this.getY() * factor);
    }

    public Vector2D copy() {
        return new Vector2D(this.getX(), this.getY());
    }

    public void rotate(float radius) {
        float length = this.getLength();

        if (this.getY() != 0) {
            radius += Math.asin(this.getY() / length);
        } else if (this.getX() != 0) {
            radius += Math.asin(this.getX() / length);
        } else {
            return;
        }
        this.vector.x = (float) (length * Math.cos(radius));
        this.vector.y = (float) (length * Math.sin(radius));
    }

    public float getLength() {
        return (float) Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY());
    }

    public void normalize() {
        float length = this.getLength();
        this.vector.x /= length;
        this.vector.y /= length;
    }

    public float getAngle() {
        return (float) Math.atan2(this.getY(), this.getX());
    }

    public static float dot(Vector2D a, Vector2D b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    public static float cross(Vector2D a, Vector2D b) {
        return a.getX() * b.getX() - a.getY() * b.getY();
    }

    public static Vector2D fromPoints(Vector2D point, Vector2D toPoint) {
        return point.sub(toPoint);
    }
}
