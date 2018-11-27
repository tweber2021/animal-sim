import javax.swing.*;
import java.awt.*;

class SimWindow extends JFrame {
    private JTextArea textArea;
    private JTextArea sideTextArea;
    private String sideText;
    JButton fastForwardButton = new JButton("Fast Forward");

    SimWindow(String title, int columns){
        super(title); // Use JFrame constructor
        int rows = (int) (0.4375 * columns); // This ratio create a roughly square-shaped textbox.
        setSize(columns, rows);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        textArea = new JTextArea(rows, columns);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, getScreenHeight()/60));

        sideTextArea = new JTextArea(rows, 30);
        sideTextArea.setEditable(false);
        sideTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sideText = "";

        fastForwardButton.setBorder(null);
        fastForwardButton.setFocusPainted(false);
        fastForwardButton.setPreferredSize(new Dimension(0, getScreenHeight()/27)); // Width doesn't matter

        Container pane = getContentPane();
        pane.add(textArea, BorderLayout.WEST);
        pane.add(sideTextArea, BorderLayout.EAST);
        pane.add(fastForwardButton, BorderLayout.SOUTH);

        // Dark Theme
        fastForwardButton.setBackground(new Color(31,31,31));
        fastForwardButton.setForeground(new Color(0,255,0));
        setBackground(new Color(31,31,31));
        textArea.setBackground(new Color(0,0,0));
        textArea.setForeground(new Color(0,255,0));
        sideTextArea.setBackground(new Color(15,15,15));
        sideTextArea.setForeground(new Color(0,255,0));

        pack();
        setLocationRelativeTo(null); // Center
    }

    void setMap(Map map){
        textArea.setText(mapToText(map));
    }

    void sidePrintln(String line){sideText += " "+line+"\n";}

    void sidePrintln(String line, int additionalLineBreaks){
        StringBuilder builder = new StringBuilder(sideText);
        for(int i=0;i<additionalLineBreaks;i++){
            builder.append("\n");
        }
        sideText = builder.toString()+" "+line+"\n";
    }

    void clearSideText(){sideText = "";}

    void updateSideText(){sideTextArea.setText(sideText);} // Controlled refresh so printing doesn't cause flickers

    private static String mapToText(Map map){
        StringBuilder result = new StringBuilder();
        for(int i=0;i<map.getHeight();i++){
            for(int j=0;j<map.getWidth();j++){
                result.append(map.read(j,i));
            }
            result.append("\n");
        }
        return result.toString();
    }

    private static int getScreenHeight(){
        return (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }

    void setClosable(boolean closable){
        if(closable){setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);}
        else{setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);}
    }

}