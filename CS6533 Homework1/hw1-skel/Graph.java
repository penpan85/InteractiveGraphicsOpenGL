import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* class Graph
 * Creates a OpenGL window with a trivial Swing interface
 * 
 * Doug DeCarlo 9/04
 * Xiaofeng Mi  9/06  (revised for JOGL)
 */

public class Graph extends JFrame
{
    public Graph(final double a, final double b, final double delta,
                 final double speed,
                 final boolean fancy, final boolean moving)
    {
        // --- Drawing area
        final DrawGraph graph = new DrawGraph(this, a, b, delta, speed,
                                              fancy, moving);
        
        // --- Menubar

        final JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        final JMenu menu = new JMenu("File");
        // Ensure popup menu is drawn in front of OpenGL window
        // (by not using a lightweight window for the menu)
        menu.getPopupMenu().setLightWeightPopupEnabled(false);
        menubar.add(menu);

        // Exit when quit selected
        final JMenuItem quitm = menu.add("Quit");
        quitm.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(final ActionEvent e)
                {
                    System.exit(0);
                }
            });
        
        // Exit when window closes
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(final WindowEvent e)
                {
                    System.exit(0);
                }
            });

        // ------------------------------------------------------

        // Lay out main window
        final Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(graph, BorderLayout.CENTER);
        pack();
        setVisible(true);

        // Placement of window on screen
        setLocation(100, 50);
        this.setSize(350, 380);

        // Start animation
        graph.setAnimation(true);
    }
    
    public static void main(final String args[])
    {
        boolean fancy = false, moving = false;
        double a = 5, b = 4, delta = 0, speed = 1;

        // Parse command-line arguments
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-help")) {
                    System.out.println("Usage: java Graph " + 
                                       "[-fancy] [-a #] [-b #] [-delta #] " +
                                       "[-speed #]");
                } else if (args[i].equals("-fancy")) {
                    fancy = true;
                } else if (args[i].equals("-moving")) {
                    moving = true;
                } else if (args[i].equals("-a") && i+1 < args.length) {
                    a = (new Double(args[++i])).floatValue();
                } else if (args[i].equals("-b") && i+1 < args.length) {
                    b = (new Double(args[++i])).floatValue();
                } else if (args[i].equals("-delta") && i+1 < args.length) {
                    delta = (new Double(args[++i])).floatValue();
                } else if (args[i].equals("-speed") && i+1 < args.length) {
                    speed = (new Double(args[++i])).floatValue();
                } else {
                    throw new Exception("Illegal argument: " + args[i]);
                }
            }
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        // Create main window
        try {
            final Graph g = new Graph(a, b, delta, speed, fancy, moving);
            g.setVisible(true);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}    
