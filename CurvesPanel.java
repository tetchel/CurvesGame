package ca.etchells.curves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;

public class CurvesPanel extends JPanel {
    private Curve[] curves;
    private javax.swing.Timer gameLoop;
    private boolean start = false;
    private int     height, width, winnerId = -1;
    private static final int GAME_TIED = 99;
    ///////////////////////////////PANEL constructor///////////////////////////////
    public CurvesPanel(final Dimension d, int numPlayersIn) {
        //basic set-up
        setPreferredSize(d);
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();

        height  =   (int)d.getHeight();
        width   =   (int)d.getWidth();
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
        //we do enter and esc manually because they are not movement actions
        //addInput and addMoveAction are helper methods to minimize repetition
        addInput(KeyEvent.VK_ENTER, "enterAction");
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
        addInput(KeyEvent.VK_ESCAPE, "escAction");
        getActionMap().put("escAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminate();
            }
        });

        //quick iterator for accessing key values
        //can't use a loop for the first part, the keyChar changes each time
        int i = 0;
        addInput(KeyEvent.VK_LEFT, KEYS[i++]);
        addInput(KeyEvent.VK_RIGHT,KEYS[i++]);
        addInput(KeyEvent.VK_Q, KEYS[i++]);
        addInput(KeyEvent.VK_W, KEYS[i++]);
        addInput(KeyEvent.VK_V, KEYS[i++]);
        addInput(KeyEvent.VK_B, KEYS[i++]);
        addInput(KeyEvent.VK_O, KEYS[i++]);
        addInput(KeyEvent.VK_P, KEYS[i]);

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
    //TODO figure out a way to queue up inputs so that multiple keys can be processed at once.
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
            //check for a win/tie state
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
            //one player is left, game ends
            if(alive == 1) {
                //game is over
                winnerId = tmp;
            }
            //if tmp was never set, every curve is dead!
            else if(tmp == -1) {
                winnerId = GAME_TIED;
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
    /**
     * paintComponent for the main game panel, draws everything
     * @param g the graphics object corresponding to the panel
     */
    @Override
    protected void paintComponent(Graphics g) {
        //looks better
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        //font to be used for messages to user
        final Font MSG_FONT = new Font("Arial Black", Font.PLAIN, 20);
        //output intro text
        if(!start) {
            g2.setFont(MSG_FONT);
            final String[] OUTPUTS = new String[]   {
                                                        "Welcome to CURVES",
                                                        "PINK USES ARROWS",
                                                        "BLUE USES Q/W",
                                                        "YELLOW USES V/B",
                                                        "GREEN USES O/P",
                                                        "Press ENTER for a new game"
                                                    };
            //where the top string will be drawn
            int stringy = height/20;
            for(int i = 0; i < OUTPUTS.length; i++) {
                //if we're outputting a "PLAYER USES BUTTONS" method, do it in the player's colour
                if(i > 0 && i < OUTPUTS.length-1) {
                    //try/catch required in case there are <4 players
                    try {
                        g2.setPaint(curves[i - 1].getColor());
                    }
                    catch(ArrayIndexOutOfBoundsException e) {
                        //if it's out-of-bounds just go to the next iteration (until you reach the last one)
                        //not perfectly efficient but it works
                        continue;
                    }
                }
                //else do it in the default white
                else
                    g2.setPaint(Color.WHITE);
                //awesome method of automatically centering strings
                String s = OUTPUTS[i];
                //draw each string taking into account the length of the string in pixels
                g2.drawString(s, width/2 - getStringLength(s,g2)/2, stringy+=25);
            }
        }
        //intro is over
        else {
            //hack solution to hide intro text
            g2.setPaint(getBackground());
            //the +50s are required for some reason or it doesn't fill the whole panel
            g2.fillRect(0, 0, width + 50, height + 50);
        }
        //draw the curves
        for(Curve c : curves) {
            //add the next curve segment
            if(c.isAlive())
                c.advance(curves);
            HashSet<CurveSegment> currentPath = c.getPath();
            g2.setPaint(c.getColor());
            //loop through each segment of the curve and draw
            for(Shape s : currentPath) {
                g2.fill(s);
            }
        }
        //check for win/tie
        //output tie message
        if(winnerId == GAME_TIED) {
            //it's a tie
            g2.setPaint(Color.RED);
            String s = "It's a tie!";
            //the next line is messy but essentially it draws the string in the center of the screen, adjusting for
            //the length of the string
            g2.drawString(s, width/2-getStringLength(s,g2)/2, height/2);
            gameLoop.stop();
        }
        //output win message
        else if(winnerId != -1) {
            g2.setPaint(curves[winnerId].getColor());
            //+1 since most people use 1-based indexing!
            String s = "Player " + (winnerId+1) + " has won!";
            g2.drawString(s, width/2-getStringLength(s,g2)/2, height/2);
            gameLoop.stop();
        }
    }
    /**
     * takes a string and graphics object and returns the length of the string in the given graphics context in pixels
     * @param s the string to find the length of
     * @param g2 the graphics object on which the string is to be drawn
     * @return the length of the string in pixels
     */
    private int getStringLength(String s, Graphics2D g2) {
        //the magic of swing, I won't pretend to know what exactly this does
        return (int)g2.getFontMetrics().getStringBounds(s, g2).getWidth();
    }
}
