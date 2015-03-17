package ca.etchells.curves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CurvesMain {

    public static void main(String[] a) {
        final JFrame f = new JFrame("~~~~~~~~~~~~~~~ Drawing simulator 2015 ~~~~~~~~~~~~~~~");

        final CurvesPanel cp = new CurvesPanel();
        f.setSize(cp.getSize());
        f.setBackground(Color.BLACK);

        f.add(cp);

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                f.dispose();
                cp.terminate();
            }
        });
        f.pack();
        f.setResizable(false);
        f.setVisible(true);
    }
}
