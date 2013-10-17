/* class Critter
 * This abstract class implements methods for keeping track of the position,
 * velocity and acceleration of a critter (such as a bug), for integrating
 * these quantities over time, and for computing accelerations that give
 * the bug wandering behavior
 *
 * Doug DeCarlo
 */

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;
import java.util.*;

abstract class Critter
{
    // Position, velocity, acceleration
    Point3d pos;
    Vector3d vel, acc;

    // Total distance traveled (used for keyframing)
    double dist;

    // Random number generator
    Random rgen;

    // ---------------------------------------------------------------

    // Constructor
    public Critter(Random randomGen)
    {
	pos = new Point3d();
	vel = new Vector3d();
	acc = new Vector3d();

	dist = 0;

	rgen = randomGen;
    }

    // Method to draw critter
    abstract void draw(GL gl);

    // Method to do keyframe animation
    abstract void keyframe(double t);

    // ---------------------------------------------------------------

    // Return location of critter
    public Point3d getLocation()
    {
	return pos;
    }

    // Method to integrate acc to get updated vel and pos;
    // also computes the distance traveled
    // (assumes acc is already computed)
    public void integrate(double dt)
    {
	// Euler integration

	// ...

	// Update distance

        // ...
    }

    // Accessor for total distance traveled by bug
    public double distTraveled()
    {
	return dist;
    }

    // ---------------------------------------------------------------

    // Reset acceleration to zero
    public void accelReset()
    {
	acc.set(0,0,0);
    }

    // Add in viscous drag (assume mass of 1):  a += -k v   (k > 0)
    public void accelDrag(double k)
    {
        // Add viscous drag to acceleration acc

        // ...
    }

    // Add in attraction acceleration:  a+= direction * (k*dist^exp)
    // (negative values of k produce repulsion)
    public void accelAttract(Point3d p, double k, double exp)
    {
        // ...
    }

    // ...   (add more methods like those above when you need them)
}
