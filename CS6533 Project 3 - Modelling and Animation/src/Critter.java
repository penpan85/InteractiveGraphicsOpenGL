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


    	Vector3d newVel = new Vector3d();
    	newVel.x = vel.x + acc.x * dt;
    	newVel.y = vel.y + acc.y * dt;
    	newVel.z = vel.z + acc.z * dt;
    	
    	Point3d newPos = new Point3d();
    	newPos.x = pos.x + vel.x * dt;
    	newPos.y = pos.y + vel.y * dt;
    	newPos.z = pos.z + vel.z * dt;
    	
	// Update distance

        dist += Math.abs(pos.distance(newPos));
        
        vel.x = newVel.x;
        vel.y = newVel.y;
        vel.z = newVel.z;
        
        pos.x = newPos.x;
        pos.y = newPos.y;
        pos.z = newPos.z;
        
        newVel = null;
        newPos = null;
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
    	acc.x += -k * vel.x;
    	acc.y += -k * vel.y;
    	acc.z += -k * vel.z;
    }

    // Add in attraction acceleration:  a+= direction * (k*dist^exp)
    // (negative values of k produce repulsion)
    public void accelAttract(Point3d p, double k, double exp)
    {
    	double kdistexp = k * Math.pow(pos.distance(p), exp);

    	// direction will change based on attraction / repulsion
    	Vector3d direction = new Vector3d();
		direction.x = p.x;
		direction.y = p.y;
		direction.z = p.z;
		direction.sub(pos);

    	direction.normalize();
    	
    	acc.x += direction.x * kdistexp ;
    	acc.y += direction.y * kdistexp ;
    	acc.z += direction.z * kdistexp ;
    	
    	direction = null;
    }

    // ...   (add more methods like those above when you need them)
}
