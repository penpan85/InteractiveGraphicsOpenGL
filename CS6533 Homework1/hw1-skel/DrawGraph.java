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
        double time = readClock();

        // Clear the window
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // -- FILL IN:

    }
}
