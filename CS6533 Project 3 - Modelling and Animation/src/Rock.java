/* class Rock
 * Represents a rock using a rectangular grid; given a particular level
 * of subdivision l, the rock will be a 2^l+1 X 2^l+1 height field
 * The rock is drawn a little below the surface to get a rough edge.
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

class Rock implements Obstacle
{
    // Location of rock
    private double xpos, ypos, scale;

    // -- Rock mesh: a height-field of rsize X rsize vertices
    int rsize;
    // Height field: z values
    private double[][] height;
    // Whether height value has been set (locked) already
    private boolean[][] locked;

    // Random number generator
    Random rgen;

    // ---------------------------------------------------------------

    public Rock(Random randGen, int level, 
		double xPosition, double yPosition, double scaling)
    {
        // Grid size of (2^level + 1)
        rsize = (1 << level) + 1;

        // Height field -- initially all zeros
        height = new double[rsize][rsize];
        locked = new boolean[rsize][rsize];
 
        rgen = randGen;

		// Set rock position in the world
		xpos = xPosition;
		ypos = yPosition;
		scale = scaling;
	
		compute();
    }

    // ----------------------------------------------------------------
    // Obstacle methods

    // Get rock location (as a scene element)
    public Point3d getLocation()
    {
    	return new Point3d(xpos, ypos, 0);
    }

    // Draw rock in scene
    public void draw(GL gl)
    {
		gl.glPushMatrix();
	
	    // Translate rock down (so it has an interesting boundary)
		gl.glTranslated(xpos, ypos, -0.15);
	
		gl.glScaled(scale, scale, scale);
	
        gl.glColor3d(0.6, 0.6, 0.6);

        // Create these outside the loops, so objects persist and
        // unnecessary GC is avoided
        Point3d p = new Point3d();
        Vector3d n = new Vector3d();

        // Draw polygon grid of rock as quad-strips
        for (int i = 0; i < rsize-1; i++) {
            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int j = 0; j < rsize; j++) {
                getRockPoint(i, j, p);
                getRockNormal(i, j, n);
                gl.glNormal3d(n.x, n.y, n.z);
                gl.glVertex3d(p.x, p.y, p.z);
                
                getRockPoint(i+1, j, p);
                getRockNormal(i+1, j, n);
                gl.glNormal3d(n.x, n.y, n.z);
                gl.glVertex3d(p.x, p.y, p.z);
            }
            gl.glEnd();
        }

        // Make GC easy
        p = null;
        n = null;
	    
		gl.glPopMatrix();
    }
    
    // ---------------------------------------------------------------

    // Point (i,j) on the rock -- point p gets filled in
    public void getRockPoint(int i, int j, Point3d p)
    {
        // Rock (x,y) locations are on the grid [-0.5, 0.5] x [-0.5, 0.5]
        p.x = (double)i / (rsize-1) - 0.5;
        p.y = (double)j / (rsize-1) - 0.5;
        // Rock z comes from height field
        p.z = height[i][j];
    }

    // Normal vector (i,j) on the rock -- vector n gets filled in
    public void getRockNormal(int i, int j, Vector3d n)
    {
        // This is the formula for a normal vector of a height field with
        // regularly spaced x and y values (assuming rock is zero on
        // its borders and outside of it too)

        // X component is zleft - zright (respecting boundaries)
        n.x = height[(i == 0) ? i : i-1][j] - 
              height[(i == rsize-1) ? i : i+1][j];

        // Y component is zbottom - ztop (respecting boundaries)
        n.y = height[i][(j == 0) ? j : j-1] - 
              height[i][(j == rsize-1) ? j : j+1];

        // Z component is twice the separation
        n.z = 2 / (rsize-1);

        n.normalize();
    }

    // ---------------------------------------------------------------

    // Compute the geometry of the rock
    // (called when the rock is created)
    public void compute()
    {
		// Initialize mesh
		for (int i = 0; i < rsize; i++) {
	            for (int j = 0; j < rsize; j++) {
	                height[i][j] = 0;
	
	                // Lock sides...
	                locked[i][j] = (i == 0 || i == rsize-1 ||
	                                j == 0 || j == rsize-1);
	            }
		}
	
        // Raise the middle point and lock it there
        height[rsize/2][rsize/2] = 0.5;
        locked[rsize/2][rsize/2] = true;

        // Recursively compute fractal structure
		computeFractal(0, 0, rsize-1, rsize-1, 0.3);
    }

    // Recursively compute fractal rock geometry
    private void computeFractal(int x0, int y0, int x1, int y1, double var)
    {
        if ((x1-x0) <= 1)
        {
        	return;
        }
        
        int x_mid = (x0 + x1) / 2;
        int y_mid = (y0 + y1) / 2;
        double rand = Math.abs(rgen.nextGaussian() * var);
        //double rand = rgen.nextDouble() * var;

        //diamond step
        safeSet(x_mid, y_mid, (height[x0][y0] + height[x1][y0] + height[x0][y1] + height[x1][y1]) / 4.0 + rand);
        
        //square step
        safeSet(x0, y_mid, (height[x0][y0] + height[x_mid][y_mid] + height[x0][y1]) / 3.0 + rand);
        safeSet(x_mid, y0, (height[x0][y0] + height[x_mid][y_mid] + height[x1][y0]) / 3.0 + rand);
        safeSet(x1, y_mid, (height[x1][y0] + height[x_mid][y_mid] + height[x1][y1]) / 3.0 + rand);
        safeSet(x_mid, y1, (height[x0][y1] + height[x_mid][y_mid] + height[x1][y1]) / 3.0 + rand);

        computeFractal(x0, y0, x_mid, y_mid, var/2.0);
        computeFractal(x_mid, y0 , x1, y_mid, var/2.0);
        computeFractal(x0, y_mid, x_mid, y1, var/2.0);
        computeFractal(x_mid, y_mid, x1, y1, var/2.0);
    }
    
    private void safeSet(int x, int y, double val)
    {
    	if (!(locked[x][y]))
        	height[x][y] = val;   
    }
}
