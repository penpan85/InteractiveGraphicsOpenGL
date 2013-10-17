import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.vecmath.*;


public class Predator extends Critter {

	 // Number of legs, and number of parameters per leg
    final int legNum = 6, pNum = 2;

    // Keyframes for bug:
    // - this array specifies one cycle of motion for the bug, which
    //   lasts from T=0 to T=1.
    // - T isn't the actual time, but rather is just a measure of
    //   progress through a single cycle
    // - keyFrames[j] is a vector of angles that describes the bug's
    //   configuration at the time keyT[j].
    static double[][] keyFrames = {
		{
		    30, 10,      30, 30, 
		    0,  30,       0, 10, 
		   -30, 10,     -30, 30, 	
		},
		{
		    20, 10,      40, 30, 
		    10, 30,     -10, 10, 
		   -40, 10,     -20, 30, 	
		},
		{
		    30, 30,      30, 10, 
		    0,  10,       0, 30, 
		   -30, 30,     -30, 10, 	
		},
		{
		    40, 30,      20, 10, 
		   -10, 10,      10, 30, 
		   -20, 30,     -40, 10, 	
		},
		{ // copy of first keyFrame -- cyclic motion
		    30, 10,      30, 30, 
		    0,  30,       0, 10, 
		   -30, 10,     -30, 30,	
		}
    };
    // "Timestamps" for keyframes, in [0,1]
    static double [] keyT = {
    	0.0, 0.25, 0.5, 0.75, 1.0
    };
    // Distance traveled in one T-second of keyframes for bug
    // which has scale=1
    // (used to make leg speed match bug speed)
    static double stride = 1.3;

    // --------------------------------------------------------------

    // Current bug parameters/leg angles (computed from keyframes for
    // each moment in time) using keyframe(t) method
    double[] param;

    // Bug size
    double scale;

    // Tesselation resolution of bug parts 
    static int partDetail;

    // ---------------------------------------------------------------

    // constructor
    public Predator(Random randomGen, double bugScale,
	       double bugPx, double bugPy, double bugVx, double bugVy)
    {
		super(randomGen);
	
		pos.set(bugPx, bugPy, 0);
		vel.set(bugVx, bugVy, 0);
		scale = bugScale;
	
		param = new double[keyFrames[0].length];
		keyframe(0);
    }

    // ---------------------------------------------------------------

    // Compute bug parameters by keyframing
    // Given t, compute the corresponding value of param[]
    public void keyframe(double t)
    {
    	// -- Find two nearest keyframes
    	double T = (t/(stride*scale))%1.0;
    	int T1 = findT1(T);
    	int T2 = T1+1;
    	if (T2 >= keyT.length)
    	{
    		T2 = 1;
    	}
    	linear_interpolation(T1, T2, T);
    }
    
    //assuming keyT is ordered by time ascending
    private int findT1(double t)
    {
    	for (int i=0; i < keyT.length; i++)
    	{
    		if (t <= keyT[i] && i > 0)
    			return i-1;
    	}
    	return 0;
    }
    
    private void linear_interpolation(int T1, int T2, double t)
    {
    	for (int i = 0; i < keyFrames[0].length; i++)
    	{
    		double y0 = keyFrames[T1][i];
    		double y1 = keyFrames[T2][i];
    		double x0 = keyT[T1];
    		double x1 = keyT[T2];
    		
    		param[i] = y0 + (y1-y0)*(t-x0)/(x1-x0);
    	}
    }

    // --------------------------------------------------------------------

    // Transformation to place bug in scene
    public void transform(GL gl)
    {
		gl.glTranslated(pos.x, pos.y, pos.z);
		
		double angle = Math.atan2(vel.y, vel.x);
		gl.glRotated(Math.toDegrees(angle), 0, 0, 1);
	
		gl.glScaled(scale, scale, scale);

    }

    // ---------------------------------------------------------------
    // Draw bug in scene using current set of parameters
    public void draw(GL gl)
    {
		// Bug transform (default bug faces +x direction)
		gl.glPushMatrix();
		transform(gl);
	
		// Body
		gl.glPushMatrix();
		{
		    gl.glTranslated(0, 0, 0.75);
	            gl.glColor3d(0.2, 0.2, 0.2);
		    gl.glPushMatrix();
		    {
			gl.glScaled(1.3, 1.1, 1);
	                Objs.sphere(gl);
		    }
		    gl.glPopMatrix();
		    
		    // Head (relative to body)
		    gl.glPushMatrix();
		    {
			gl.glTranslated(0.7, 0.0, 0.0);
			gl.glScaled(0.7, 0.5, 0.7);
	                gl.glColor3d(0.21, 0.22, 0.23);
	                Objs.sphere(gl);
		    }
		    gl.glPopMatrix();
	
		    // Legs (relative to body)
	
		    double legThick = 0.05;
	            gl.glColor3d(0.95, 0.1, 0.1);
	
		    for (int i = 0; i < legNum/2; i++) {
			// Left legs
			gl.glPushMatrix();
			{
			    gl.glRotated(param[2*i*pNum],      0, 0, 1);
			    gl.glRotated(90-param[2*i*pNum+1], 1, 0, 0);
			    
			    gl.glPushMatrix();
			    {
				gl.glScaled(legThick, legThick, 1.0);
	                        Objs.cylinder(gl);
			    }
			    gl.glPopMatrix();
	
			    gl.glTranslated(0, 0, 1);
			    gl.glRotated(90, 1, 0, 0);
			    gl.glScaled(legThick, legThick, 1.0);
	                    Objs.cylinder(gl);
			}
			gl.glPopMatrix();
	
			// Right legs	    
			gl.glPushMatrix();
			{
			    gl.glRotated(-param[(2*i+1)*pNum],      0, 0, 1);
			    gl.glRotated(-90+param[(2*i+1)*pNum+1], 1, 0, 0);
			    
			    gl.glPushMatrix();
			    {
				gl.glScaled(legThick, legThick, 1.0);
	                        Objs.cylinder(gl);
			    }
			    gl.glPopMatrix();
			    
			    gl.glTranslated(0, 0, 1.0);
			    gl.glRotated(-90, 1, 0, 0);
			    gl.glScaled(legThick, legThick, 1.0);
	                    Objs.cylinder(gl);
			}
			
			gl.glPopMatrix();
		    }
		}
	
		// Body
		gl.glPopMatrix();
	
		// Bug
		gl.glPopMatrix();
		
		//set back the color
		gl.glColor3d(0.5, 0.4, 0.3);
    }

}
