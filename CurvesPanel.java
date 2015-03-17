package ca.etchells.curves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;

public class CurvesPanel extends JPanel {

    private Curve[] curves;
    private javax.swing.Timer gameLoop;
    private boolean start = false;
    private static final int    HEIGHT = 900,
                                WIDTH  = 1600;

    ///////////////////////////////APPLET methods///////////////////////////////
    public CurvesPanel() {
        //basic set-up
        Dimension windowSize = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(windowSize);
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();

        gameLoop = new javax.swing.Timer(17, new TimerListener());			//ticks 1000/17 = 60 FPS

        //fill curves array with initialized curve objects - ONE PER PLAYER, EDIT THIS NUMBER FOR FEWER PLAYERS
        //but be aware <4 players will result in exceptions being thrown for now.
        curves = new Curve[4];
        for(int i = 0; i < curves.length; i++) {
            curves[i] = new Curve(i, windowSize);
        }

        //add key bindings, this is heavy
        //the name of each key binding
        String[] keys = new String[] {  "leftAction",
                                        "rightAction",
                                        "QAction",
                                        "WAction",
                                        "VAction",
                                        "BAction",
                                        "OAction",
                                        "PAction"
                                    };


        //we do enter manually because its action is different from the others
        //addInput and addAction are helper methods to minimize repetition
        addInput(KeyEvent.VK_ENTER, "enterAction");
        getActionMap().put("enterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start = true;
                gameLoop.start();
            }
        });

        //quick iterator for accessing key values
        //can't use a loop, the keyChar changes each time
        int i = 0;
        addInput(KeyEvent.VK_LEFT,  keys[i++]);
        addInput(KeyEvent.VK_RIGHT, keys[i++]);
        addInput(KeyEvent.VK_Q, keys[i++]);
        addInput(KeyEvent.VK_W, keys[i++]);
        addInput(KeyEvent.VK_V, keys[i++]);
        addInput(KeyEvent.VK_B, keys[i++]);
        addInput(KeyEvent.VK_O, keys[i++]);
        addInput(KeyEvent.VK_P, keys[i]);

        //map curve turning actions
        int j = 0;
        for(i = 1; i < keys.length; i++) {
            boolean b = false;
            if(i % 2 == 0) {
                j++;
                b = true;
            }
            addAction(keys[i], j, b);
        }
    }

    ///////////////////////////////KEYBINDINGS methods///////////////////////////////

    /**
     * Helper method for the constructor so I don't have to manually put all the inputs
     * @param keyChar key code for the binding
     * @param key name for the binding
     */
    private void addInput(int keyChar, String key) {
        getInputMap().put(KeyStroke.getKeyStroke(keyChar, 0), key);
    }

    /**
     * Helper method for the constructor so that I don't have to manually put all the actions
     * @param key the actionMap name
     * @param index the curve # the key corresponds to
     * @param b whether to increase the curve's heading positively or negatively
     */
    private void addAction(String key, final int index, final boolean b) {
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                curves[index].adjustHeading(b);
            }
        });
    }

    ///////////////////////////////GAME methods///////////////////////////////
    //timer which ticks 60 times/second to update the window at a silky smooth 60fps.
    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            repaint();		//redraw the window
        }
    }

    /**
     * Called when the containing frame is closed
     */
    public void terminate() {
        gameLoop.stop();
        System.exit(0);
    }

    @Override
    public void paintComponent(Graphics g) {
        //antialiasing makes things look cleaner
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if(!start) {
            //output intro text
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

        //draw the curves
        for(Curve c : curves) {
            //add the next curve segment
            c.advance();
            HashSet<Ellipse2D.Double> currentPath = c.getPath();
            g2.setPaint(c.getColor());
            //loop through each segment of the curve and draw
            for(Shape s : currentPath) {
                g2.fill(s);
            }
        }
    }
}
