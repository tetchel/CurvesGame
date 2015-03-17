package ca.etchells.curves;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;

public class CurvesApplet extends Applet implements KeyListener {

    private final int NUMBER_OF_PLAYERS = 4;
    private Curve[] curves;
    private javax.swing.Timer gameLoop;

    @Override
    public void init() {
        //basic set-up
        //IF CHANGING SIZE, CHANGE IN CURVES CLASS CONSTRUCTOR AS WELL
        setSize(new Dimension(1600, 900));

        setBackground(Color.BLACK);
        setFocusable(true);
        //applet window is contained in frame f
        Frame f = (Frame) this.getParent().getParent();
        f.setSize(getSize());
        f.setResizable(false);
        f.setTitle("~~~~~~~~~~~~~~~ Curves ~~~~~~~~~~~~~~~");

        //fill curves array with initialized curve objects
        curves = new Curve[NUMBER_OF_PLAYERS];
        for(int i = 0; i < curves.length; i++) {
            curves[i] = new Curve(i);
        }

        //add key bindings
        addKeyListener(this);

        //start a timer
        gameLoop = new javax.swing.Timer(17, new TimerListener());			//ticks 1000/17 = 60 FPS
        gameLoop.start();
    }

    //old listener
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                curves[0].adjustHeading(false);
                break;
            case KeyEvent.VK_RIGHT:
                curves[0].adjustHeading(true);
                break;
            case KeyEvent.VK_Q:
                curves[1].adjustHeading(false);
                break;
            case KeyEvent.VK_W:
                curves[1].adjustHeading(true);
                break;
            case KeyEvent.VK_V:
                curves[2].adjustHeading(false);
                break;
            case KeyEvent.VK_B:
                curves[2].adjustHeading(true);
                break;
            case KeyEvent.VK_I:
                curves[3].adjustHeading(false);
                break;
            case KeyEvent.VK_O:
                curves[3].adjustHeading(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void start() {
        //start the game loop
        gameLoop.start();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void destroy() {
        gameLoop.stop();
        super.destroy();
    }

    public class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            repaint();		//redraw the window
        }
    }

    /**
     * Contains all tasks that are performed on each "tick" of the clock -> updates positions etc.
     */
    @Override
    public void update(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        for(Curve c : curves) {
            //add collision detection
            c.advance();
            HashSet<Ellipse2D.Double> currentPath = c.getPath();
            g2.setPaint(c.getColor());
            for(Shape s : currentPath) {
                g2.fill(s);
            }
        }
    }
}
