package lorenz875013.a07.Shapes;

import cgtools.Vec3;
import static cgtools.Vec3.*;

import lorenz875013.a07.Materials.Material;
import lorenz875013.a07.RayTracer.Hit;
import lorenz875013.a07.RayTracer.Ray;

public class Cone implements Shape{
    /** should always point from the tip in direction of increasing radius */
    public Vec3 tipPos;
    public Vec3 unitVec;
    public double tipAngle;
    public Material material;

    public Cone(Vec3 tipPos, Vec3 unitVec, double tipAngle, Material material){
        this.tipPos = tipPos;
        this.unitVec = unitVec;
        this.tipAngle = tipAngle;
        this.material = material;
    }

    public Hit intersect(Ray ray){
        /**
         *
         * http://lousodrome.net/blog/light/2017/01/03/intersection-of-a-ray-and-a-cone/
         *
         * O = ray.origin
         * D = ray.normDirection
         * V = unitVec
         * C = tipPos
         * X = tipAngle
         *
         * to be found:
         * t (ray.normVecMultiplier)
         * hitVec
         *
         * a = (D * V) ^ 2 - cos^2 * X
         * b = 2 * ((D * V)(C * O * V) - D * C * O * cos^2 * X)
         * c = (C * O * V)^2 - CO * C * O cos^2 * X
         *
         * hitVec = O + t * D
         */

        double aToSquare = dotProduct(ray.normDirection, unitVec);
        double a = aToSquare * aToSquare - Math.pow(Math.cos(tipAngle), 2.0);

        double b = 2.0 * dotProduct(ray.normDirection, unitVec) * dotProduct(multiply(tipPos, ray.origin), unitVec) - dotProduct(ray.normDirection, multiply(tipPos, ray.origin)) * Math.pow(Math.cos(tipAngle), 2);

        double cToSquare = dotProduct(tipPos , ray.origin);
        double c = Math.pow(dotProduct(multiply(tipPos, ray.origin), unitVec), 2) - cToSquare * cToSquare * Math.pow(Math.cos(tipAngle), 2);

        /**
         * calc determinant Δ = b2 - 4ac
         *
         * If Δ < 0Δ < 0, the ray is not intersecting the cone.
         * If Δ = 0Δ = 0, the ray is intersecting the cone once at t=−b2at=−b2a.
         * If Δ > 0Δ > 0, the ray is intersecting the cone twice, at t1 = -b - √Δ / (2 * a) and t2 = -b + √Δ / (2 * a)
         */

        double determinant = (b * b) - (4 * a * c);

        Vec3 hitVec;
        double t;
        double t1;
        double t2;
        if (determinant == 0) {
            t = -b / (2 * a);
            if (t > ray.min && t < ray.max) {
                hitVec = ray.pointAt(t);
                Hit hit = new Hit(t, hitVec, normalize(hitVec), material);
                return hit;
            }
        } else if (determinant > 0) {
            t1 = (-b - Math.sqrt(determinant)) / (2 * a);
            t2 = (-b + Math.sqrt(determinant)) / (2 * a);
            if (t1 <= t2) {
                if (t1 > ray.min && t1 < ray.max) {
                    hitVec = ray.pointAt(t1);
                    Hit hit = new Hit(t1, hitVec, normalize(hitVec), material);
                    return hit;
                } else {
                    return null;
                }
            } else {
                if (t2 > ray.min && t2 < ray.max) {
                    hitVec = ray.pointAt(t2);
                    //if (tipAngle < 90 || dotProduct(subtract(hitVec, tipPos), unitVec) > 0) {
                    Hit hit = new Hit(t2, hitVec, normalize(hitVec), material);
                    return hit;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }

        return null;
    }
}
