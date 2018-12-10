import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnimalSim {
    private static boolean looping = true; // When set to false, stop running the program

    public static void main(String args[]) {
        AtomicBoolean running = new AtomicBoolean(false);
        Genes[] genePool = new Genes[1200];
        for (int i = 0; i < genePool.length; i++) {
            genePool[i] = new Genes(Genes.TEMPLATE, 0);
        }

        // Starting Menu
        JFrame dialog = new JFrame("AnimalSim - Settings"); // TODO: Make this a settings picker eventually
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(15, 15, 15));
        dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                looping = false;
            }
        });

        // Starting text
        JLabel dialogText = new JLabel("Welcome to AnimalSim!", SwingConstants.CENTER); // center text
        dialogText.setForeground(new Color(0, 255, 0));

        // Demo Button
        JButton demoButton = new JButton("Run Simulation");
        demoButton.setPreferredSize(new Dimension(200, 50));
        demoButton.setFocusPainted(false);
        demoButton.setBorder(null);
        demoButton.setBackground(new Color(31, 31, 31));
        demoButton.setForeground(new Color(0, 255, 0));
        demoButton.addActionListener(e -> running.set(true));

        dialog.add(dialogText);
        dialog.add(demoButton);
        dialog.setLayout(new GridLayout(2, 1, 1, 0));
        dialog.setBackground(new Color(15, 15, 15));

        // Finalize
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center
        dialog.setVisible(true);

        while (looping) {
            if (running.get()) {
                demoButton.setEnabled(false);
                demoButton.setText("Running...");

                Animal[] placement = new Game(true, 200, 1200).run(genePool);
                System.out.println(placement[1199].getSymbol());
                // TODO: Fix uninitialized array

                demoButton.setEnabled(true);
                demoButton.setText("Run Simulation");
                running.set(false);
            }
        }
    }
}
