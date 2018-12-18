import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AnimalSim {
    private static boolean looping = true; // When set to false, stop running the program
    private static boolean ranBefore = false;
    private static int animalSelection = 1199;
    private static Animal[] placement = new Animal[1200];

    public static void main(String args[]) {
        AtomicBoolean running = new AtomicBoolean(false);
        AtomicBoolean pause = new AtomicBoolean(false);
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

        // TODO: Ability to change base energy, possibly other animal properties, such as immunity to attacks
        // TODO: Ability to add animals with custom Genes

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
        JLabel mutationText = new JLabel("Mutation Rate: 20%", SwingConstants.CENTER);
        mutationText.setForeground(new Color(0, 255, 0));

        // Mutation Rate Slider
        AtomicReference<Double> mutationRate = new AtomicReference<>(0.2);
        JSlider mutationSlider = new JSlider(JSlider.HORIZONTAL,1,99,20);
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

        // Main Control Panel
        JPanel mainControls = new JPanel(new GridLayout(4, 2, 1, 0));
        mainControls.setBackground(new Color(15, 15, 15));

        // Animal information textarea
        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(0,0,0));
        textArea.setForeground(new Color(0,255,0));
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        // Animal Picker Text
        JLabel animalText = new JLabel("Show Animal's Genes", SwingConstants.CENTER);
        animalText.setForeground(new Color(0, 255, 0));

        // Animal Picker
        String[] animalChoices = new String[1200];
        for (int i = 0; i < animalChoices.length; i++) {
            animalChoices[i] = (i+1)+ ordinalSuffix(i+1)+" Place";
        }
        JComboBox<String> animalPicker = new JComboBox<>(animalChoices);
        animalPicker.setForeground(new Color(0, 255, 0));
        animalPicker.setBackground(new Color(15,15,15));
        animalPicker.addActionListener(e -> {
            animalSelection = 1199-animalPicker.getSelectedIndex();
            animalPicker.setEnabled(false);
            animalPicker.setEnabled(true); // Erase annoying highlighting quickly and nearly flawlessly
            if(ranBefore){
                textArea.setText(placement[animalSelection].getGenes().translateGenes());
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

        // Pause Button
        JButton pauseButton = new JButton("Exit");
        pauseButton.setPreferredSize(new Dimension(200, 50));
        pauseButton.setFocusPainted(false);
        pauseButton.setBorder(null);
        pauseButton.setBackground(new Color(31, 31, 31));
        pauseButton.setForeground(new Color(255, 63, 63));
        pauseButton.addActionListener(e -> {
            if(running.get()){
            pause.set(true);
            pauseButton.setText("Waiting for Game...");
            pauseButton.setEnabled(false);
            }
            else{
                System.exit(0);
            }
        });

        // Add components
        mainControls.add(mutationText);
        mainControls.add(animalText);
        mainControls.add(mutationSlider);
        mainControls.add(animalPicker);
        mainControls.add(gameNumText);
        mainControls.add(playBox);
        mainControls.add(demoButton);
        mainControls.add(pauseButton);

        dialog.setLayout(new GridLayout(1, 2, 4, 0));
        dialog.add(mainControls, BorderLayout.CENTER);
        dialog.add(scrollPane, BorderLayout.EAST);

        dialog.setBackground(new Color(15, 15, 15));

        // Finalize
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center
        dialog.setVisible(true);

        int gen = 0;

        // Initial GameOfLife prompt
        GameOfLife baseGameOfLife = new GameOfLife(200,(int)(0.4375 * 200),20, true);

        // Active part of the code
        while (looping) {
            if (running.get()) {
                demoButton.setEnabled(false);
                mutationSlider.setEnabled(false);
                pauseButton.setText("Pause");
                demoButton.setText("Running...");

                while(running.get()){
                    if(pause.get()){
                        pauseButton.setText("Exit");
                        pauseButton.setEnabled(true);
                        pause.set(false);
                        break;
                    }
                    gen++;
                    gameNumText.setText("Generation "+gen);
                    boolean isVisible = playBox.isSelected();
                    playBox.setSelected(false);

                    // Main loop
                    if(isVisible){
                        placement = new Game(true, 200, 1200).run(genePool);
                    }
                    else {
                        placement = new Game(200, 1200, baseGameOfLife).run(genePool);
                    }
                    ranBefore = true;
                    //System.out.println("Game ended at t="+placement[1199].getAge());
                    if(placement[1199].getAge() == 0){ // Instant game ends mean that there's only one species left
                        System.err.println("Extinction. "+placement[1199].getSymbol()+"s win.");
                        pause.set(true);
                    }
                    textArea.setText(placement[animalSelection].getGenes().translateGenes());
                    Animal[] mutatedAnimals = Genes.mutateAnimals(placement, mutationRate.get());
                    for (int j = 0; j < mutatedAnimals.length; j++) {
                        genePool[j] = mutatedAnimals[j].getGenes();
                    }
                }

                // Pause the game normally
                if(placement[1199].getAge() > 0){
                demoButton.setEnabled(true);
                mutationSlider.setEnabled(true);
                demoButton.setText("Run Simulation");
                running.set(false);
                }
                // End the simulation due to extinction
                else{
                    demoButton.setEnabled(false);
                    demoButton.setText("Game Over.");
                    running.set(false);
                }
            }
        }
    }

    private static String ordinalSuffix(int number){ // 1st, 2nd, 3rd, etc.
        String numString = Integer.toString(number);
        int lastDigit = Integer.parseInt(numString.substring(numString.length()-1));
        if(Math.abs(number)>9){
            // If the tens digit of a number is 1, the number always ends in "th"
            int tensDigit = Integer.parseInt(numString.substring(numString.length()-2,numString.length()-1));
            if(tensDigit==1){return "th";}
        }
        switch(lastDigit){
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
