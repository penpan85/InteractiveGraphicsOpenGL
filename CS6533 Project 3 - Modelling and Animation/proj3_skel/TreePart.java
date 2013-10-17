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

    // Leaf or trunk
    boolean leaf;

    // Size of part
    double length, width;

    // Children
    TreePart[] parts;

    // ---------------------------------------------------------------

    // Constructor: recursively construct a treepart of a particular depth,
    // with specified branching factor, dimensions and transformation
    public TreePart(Random rgen,
		    int depth, int numBranch,
		    double partLen, double partWid
                    // ... transformation specification can go here
                    )
    {
        // **** placeholder -- remove this when you start working on the tree

        this.leaf = false;
        this.length = partLen;
        this.width = partWid;

        // **** end of placeholder


        // ... Create branch or leaf (based on depth) and create children
        // ... branches/leaves recursively
    }

    // Recursively draw a tree component
    //  - place the component using transformation for this subtree
    //  - draw leaf (if this is a leaf node)
    //  - draw subtree (if this is an interior node)
    //    (draw this component, recursively draw children)
    public void draw(GL gl)
    {
	gl.glPushMatrix();

	// Place this component
        // ...
        // ... (apply transformation for this component)
	
	if (leaf) {
            // Draw leaf

            // ...
	} else {
            // Draw branch

            // ... (transformation for cylinder)

            Objs.cylinder(gl);

	    // Recursively draw children
            // ...
	}

	gl.glPopMatrix();
    }
}
