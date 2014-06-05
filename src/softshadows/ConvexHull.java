package softshadows;

import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class ConvexHull {

    private static final Random random = new Random();

    private Vector2D position;
    private List<Vector2D> points;

    private float z = 0f;
    private Vector3f color;

    private static class Result {
        protected List<ShadowFin> list;
        protected int index;
        protected Vector2D vector;

        protected Result(List<ShadowFin> list, int index, Vector2D vector) {
            this.list = list;
            this.index = index;
            this.vector = vector;
        }
    }

    public ConvexHull(Vector2D position) {
        this.position = position.copy();
        this.points = new ArrayList<Vector2D>();
        this.color = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public ConvexHull(Vector2D position, Vector2D... points) {
        this(position);
        this.append(points);
    }

    public void setColor(Vector3f color) {
        this.color.set(color.x, color.y, color.z);
    }

    public void setColor(int r, int g, int b) {
        this.color.set((float) (r / 255f), (float) (g / 255f), (float) (b / 255f));
    }

    public void append(Vector2D... points) {
        for (Vector2D point : points) {
            this.points.add(point.add(this.position));
        }
    }

    private int findIndex(Vector2D find) {
        if (find == null) {
            return -1;
        }
        int index = -1;
        for (Vector2D vector : this.points) {
            index++;
            if (vector.equals(find)) {
                return index;
            }
        }
        return -1;
    }

    public void drawShadowGeometry(Light light) {
        int first = -1, last = -1;
        Vector2D currentPoint = null, prevPoint = null;

        boolean lastNodeFrontfacing = false;

        for (int x = 0; x < this.points.size(); x++) {
            try {
                currentPoint = this.points.get(x);
                prevPoint = this.points.get(x - 1);
                Vector2D nv = new Vector2D(currentPoint.getY() - prevPoint.getY(), currentPoint.getX() - prevPoint.getX());
                Vector2D lv = new Vector2D(currentPoint.getX() - light.getPosition().getX(), currentPoint.getY() - light.getPosition().getY());

                if (((nv.getX() * -1 * lv.getX()) + (nv.getY() * lv.getY())) > 0) {
                    if (lastNodeFrontfacing == false) {
                        last = this.findIndex(prevPoint);
                    }
                    lastNodeFrontfacing = true;
                } else {
                    if (lastNodeFrontfacing == true) {
                        first = this.findIndex(prevPoint);
                    }
                    lastNodeFrontfacing = false;
                }

                if (first < 0 || last < 0) {
                    return;
                }

                // GO ON HERE....
                Result r1 = this.createShadowFins(light, first, 1);
                Result r2 = this.createShadowFins(light, last, -1);

                for (ShadowFin fin : r1.list) {
                    fin.render();
                }
                for (ShadowFin fin : r2.list) {
                    fin.render();
                }

                List<Vector2D> backpoints = new ArrayList<Vector2D>();
                for (int i = r1.index; i < r1.index + this.points.size(); i++) {
                    backpoints.add(0, this.points.get(i % this.points.size()));
                    if (i % this.points.size() == r2.index) {
                        break;
                    }
                }

                List<Float> backLength = new ArrayList<Float>();
                int sumBackLength = 0;
                for (int i = 1; i < backpoints.size(); i++) {
                    float length = Vector2D.fromPoints(backpoints.get(i - 1), backpoints.get(i)).getLength();
                    backLength.add(0, length);
                    sumBackLength += length;
                }

                float a = 0;
                glColor4f(0f, 0f, 0f, 0f);
                glBegin(GL_TRIANGLE_STRIP);
                {
                    int i = 0;
                    for (Vector2D vector : backpoints) {
                        glVertex3f(vector.getX(), vector.getY(), this.z);
                        if (i != backpoints.size() - 2) {
                            glVertex3f(vector.getX() + (r1.vector.getX() * (a / sumBackLength)) + (r2.vector.getX() * (1 - (a / sumBackLength))), vector.getY() + (r1.vector.getY() * (a / sumBackLength)) + (r2.vector.getY() * (1 - (a / sumBackLength))), this.z);
                        } else {
                            glVertex3f(vector.getX() + r1.vector.getX(), vector.getY() + r1.vector.getY(), this.z);
                        }
                        a += backLength.get(i);
                        i++;
                    }
                }
                glEnd();
            } catch (Exception e) {

            }
        }
    }

    public Result createShadowFins(Light light, int origin, int step) {
        List<ShadowFin> list = new ArrayList<ShadowFin>();

        int i = origin;
        while (true) {
            Vector2D p1 = this.points.get(i);
            i--;
            if (i < 0) {
                i = this.points.size() - 1;
            } else if (i == this.points.size()) {
                i = 0;
            }

            Vector2D p0 = this.points.get(i);

            Vector2D edge = Vector2D.fromPoints(p1, p0);
            edge.normalize();

            ShadowFin shadowFin = new ShadowFin(p0);
            shadowFin.setIndex(i);

            float angle = edge.getAngle() - light.outerVector(p0, step).getAngle();
            if (step == 1) {
                if (angle < 0 || angle > Math.PI * 0.5f) {
                    break;
                }
            } else if (step == -1) {
                if (angle > Math.PI) {
                    angle -= Math.PI * 2;
                }
                if (angle > 0 || angle < -Math.PI * 0.5f) {
                    break;
                }
            }

            shadowFin.setOuter(light.outerVector(p0, step));
            shadowFin.setInner(edge.mul(light.innerVector(p0, step).getLength()));
            list.add(shadowFin);
        }

        i = origin;
        while (true) {
            ShadowFin fin = new ShadowFin(this.points.get(i));
            fin.setIndex(i);
            fin.setOuter(light.outerVector(this.points.get(i), step));
            fin.setInner(light.innerVector(this.points.get(i), step));

            if (list.size() > 0) {
                fin.setOuter(list.get(0).getInner());
            }

            Vector2D p0 = this.points.get(i);

            i += step;
            if (i < 0) {
                i = this.points.size() - 1;
            } else if (i == this.points.size()) {
                i = 0;
            }

            Vector2D p1 = this.points.get(i);

            Vector2D edge = Vector2D.fromPoints(p1, p0);
            edge.normalize();

            boolean done = true;
            Vector2D penumbra = fin.getOuter().copy();
            penumbra.normalize();
            Vector2D umbra = fin.getInner().copy();
            umbra.normalize();

            if (Math.acos(Vector2D.dot(edge, penumbra)) < Math.acos(Vector2D.dot(umbra, penumbra))) {
                fin.setInner(edge.mul(light.outerVector(p0, step).getLength()));
                done = false;
            }

            list.add(0, fin);
            if (done) {
                break;
            }
        }

        float sumAngles = 0;
        for (ShadowFin fin : list) {
            sumAngles += fin.getAngle();
        }

        float angle = 0;
        for (ShadowFin fin : list) {
            fin.setUmbraIntensity(angle / sumAngles);
            angle += fin.getAngle();
            fin.setPenumbraIntensity(angle / sumAngles);
        }

        // wrong here...
        return new Result(list, list.get(0).getIndex(), list.get(0).getInner());
    }

    public void render() {
        glColor4f(this.color.x, this.color.y, this.color.z, 1.0f);
        glBegin(GL_POLYGON);
        {
            for (Vector2D point : this.points) {
                glVertex3f(point.getX(), point.getY(), this.z);
            }
        }
        glEnd();
    }
}
