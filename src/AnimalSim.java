import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AnimalSim {
    private static boolean looping = true; // When set to false, stop running the program

    public static void main(String args[]) {
        AtomicBoolean running = new AtomicBoolean(false);
        AtomicBoolean reset = new AtomicBoolean(false);
        Genes[] genePool = new Genes[1200];
        for (int i = 0; i < genePool.length; i++) {
            genePool[i] = new Genes(Genes.TEMPLATE);
            if (i < genePool.length / 3) {
                genePool[i].setSpecies('L');
            } else if (i < (genePool.length / 3) * 2) {
                genePool[i].setSpecies('W');
            } else {
                genePool[i].setSpecies('B');
            }
        }

        // Starting Menu
        JFrame dialog = new JFrame("AnimalSim - Settings");
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(15, 15, 15));
        dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                looping = false;
            }
        });

        // Demo Button
        JButton demoButton = new JButton("Run Simulation");
        demoButton.setPreferredSize(new Dimension(200, 50));
        demoButton.setFocusPainted(false);
        demoButton.setBorder(null);
        demoButton.setBackground(new Color(31, 31, 31));
        demoButton.setForeground(new Color(0, 255, 0));
        demoButton.addActionListener(e -> running.set(true));

        // Mutation Text
        JLabel mutationText = new JLabel("Mutation Rate: 50%", SwingConstants.CENTER);
        mutationText.setForeground(new Color(0, 255, 0));

        // Mutation Rate Slider
        AtomicReference<Double> mutationRate = new AtomicReference<>(0.5);
        JSlider mutationSlider = new JSlider(JSlider.HORIZONTAL,1,99,50);
        mutationSlider.setBackground(new Color(15,15,15));
        mutationSlider.addChangeListener(e -> {
            mutationRate.set((double) (mutationSlider.getValue()) / 100);
            mutationText.setText("Mutation Rate: "+(int)(mutationRate.get() *100)+"%");
            for (int i = 0; i < genePool.length; i++) {
                genePool[i] = new Genes(Genes.TEMPLATE);
                if (i < genePool.length / 3) {
                    genePool[i].setSpecies('L');
                } else if (i < (genePool.length / 3) * 2) {
                    genePool[i].setSpecies('W');
                } else {
                    genePool[i].setSpecies('B');
                }
            }
        });

        // Generations Text
        JLabel generationText = new JLabel("Run for 10 gens", SwingConstants.CENTER);
        generationText.setForeground(new Color(0, 255, 0));

        // Generations Slider
        AtomicInteger generations = new AtomicInteger(10);
        JSlider generationSlider = new JSlider(JSlider.HORIZONTAL,0,5,1);
        generationSlider.setBackground(new Color(15,15,15));
        generationSlider.addChangeListener(e -> {
                generations.set((int) Math.pow(10, generationSlider.getValue()));
                if(generations.get() == 1){
                    generationText.setText("Run for one gen");
                }
                else if(generations.get() == 100000){
                    generationText.setText("Run endlessly");
                    generations.set(Integer.MAX_VALUE); // Pretty much infinite
                }
                else{
                generationText.setText("Run for "+generations+" gens");
                }
        });

        // Progress text
        JLabel gameNumText = new JLabel("Generation 0", SwingConstants.CENTER);
        gameNumText.setForeground(new Color(0, 255, 0));

        // Play next game checkbox
        JCheckBox playBox = new JCheckBox("Show next game");
        playBox.setBackground(new Color(15,15,15));
        playBox.setForeground(new Color(0, 255, 0));
        playBox.setFocusPainted(false);
        /*playBox.addActionListener(e -> {
            if(!running.get() && playBox.isSelected()){
                generations.set(1);
                running.set(true);
            }
        });*/

        // Cancel Button
        JButton resetButton = new JButton("Exit");
        resetButton.setPreferredSize(new Dimension(200, 50));
        resetButton.setFocusPainted(false);
        resetButton.setBorder(null);
        resetButton.setBackground(new Color(31, 31, 31));
        resetButton.setForeground(new Color(255, 63, 63));
        resetButton.addActionListener(e -> {
            if(running.get()){
            reset.set(true);
            resetButton.setText("Waiting for Game...");
            resetButton.setEnabled(false);
            }
            else{
                System.exit(0);
            }
        });


        // Add components
        dialog.add(mutationText);
        dialog.add(generationText);
        dialog.add(mutationSlider);
        dialog.add(generationSlider);
        dialog.add(gameNumText);
        dialog.add(playBox);
        dialog.add(demoButton);
        dialog.add(resetButton);

        dialog.setLayout(new GridLayout(4, 2, 1, 0));
        dialog.setBackground(new Color(15, 15, 15));

        // Finalize
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center
        dialog.setVisible(true);

        while (looping) {
            if (running.get()) {
                demoButton.setEnabled(false);
                mutationSlider.setEnabled(false);
                generationSlider.setEnabled(false);
                resetButton.setText("Cancel");
                demoButton.setText("Running...");

                for (int i = 0; i < generations.get(); i++) {
                    if(reset.get()){
                        for (int j = 0; j < genePool.length; j++) {
                            genePool[i] = new Genes(Genes.TEMPLATE);
                            if (j < genePool.length / 3) {
                                genePool[j].setSpecies('L');
                            } else if (j < (genePool.length / 3) * 2) {
                                genePool[j].setSpecies('W');
                            } else {
                                genePool[j].setSpecies('B');
                            }
                        }
                        resetButton.setText("Exit");
                        resetButton.setEnabled(true);
                        reset.set(false);
                        gameNumText.setText("Generation 0");
                        break;
                    }
                    gameNumText.setText("Generation "+(i+1));
                    boolean isVisible = playBox.isSelected();
                    playBox.setSelected(false);

                    // Main loop
                    Animal[] placement = new Game(isVisible, 200, 1200).run(genePool);
                    System.out.println("Game ended at t="+placement[1199].getAge()); // TODO: Handle extinction scenario
                    System.out.print("'"+placement[1199].getSymbol()+"' #"+placement[1199].getID()+" wins! Code:\n");
                    System.out.println(placement[1199].getGenes().translateGenes());
                    //System.out.println("  ("+(placement[1199].getCode().length-23)/8+")"); // Print number of chunks
                    Animal[] mutatedAnimals = Genes.mutateAnimals(placement, mutationRate.get());
                    for (int j = 0; j < mutatedAnimals.length; j++) {
                        genePool[j] = mutatedAnimals[j].getGenes();
                    }
                }

                demoButton.setEnabled(true);
                generationSlider.setEnabled(true);
                mutationSlider.setEnabled(true);
                demoButton.setText("Run Simulation");
                running.set(false);
            }
        }
    }
}
