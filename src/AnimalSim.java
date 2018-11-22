import javax.swing.*;
import java.awt.*;

public class AnimalSim {
    public static void main(String args[]){

        // Starting Menu
        JFrame dialog = new JFrame("AnimalSim Menu");
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(15,15,15));
        dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Starting text
        JLabel dialogText = new JLabel("Welcome to AnimalSim!",SwingConstants.CENTER); // HTML time (center text)
        dialogText.setForeground(new Color(0,255,0));

        // Demo Button
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

        dialog.add(dialogText);
        dialog.add(demoButton);
        dialog.setLayout(new GridLayout(2,1,1,0));
        dialog.setBackground(new Color(15,15,15));

        // Finalize
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center
        dialog.setVisible(true);

        // Debug and other temporary stuff
        int[] tally = new int[3];
        for (int i = 0; i <100 ; i++) {
            char winner = new Game(false).run();
            switch(winner){
                case 'm': tally[0]++;
                break;
                case 'c': tally[1]++;
                break;
                case 's': tally[2]++;
            }
        }
        System.out.println("Games won out of 100:");
        System.out.println("\nMice: "+tally[0]+"\nCats: "+tally[1]+"\nSnakes: "+tally[2]);
    }
}
