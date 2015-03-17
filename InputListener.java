package ca.etchells.curves;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public class InputListener implements KeyListener{

    HashSet<Integer> inputs;

    public InputListener() {
        inputs = new HashSet<>();
        Timer keyTimer = new javax.swing.Timer(17, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        keyTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        inputs.add(e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        inputs.remove(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
