/* class Scene
 * Methods to describe, process and display the scene which contains
 * rocks, trees and critters.
 *
 * Doug DeCarlo
 */

import java.util.*;
import java.text.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

import com.sun.opengl.util.GLUT;

public class Scene
{
    // Parameters for specifying V; the 3D view
    private Vector<DoubleParameter> params;
    private DoubleParameter tH, tV, tZ, rAlt, rAzim;

    // Parameters for display options
    private Vector<BooleanParameter> options;
    private BooleanParameter drawTime;
    public  BooleanParameter drawAnimation, drawBugView;
    
    private Point3d goal;
    private int target;

    // ------------

    // Elements of the scene
    Vector<Critter> critters;
    Vector<Obstacle> obstacles;
    
    // Main character in scene (a reference to a bug stored in critters) */
    Bug mainBug;
    
    Critter predator;
    
    // Random number generator
    Random rgen;

    // Clock reading at last computation
    double computeClock = 0;
    
    // Clock reading at last pick of goal
    double goalPickClock = 0;

    // Seed for random number generator
    long seed;

    // Rendering quality flag
    boolean nice;

    // Speed multiplier for clock (1.0 is default)
    double clockSpeed;

    // File prefix used for file dumping (null if not dumping images)
    String dumpPrefix;

    // Center of the world
    Point3d origin = new Point3d(0,0,0);
    
    

    //-----------------------------------------------------------------------

    // Default constructor for scene
    public Scene(long seedVal, boolean niceVal, double clockSpeedVal, 
                 String dumpPrefixVal)
    {
        seed = seedVal;
        nice = niceVal;
        clockSpeed = clockSpeedVal;
        dumpPrefix = dumpPrefixVal;

        // Allocate parameters
        params = new Vector<DoubleParameter>();
        options = new Vector<BooleanParameter>();

        tH = addParameter(new DoubleParameter("Left/Right", 0, -10, 10, 1));
        tV = addParameter(new DoubleParameter("Down/Up",   -2, -10, 10, 1));
        tZ = addParameter(new DoubleParameter("Out/In",     0, -30, 20, 1));

        rAlt  = addParameter(new DoubleParameter("Altitude", 15, -15, 80, 1));
        rAzim = addParameter(new DoubleParameter("Azimuth", 0, -180, 180, 1));

        drawAnimation = addOption(new BooleanParameter("Animation", 
                                                       false, 2));
        drawTime      = addOption(new BooleanParameter("Show time",
                                                       dumpPrefix == null, 
                                                       1));
        drawBugView   = addOption(new BooleanParameter("Bug camera view", 
                                                       false, 1));
        
        //goal = new Point3d(rgen.nextDouble() * 5, rgen.nextDouble() * 5, 0);

        build();
    }

    // ----------------------------------------------------------------------

    // Keep track of list of all scene parameters/drawing options
    public DoubleParameter addParameter(DoubleParameter p)
    {
        params.add(p);
        return p;
    }
    public BooleanParameter addOption(BooleanParameter p)
    {
        options.add(p);
        return p;
    }

    // Accessors for parameters/options
    public Vector getParams()
    {
        return params;
    }
    public Vector getOptions()
    {
        return options;
    }

    // Reset all shape parameters to default values
    public void reset()
    {
        Iterator i;

        i = params.iterator(); 
        while (i.hasNext()) {
            ((Parameter)i.next()).reset();
        }

        i = options.iterator(); 
        while (i.hasNext()) {
            ((Parameter)i.next()).reset();
        }
    }

    // -----------------------------------------------------------------
    // -- Clock stuff

    // Starting time of program, and time of latest pause
    public long startTime, pauseTime;

    // Flag for determining if clock always reports 1/30 second
    // intervals each time it is polled
    public boolean frameByFrameClock = false;

    // Frame number (for frame-by-frame clock)
    private int frameNumber = 0;

    // Get current frame number
    public int getFrameNumber()
    {
	return frameNumber;
    }

    // Go to next frame number
    public void incrementFrameNumber()
    {
	frameNumber++;
    }

    // Make clock frame-by-frame (each frame has 1/30 second duration)
    public void setFrameByFrameClock()
    {
	frameByFrameClock = true;
    }

    // Record starting time of program and frame number
    public void resetClock()
    {
	startTime = System.currentTimeMillis();
	pauseTime = startTime;

        computeClock = 0;

	frameNumber = 0;
    }

    // Pause clock (time doesn't elapse)
    public void pauseClock(boolean stop)
    {
	long now = System.currentTimeMillis();

	if (stop) {
	    pauseTime = now;
	} else {
	    startTime += now - pauseTime;
	}
    }

    // Return current time (in seconds) since program started
    // (or if frameByFrameClock is true, then return time of current
    // frame number)
    public double readClock()
    {
	if (frameByFrameClock) {
	    return frameNumber * (1/30.0f);
	} else {
	    long elapsed;
	    
	    if (drawAnimation.value) {
		// Time during animation
		elapsed = System.currentTimeMillis() - startTime;
	    } else {
		// Time during pause
		elapsed = pauseTime - startTime;
	    }
	    
	    return elapsed / 1000.0f;
	}
    }

    // ----------------------------------------------------------------------

    // Build the contents of the scene
    // (no OpenGL calls are allowed in here, as it hasn't been
    //  initialized yet)
    public void build()
    {
		Point3d loc;
	
		computeFPS(0);
	
		// Make random number generator
		if (seed == -1) {
		    seed = System.currentTimeMillis() % 10000;
		    System.out.println("Seed value: " + seed);
		}
		rgen = new Random(seed);
	
		// Create empty scene
		obstacles = new Vector<Obstacle>();
		critters = new Vector<Critter>();
	
		// ---------------

        // Create a couple of trees, one big and one smaller
        obstacles.addElement(new Tree(rgen, 5, 6, 4.5f, 0.4f, 0.0f, 0.0f));
        obstacles.addElement(new Tree(rgen, 4, 6, 2.5f, 0.15f, 8.0f, -5.0f));

        // Create a few rocks
        obstacles.addElement(new Rock(rgen, 4, 2, 2, 1));
        obstacles.addElement(new Rock(rgen, 3, 4, 8, 1));
        obstacles.addElement(new Rock(rgen, 3, 7, 2, 2));
        obstacles.addElement(new Rock(rgen, 2, -2.6, -9, 2));
        obstacles.addElement(new Rock(rgen, 4, -2, -3, 1));
        obstacles.addElement(new Rock(rgen, 3, -2, -1.11, 1));
        
        // Create the main bug
        mainBug = new Bug(rgen, 0.4f,  -1, 1,  0.1f, 0.0f);
        critters.addElement(mainBug);
        
        // baby critters
        critters.addElement(new Bug(rgen, 0.1f, -5, 6, 0.1, 0.0f));
        critters.addElement(new Bug(rgen, 0.15f, -7, -6, 0.1, 0.0f));
        critters.addElement(new Bug(rgen, 0.2f, -8, 1, 0.1, 0.0f));
        
        //predator
        predator = new Predator(rgen, 0.3f, -9, 9, 0, 0);
        critters.addElement(predator);
        
        
        goal = new Point3d(rgen.nextDouble()*5, rgen.nextDouble()*5, 0);
        target = 0;

		// Reset computation clock
		computeClock = 0;
    }

    // Perform computation for critter movement so they are updated to
    // the current time
    public void process()
    {
        // Get current time
		double t = readClock() * clockSpeed;
		double dTime = t - computeClock;
		double dtMax = 1/50.0f;
	
		// Set current time on display
		computeClock = t;
		

        // Only process if time has elapsed
        if (dTime <= 0)
          return;


		// ---------------
	
		// Compute accelerations, then integrate (using Critter methods)
	
	        // This part advances the simulation forward by dTime seconds, but
	        // using steps that are no larger than dtMax (this means it takes
	        // more than one step when dTime > dtMax -- the number of steps
	        // you need is stored in numSteps).
	
	        // *** placeholder value
	        int numSteps = (int) Math.floor(dTime / dtMax);
	        
	        // compute a new 'goal' point for the mainbug to attract toward - every 7 seconds
	        if ((computeClock) - (goalPickClock) > 7)
	        {
	        	// pick a new random goal point
	        	goal = new Point3d(rgen.nextDouble()*16-8, rgen.nextDouble()*16-8, 0);
	        	goalPickClock = computeClock;
      	
	        	target = (int)rgen.nextDouble()*(critters.size()-2);
	        	while (critters.get(target) == predator || critters.get(target) == mainBug)
	        		target = (int)(rgen.nextDouble()*(critters.size()-1));
	        }
	        
	        // Here is the rough structure of what you'll need
	        //
		// numSteps = how many steps to take for stable integration
	        // do numSteps times
	        //   - reset acceleration
	        //   - compute acceleration (adding up accelerations from attractions,
	        //     repulsions, drag, ...)
	        //   - integrate (by dTime/numSteps)
	        // end
	        //
	        // ...
	        for (int i = 0; i < numSteps; i++)
	        {
	        	for (int k = 0; k < critters.size(); k++)
	        	{
	        		Critter tmp = critters.get(k);
	        		tmp.accelReset();
	        		tmp.accelDrag(0.6); // dampening
	        		

	        		
	        		if (tmp == mainBug)// big bug follows randomized 'goal' point (food? water? shelter? )
	        		{
	        			tmp.accelAttract(goal, 0.1, 1);
	        			tmp.accelAttract(predator.getLocation(), -rgen.nextDouble()*0.3, -2);
	        			tmp.accelAttract(origin, 0.25, 1);	        			 // attraction toward center of scene
	        		}
	        		else if (tmp == predator )
	        		{
      					tmp.accelAttract(critters.get(target).getLocation(), rgen.nextDouble()*0.4, 2);	        					
	        		}
	        		else 
	        		{
	        			// baby bugs follow big bug and are repelled from other baby bugs and predator!
	        			tmp.accelAttract(mainBug.getLocation(), rgen.nextDouble()*0.3, 1);
	        			tmp.accelAttract(predator.getLocation(), -rgen.nextDouble()*0.45, -3);
	        			for (int m = 0; m < critters.size(); m++)
	        			{
	        				if (critters.get(m) != mainBug && critters.get(m) != tmp)
	        					tmp.accelAttract(critters.get(m).getLocation(), -rgen.nextDouble()*0.5, -1);	        					
	        			}
	        		}
	
		        	// repulsion away from obstacles
		        	for (int j = 0; j < obstacles.size(); j++)
		        	{
		        		tmp.accelAttract(obstacles.get(j).getLocation(), -0.5, -2);
		        	}
		        	
		        	tmp.integrate(dTime/numSteps);
	        	}
	        }
	
	        // Keyframe motion for each critter
	        for (int i = 0; i < critters.size(); i++)
	        {
	        	critters.get(i).keyframe(critters.get(i).dist);
	        }
    }

    // Draw scene
    public void draw(GL gl, GLUT glut)
    {
        // Light position
        float lt_posit[] = { 10, 5, 30, 0 };
        // Ground plane (for clipping)
        double ground[]  = { 0.0, 0.0, 1.0, 0.0 };
	
        // Do computation if animating
        if (drawAnimation.value) {
            process();
        }
	
        // ------------------------------------------------------------
	
        // Initialize materials
        materialSetup(gl);
	
        // Specify V for scene
        gl.glLoadIdentity();
        transformation(gl);
	
        // Position light wrt camera
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lt_posit, 0);
        gl.glEnable(GL.GL_LIGHTING);
	
        // Draw ground plane (a circle at z=0 of radius 15)
        gl.glColor3d(0.4, 0.6, 0.35);
        gl.glBegin(GL.GL_POLYGON);
        gl.glNormal3d(0, 0, 1);
        int ncirc = 200;
        for (int i = 0; i < ncirc; i++) {
            double theta = 2*Math.PI * i / ncirc;
            gl.glVertex3d(15*Math.cos(theta), 15*Math.sin(theta), 0);
        }
        gl.glEnd();
	
        // Draw critters
        for (int i = 0; i < critters.size(); i++) {
            ((Critter)(critters.elementAt(i))).draw(gl);
        }
	
        // Clip below ground (so rocks don't peek below ground)
        gl.glClipPlane(GL.GL_CLIP_PLANE0, ground, 0);

        // **** Once you get the rock working, enable this -- it can be
        //      difficult to debug the rock when this is on, as you can only
        //      see the top of it -- this way you'll see the entire rock if
        //      you peek below the ground plane...
        gl.glEnable(GL.GL_CLIP_PLANE0);
	
        // Draw obstacles
        for (int i = 0; i < obstacles.size(); i++) {
            ((Obstacle)(obstacles.elementAt(i))).draw(gl);
        }
        gl.glDisable(GL.GL_CLIP_PLANE0);
	
        // Draw text on top of display showing time
        if (drawTime.value) {
            drawText(gl, glut, computeClock / clockSpeed);
        } else {
            numPrevT = 0;
        }
    }
    
    // Transformation of scene based on GUI values
    // (also transform scene so Z is up, X is forward)
    private void transformation(GL gl)
    {
		// Make X axis face forward, Y right, Z up
		// (map ZXY to XYZ)
		gl.glRotated(-90, 1, 0, 0);
		gl.glRotated(-90, 0, 0, 1);
	            
		if (drawBugView.value) {
		    // ---- "Bug cam" transformation (for mainBug)
			// this is the inverse of the M transformation that places the bug in the scene (based on mainbug pos and vel)
            double angle = Math.atan2(mainBug.vel.y, mainBug.vel.x);
    		gl.glRotated(Math.toDegrees(Math.PI - angle), 0, 0, 1);
    		
    		//drawing a bit above the z axis so we can see the ground
			gl.glTranslated(-mainBug.pos.x, -mainBug.pos.y, -mainBug.pos.z-0.2);
		} else {
		    // ---- Ordinary scene transformation
	
		    // Move camera back so that scene is visible
		    gl.glTranslated(-20, 0, 0);
		    
		    // Translate by Zoom/Horiz/Vert
		    gl.glTranslated(tZ.value, tH.value, tV.value);
		    
		    // Rotate by Alt/Azim
		    gl.glRotated(rAlt.value,  0, 1, 0);
		    gl.glRotated(rAzim.value, 0, 0, 1);
		}
    }

    // Define materials and lights
    private void materialSetup(GL gl)
    {
	float white[]  = {   1.0f,   1.0f,   1.0f, 1.0f };
	float black[]  = {   0.0f,   0.0f,   0.0f, 1.0f };
	float dim[]    = {   0.1f,   0.1f,   0.1f, 1.0f };
	
	// Set up material and light
	gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT,  dim, 0);
	gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE,  white, 0);
	gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, dim, 0);
	gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, 5);

	// Set light color
 	gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, dim, 0);
 	gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, white, 0);
 	gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, black, 0);

	// Turn on light and lighting
	gl.glEnable(GL.GL_LIGHT0);
	gl.glEnable(GL.GL_LIGHTING);

	// Allow glColor() to affect current diffuse material
	gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);
	gl.glEnable(GL.GL_COLOR_MATERIAL);
    }

    // Draw text info on display
    private void drawText(GL gl, GLUT glut, double t)
    {
	String message;
	DecimalFormat twodigit = new DecimalFormat("00");

	// Put orthographic matrix on projection stack
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glPushMatrix();
	gl.glLoadIdentity();
	gl.glOrtho(0, 1, 0, 1, -1, 1);
	gl.glMatrixMode(GL.GL_MODELVIEW);

	// Form text
	message = new String((int)t/60 + ":" + 
			     twodigit.format((int)t % 60) + "." +
			     twodigit.format((int)(100 * (t - (int)t))));

	// Add on frame rate to message if it has a valid value
	double fps = computeFPS(t);
	if (fps != 0) {
	    DecimalFormat fpsFormat = new DecimalFormat("0.0");

	    message = message + "  (" + fpsFormat.format(fps) + " fps)";

            fpsFormat = null;
	}

	gl.glDisable(GL.GL_LIGHTING);
	gl.glDisable(GL.GL_DEPTH_TEST);

	gl.glPushMatrix();
	gl.glLoadIdentity();

	// Draw text 
	gl.glColor3d(0.8, 0.2, 0.2);
	gl.glRasterPos2d(0.01, 0.01);
        glut.glutBitmapString(glut.BITMAP_HELVETICA_18, message);
        message = null;

	// Draw bug cam label 
	if (drawBugView.value) {
	    message = new String("BUG CAM");
	    gl.glRasterPos2d(0.45, 0.01);
  	    gl.glColor3d(1.0, 1.0, 1.0);
            glut.glutBitmapString(glut.BITMAP_HELVETICA_18, message);
            message = null;
	}
	
	glut.glutBitmapString(glut.BITMAP_HELVETICA_10, new String(goal.toString()));
	message = new String(Double.toString(mainBug.dist));
	glut.glutBitmapString(glut.BITMAP_HELVETICA_10, message);

	gl.glPopMatrix();

	gl.glEnable(GL.GL_DEPTH_TEST);

	// Put back original viewing matrix
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glPopMatrix();
	gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // ----------------------------------------------------------------------

    // Compute average frame rate (0.0 indicates not computed yet)
    private double[] prevT = new double[10];
    private int numPrevT = 0;

    private double computeFPS(double t)
    {
	// Restart average when animation stops
	if (t == 0 || !drawAnimation.value) {
	    numPrevT = 0;
	    return 0;
	}

	int which = numPrevT % prevT.length;
	double tdiff = t - prevT[which];

	prevT[which] = t;
	numPrevT++;

	// Only compute frame rate when valid
	if (numPrevT <= prevT.length || tdiff <= 0) {
	    return 0;
	}

	return prevT.length / tdiff;
    }
}
