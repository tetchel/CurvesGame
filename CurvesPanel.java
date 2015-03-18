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
    private int     height, width, numPlayers;
    //TODO allow user to pick numPlayers
    ///////////////////////////////PANEL methods///////////////////////////////
    public CurvesPanel(Dimension d, int numPlayersIn) {
        //basic set-up
        setPreferredSize(d);
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();

        height  =   (int)d.getHeight();
        width   =   (int)d.getWidth();
        numPlayers = numPlayersIn;
        gameLoop = new javax.swing.Timer(17, new TimerListener());			//ticks 1000/17 = 60 FPS

        curves = new Curve[numPlayersIn];
        for(int i = 0; i < curves.length; i++) {
            curves[i] = new Curve(i, d);
        }
        //add key bindings, this is heavy
        //the name of each key binding
        final String[] KEYS = new String[]  {
                                                "leftAction",
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
        addInput(KeyEvent.VK_LEFT,  KEYS[i++]);
        addInput(KeyEvent.VK_RIGHT, KEYS[i++]);
        addInput(KeyEvent.VK_Q, KEYS[i++]);
        addInput(KeyEvent.VK_W, KEYS[i++]);
        addInput(KeyEvent.VK_V, KEYS[i++]);
        addInput(KeyEvent.VK_B, KEYS[i++]);
        addInput(KeyEvent.VK_O, KEYS[i++]);
        addInput(KeyEvent.VK_P, KEYS[i]);

        //map curve turning actions
        //manually map the first one so the loop works for the rest
        addAction(KEYS[0], 0, false);
        int j = 0;
        for(i = 1; i < KEYS.length; i++) {
            boolean b = true;
            if(i % 2 == 0) {
                j++;
                b = false;
            }
            addAction(KEYS[i], j, b);
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
                Curve c = null;
                try {
                    c = curves[index];
                    if(c.isAlive())
                        c.adjustHeading(b);
                }
                catch(ArrayIndexOutOfBoundsException ae) {}
            }
        });
    }
    ///////////////////////////////GAME methods///////////////////////////////
    //timer which ticks 60 times/second to update the window at a silky smooth 60fps.
    private class TimerListener implements ActionListener {
        //TODO detect winner
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

    ///////////////////////////////paintComponent///////////////////////////////
    @Override
    public void paintComponent(Graphics g) {
        //antialiasing looks better
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //output intro text
        if(!start) {
            int stringy = height/2, stringx = width/2;
            //prepare for the most magical of numbers
            g2.setPaint(Color.WHITE);
            g2.setFont(new Font("Comic Sans", Font.BOLD, 20));
            g2.drawString("WELCOME TO CURVES", stringx - 130, stringy - 50);
            try {
                g2.setPaint(curves[0].getColor());
                g2.drawString("PINK USES ARROWS", stringx - 110, stringy + 20);
                g2.setPaint(curves[1].getColor());
                g2.drawString("WHITE USES Q/W", stringx - 95, stringy + 40);
                g2.setPaint(curves[2].getColor());
                g2.drawString("BLUE USES V/B", stringx - 87, stringy + 60);
                g2.setPaint(curves[3].getColor());
                g2.drawString("GREEN USES O/P", stringx - 95, stringy + 80);
            }
            catch(ArrayIndexOutOfBoundsException e) {
                g2.setPaint(Color.WHITE);
                g2.drawString("PRESS ENTER TO START", stringx - 135, stringy + 150);
            }
        }
        else {
            //hack solution to hide intro text
            g2.setPaint(getBackground());
            g2.fillRect(0, 0, width, height);
        }
        //draw the curves
        for(Curve c : curves) {
            //add the next curve segment
            if(c.isAlive())
                c.advance(curves);
            HashSet<Ellipse2D.Double> currentPath = c.getPath();
            g2.setPaint(c.getColor());
            //loop through each segment of the curve and draw
            for(Shape s : currentPath) {
                g2.fill(s);
            }
        }
    }
}
