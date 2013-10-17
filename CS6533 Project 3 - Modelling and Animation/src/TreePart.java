/* class TreePart
 * Class for representing a subtree, describing the size of the part at
 * the transformation to get to this subtree from the parent, the
 * current tree node (length and width) and whether this is a leaf node
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

class TreePart
{
    // Transformation for this branch/leaf (relative to its parent)

    // ...
    // ... specify angles and translations to say how parts of the
    // ... tree are positions with respect to each other
    // ...
	double _z;
	double _pitch, _roll, _yaw;

    // Leaf or trunk
    boolean leaf;

    // Size of part
    double length, width;

    // Children
    TreePart[] parts;
    
    // current depth
    int _depth;
    
    // maximum depth of tree
    int _maxdepth;
    
    int MAX_ANGLE = 80; 

    // ---------------------------------------------------------------

    // Constructor: recursively construct a treepart of a particular depth,
    // with specified branching factor, dimensions and transformation
    public TreePart(Random rgen,
		    int depth, int numBranch,
		    double partLen, double partWid, 
		    double z,
		    double pitch, double roll, double yaw, int maxdepth
                    )
    {
    	_depth = depth;
    	_maxdepth = maxdepth;
    	
        if (depth == 0)
        {
        	this.leaf = true;
        	this._z = z;
        	this._pitch = 25 * (rgen.nextDouble()-0.5)+90;
        	this._roll = 25 * (rgen.nextDouble()-0.5)+90;
        	this._yaw = 25 * (rgen.nextDouble()-0.5)+90;
        	this.length = 0.5;
        	this.width = 0.1;
        }
        else
        {
        	this.leaf = false;
        	this.length = partLen;
        	this.width = partWid;
        	this._z = z;
        	this._pitch = pitch;
        	this._roll = roll;
        	this._yaw = yaw;
        	
        	parts = new TreePart[numBranch];
        	for (int i = 0; i < numBranch; ++i)
        	{
        		double branchz = rgen.nextDouble()*(0.4*partLen) + 0.4*partLen;
        		
        		double branchpitch = MAX_ANGLE * (rgen.nextDouble()-0.5);
        		double branchroll = MAX_ANGLE * (rgen.nextDouble()-0.5);
        		double branchyaw = MAX_ANGLE * (rgen.nextDouble()-0.5);
        		
        		double randNumBranch = rgen.nextDouble() * (1.0 / Math.pow(_depth, 2)) + numBranch;
        		
        		double branchLen = partLen * (_depth / (double)_maxdepth);
        		branchLen = rgen.nextDouble() * (0.6*branchLen) + 0.6*branchLen;
        		double branchWid = partWid * (_depth / (double)_maxdepth);
        		branchWid = rgen.nextDouble() * (0.4*branchWid) + 0.4*branchWid;
        		
        		parts[i] = new TreePart(rgen, depth-1, (int)randNumBranch, branchLen, branchWid,
        				branchz, branchpitch, branchroll, branchyaw, maxdepth
        				);
        	}
        }
    }

    // Recursively draw a tree component
    //  - place the component using transformation for this subtree
    //  - draw leaf (if this is a leaf node)
    //  - draw subtree (if this is an interior node)
    //    (draw this component, recursively draw children)
    public void draw(GL gl)
    {
		gl.glPushMatrix();
	
			gl.glTranslated(0, 0, _z);
			gl.glRotated(_pitch, 1, 0, 0);
			gl.glRotated(_roll, 0, 1, 0);
			gl.glRotated(_yaw, 0, 0, 1);
			
			
			if (leaf) {
				gl.glPushMatrix();
					gl.glTranslated(width, width, 0);
					gl.glBegin(GL.GL_POLYGON);
					gl.glColor3f(0f, 1f, 0f);
					gl.glVertex3d(0.65*this.length,0,0);
					gl.glVertex3d(this.length/4.0,this.width/2.0,0);
					gl.glVertex3d(-1*this.length/4.0,this.width/2.0,0);
					gl.glVertex3d(-1*this.length/2.0,0,0);
					gl.glVertex3d(-1*this.length/4.0,-1*this.width/2.0,0);
					gl.glVertex3d(this.length/4.0,-1*this.width/2.0,0);
					gl.glColor3f(112/255f, 91/255f, 4/255f);
					gl.glEnd();
				gl.glPopMatrix();
			} else {
				gl.glPushMatrix();
					gl.glScaled(width, width, length);
					Objs.cylinder(gl);
	            gl.glPopMatrix();
	            
	            for (int i = 0; i < parts.length; ++i)
	            {
	            	parts[i].draw(gl);
	            }
			}
	
		gl.glPopMatrix();
    }
}
