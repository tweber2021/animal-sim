import javax.swing.*;
import java.awt.*;

public class AnimalSim {
    public static void main(String args[]){

        // Starting Menu
        JFrame dialog = new JFrame("AnimalSim Menu");
        dialog.setResizable(false);
        dialog.setBackground(new Color(31,31,31));
        dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Starting text
        JLabel dialogText = new JLabel("Welcome to AnimalSim!",SwingConstants.CENTER); // HTML time (center text)
        dialogText.setForeground(new Color(0,255,0));

        // Button
        JButton demoButton = new JButton("Play Demo");
        demoButton.setPreferredSize(new Dimension(200,50));
        demoButton.setFocusPainted(false);
        demoButton.setBorder(null);
        demoButton.setBackground(new Color(31,31,31));
        demoButton.setForeground(new Color(0,255,0));
        demoButton.addActionListener(e -> {
            // We need a new thread for this if we want both windows open at once.
            // As a side-effect, we can run as many games as we want simultaneously. That's fine.
            Thread demo = new Thread(() -> new Game(true).run());
            demo.start();
        });

        // Content pane, add components
        Container pane = dialog.getContentPane();
        pane.setBackground(new Color(15,15,15));
        pane.add(demoButton,BorderLayout.SOUTH);
        pane.add(dialogText,BorderLayout.NORTH);

        // Finalize
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center
        dialog.setVisible(true);
    }
}
