package ca.etchells.curves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CurvesMain {
    /**
     * Main method
     * @param a command line arguments
     */
    public static void main(String[] a) {
        //frame to contain the panel
        final JFrame f = new JFrame("~~~~~~~~~~~~~~~ Curves ~~~~~~~~~~~~~~~");

        Dimension d;
        int numPlayers;
        try {
            d = new Dimension(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
            numPlayers = Integer.parseInt(a[2]);
            if(numPlayers < 1 || numPlayers > 4) {
                System.out.println("1-4 players required.");
                System.exit(-2);
            }
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: java -jar Curves.jar <width> <height> <number_of_players>");
            System.out.println("Game will run with default settings.");
            //default values - 720p and 4 players
            d = new Dimension(1280, 720);
            numPlayers = 4;
        }

        //the game panel
        final CurvesPanel cp = new CurvesPanel(d, numPlayers);
        //match the frame's size to the panel
        f.setSize(cp.getSize());
        f.setBackground(Color.BLACK);
        f.add(cp);

        //when the window is closed, terminate the game loop and dispose
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                f.dispose();
                cp.terminate();
            }
        });
        //pack frame to fit contents
        f.pack();
        //resizing the window would screw up the game drastically, set it at runtime instead
        f.setResizable(false);
        f.setVisible(true);
    }
}
