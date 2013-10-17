import javax.media.opengl.*;

import java.awt.Window;
import java.lang.Math;

/* class DrawGraph
 * Draws the graph using OpenGL
 *
 * Doug DeCarlo
 */
public class DrawGraph extends SimpleGLCanvas
{
    // Flag specifying whether the graph should be drawn in various colors,
    // or if the curve is moving (for extra credit)
    boolean fancy, moving;

    // Parameters for lissajous figure
    double a, b, delta;

    // Animated drawing speed
    double speed;
    
    // -----------------------------------

    // Constructor
    public DrawGraph(Window parent, double aVal, double bVal, double deltaVal,
                     double speedVal, boolean fancyVal, boolean movingVal)
    {
        super(parent);
        // Lissajous curve parameters
        a = aVal;
        b = bVal;
        delta = deltaVal;

        // Drawing style
        speed = speedVal;
        fancy = fancyVal;
        moving = movingVal;
    }

    // Method for initializing OpenGL (called once at the beginning)
    public void init(GL gl)
    {
        // Set background color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    // Method for handling window resizing
    public void projection(GL gl, int width, int height)
    {
        // Set drawing area
        gl.glViewport(0, 0, width, height);
        
        // Set up orthographic projection with
        // window coordinates slightly larger than [-1,1]x[-1,1]
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1.01, 1.01, -1.01, 1.01, -1.0, 1.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // Method for drawing the contents of the window
    public void draw(GL gl)
    {
        double currentTime = readClock();
        
        // Clear the window
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        double t = currentTime; //starting point of domain
        double tEnd = (speed * currentTime) + Math.PI; //end point of domain
        double stepSize = 0.001; 
                
        gl.glBegin(GL.GL_LINES);
    	
    	// Draw loop for each t in interval [t, t+pi] with configurable step size
    	// [Extra Credit #1]: Use speed parameter to increase drawing speed
    	while (t < tEnd)
    	{
    		float intensity = (float)(1 / (tEnd - t)); // 0...1 value representing how close we are to domain end
   		
    		// [Extra Credit #3]: Set intensity based on 3 distinct functions that all converge at varying speeds to 1 as intensity -> 1
    		if (fancy)
    		{
    			gl.glColor3d(Math.pow(intensity, 2d), Math.pow(intensity, 3d), Math.sin(intensity + (Math.PI / 2.0f) - 1));
    		}
    		else
    		{
    			gl.glColor3d(intensity, intensity, intensity); //shades of grey
    		}
    			
    		gl.glVertex2d(
    				Math.sin(a * t + delta), 
    				Math.sin(b * t)
    		);
    		t = t + stepSize;
    	}
    	gl.glEnd();
    	
        
        // [Extra Credit #2]: Slowly alter the shape of the curve over time by increasing the delta variable 
    	// this will give the appearance that the graph is moving
        if (moving) 
        	delta += 0.0001;

    }
}
