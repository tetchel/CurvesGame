package ca.etchells.curves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;

public class CurvesPanel extends JPanel implements KeyListener {

    private Curve[] curves;
    private javax.swing.Timer gameLoop;
    private boolean start = false;
    private static final int    HEIGHT = 900,
                                WIDTH  = 1600;

    ///////////////////////////////APPLET methods///////////////////////////////
    public CurvesPanel() {
        //basic set-up
        //IF CHANGING SIZE, CHANGE IN CURVES CLASS CONSTRUCTOR AS WELL
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();

        //fill curves array with initialized curve objects - ONE PER PLAYER, EDIT THIS NUMBER FOR FEWER PLAYERS
        //but be aware <4 players will result in exceptions being thrown for now.
        curves = new Curve[4];
        for(int i = 0; i < curves.length; i++) {
            curves[i] = new Curve(i);
        }

        //add key bindings
        addKeyListener(this);
        //start a timer
        gameLoop = new javax.swing.Timer(17, new TimerListener());			//ticks 1000/17 = 60 FPS
        //gameLoop.start();
    }

    ///////////////////////////////KEYLISTENER methods///////////////////////////////
    //should really switch this to key bindings
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                start = true;
                gameLoop.start();
                break;
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
            case KeyEvent.VK_O:
                curves[3].adjustHeading(false);
                break;
            case KeyEvent.VK_P:
                curves[3].adjustHeading(true);
                break;
        }
    }

    //why do i need to have these, stupid interfaces
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    ///////////////////////////////GAME methods///////////////////////////////
    //timer which ticks 60 times/second to update the window at a silky smooth 60fps.
    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            repaint();		//redraw the window
        }
    }

    public void terminate() {
        gameLoop.stop();
        System.out.println("BYE");
        System.exit(0);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if(!start) {
            int stringy = HEIGHT/2, stringx = WIDTH/2;
            //prepare for the most magical of numbers
            g2.setPaint(Color.WHITE);
            g2.setFont(new Font("Comic Sans", Font.BOLD, 20));
            g2.drawString("WELCOME TO CURVES", stringx - 130, stringy - 50);
            g2.setPaint(curves[0].getColor());
            g2.drawString("PINK USES ARROWS", stringx-110, stringy + 20);
            g2.setPaint(curves[1].getColor());
            g2.drawString("WHITE USES Q/W", stringx-95, stringy + 40);
            g2.setPaint(curves[2].getColor());
            g2.drawString("BLUE USES V/B", stringx-87, stringy + 60);
            g2.setPaint(curves[3].getColor());
            g2.drawString("GREEN USES O/P", stringx-95, stringy + 80);
            g2.setPaint(Color.WHITE);
            g2.drawString("PRESS ENTER TO START", stringx-135, stringy + 150);
        }
        else {
            //hack solution to hide intro text
            g2.setPaint(getBackground());
            g2.fillRect(0, 0, WIDTH, HEIGHT);
        }

        for(Curve c : curves) {
            //add collision detection here
            c.advance();
            HashSet<Ellipse2D.Double> currentPath = c.getPath();
            g2.setPaint(c.getColor());
            for(Shape s : currentPath) {
                g2.fill(s);
            }
        }
    }
}
