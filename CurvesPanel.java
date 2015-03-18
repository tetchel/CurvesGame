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
    private int     height, width, numPlayers, winnerId = -1;
    //TODO allow user to pick numPlayers
    ///////////////////////////////PANEL methods///////////////////////////////
    public CurvesPanel(final Dimension d, int numPlayersIn) {
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

        /*
            CONTROLS
            ENTER RESTART GAME
            ESC EXIT
            LEFT/RIGHT P1
            Q/W P2
            V/B P3
            O/P P4
         */

        //we do enter and escmanually because its action is different from the others
        //addMoveInput and addMoveAction are helper methods to minimize repetition
        addMoveInput(KeyEvent.VK_ENTER, "enterAction");
        getActionMap().put("enterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if the game is not running, we start it.
                //if the game is running, we stop it and prepare to restart.
                if(!gameLoop.isRunning()) {
                    start = true;
                    gameLoop.start();
                }
                else {
                    start = false;
                    gameLoop.stop();
                    repaint();
                }
                winnerId = -1;
                //re-initialize curves
                for(int i = 0; i < curves.length; i++) {
                    curves[i] = new Curve(i, d);
                }
            }
        });
        //map escape to exit the game
        addMoveInput(KeyEvent.VK_ESCAPE, "escAction");
        getActionMap().put("escAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminate();
            }
        });

        //quick iterator for accessing key values
        //can't use a loop for the first part, the keyChar changes each time
        int i = 0;
        addMoveInput(KeyEvent.VK_LEFT, KEYS[i++]);
        addMoveInput(KeyEvent.VK_RIGHT, KEYS[i++]);
        addMoveInput(KeyEvent.VK_Q, KEYS[i++]);
        addMoveInput(KeyEvent.VK_W, KEYS[i++]);
        addMoveInput(KeyEvent.VK_V, KEYS[i++]);
        addMoveInput(KeyEvent.VK_B, KEYS[i++]);
        addMoveInput(KeyEvent.VK_O, KEYS[i++]);
        addMoveInput(KeyEvent.VK_P, KEYS[i]);

        //map curve turning actions
        //manually map the first one so the loop works for the rest
        addMoveAction(KEYS[0], 0, false);
        int j = 0;
        for(i = 1; i < KEYS.length; i++) {
            boolean b = true;
            //every second value we switch to the next curve, so we increment the curve ID j and swap b's value
            if(i % 2 == 0) {
                j++;
                b = false;
            }
            addMoveAction(KEYS[i], j, b);
        }
    }
    ///////////////////////////////KEYBINDINGS methods///////////////////////////////
    /**
     * Helper method for the constructor so I don't have to manually put all the inputs
     * @param keyChar key code for the binding
     * @param key name for the binding
     */
    private void addMoveInput(int keyChar, String key) {
        getInputMap().put(KeyStroke.getKeyStroke(keyChar, 0), key);
    }
    /**
     * Helper method for the constructor so that I don't have to manually put all the actions
     * @param key the actionMap name
     * @param index the curve # the key corresponds to
     * @param b whether to increase the curve's heading positively or negatively
     */
    private void addMoveAction(String key, final int index, final boolean b) {
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Curve c;
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
    public class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            //number of living curves
            int alive = 0;
            //holds the id of each living curve, accessed if only 1 is left so that we have the winner id
            int tmp = -1;
            for(Curve c : CurvesPanel.this.curves) {
                //cycle through curves and see which are left alive
                if(c.isAlive()) {
                    alive++;
                    tmp = c.getId();
                }
            }
            if(alive == 1) {
                //game is over
                winnerId = tmp;
            }
            //if tmp was never set, every curve is dead!
            else if(tmp == -1) {
                //99 is arbitrary 'code' number to say that everyone is dead
                //should use static final variable but why would I change this ever?
                winnerId = 99;
            }
            repaint();		//redraw the window every tick
        }
    }
    /**
     * Called when the containing frame is closed or when user pressed escape
     */
    public void terminate() {
        gameLoop.stop();
        System.exit(0);
    }
    ///////////////////////////////paintComponent///////////////////////////////
    @Override
    public void paintComponent(Graphics g) {
        //looks better
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Font msgFont = new Font("Comic Sans", Font.BOLD, 20);
        //output intro text
        if(!start) {
            int stringy = height/2, stringx = width/2;
            //prepare for the most magical of numbers
            g2.setPaint(Color.WHITE);
            g2.setFont(msgFont);
            g2.drawString("WELCOME TO CURVES", stringx - 122, stringy - 50);
            try {
                g2.setPaint(curves[0].getColor());
                g2.drawString("PINK USES ARROWS", stringx - 105, stringy + 20);
                g2.setPaint(curves[1].getColor());
                g2.drawString("WHITE USES Q/W", stringx - 90, stringy + 40);
                g2.setPaint(curves[2].getColor());
                g2.drawString("BLUE USES V/B", stringx - 82, stringy + 60);
                g2.setPaint(curves[3].getColor());
                g2.drawString("GREEN USES O/P", stringx - 90, stringy + 80);
            }
            catch(ArrayIndexOutOfBoundsException e) {}
            g2.setPaint(Color.WHITE);
            g2.drawString("PRESS ENTER FOR NEW GAME AT ANY TIME", stringx - 225, stringy + 150);
        }
        else {
            //hack solution to hide intro text
            g2.setPaint(getBackground());
            g2.fillRect(0, 0, width+50, height+50);
        }
        if(winnerId == 99) {
            //it's a tie
            g2.setPaint(Color.RED);
            g2.setFont(msgFont);
            //+1 since most people use 1-based indexing!
            g2.drawString("It's a tie!", width/2-50, height/2);
            gameLoop.stop();
        }
        else if(winnerId != -1) {
            //someone has won
            g2.setPaint(curves[winnerId].getColor());
            g2.setFont(msgFont);
            //+1 since most people use 1-based indexing!
            g2.drawString("Player " + (winnerId+1) + " has won!", width/2-100, height/2);
            gameLoop.stop();
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
