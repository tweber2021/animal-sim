import javax.swing.*;
import java.awt.*;

class SimWindow extends JFrame {
    private JTextArea textArea;
    private JTextArea sideTextArea;
    private String sideText;
    JButton fastForwardButton = new JButton("Fast Forward");

    SimWindow(String title, int size){
        super(title); // Use JFrame constructor
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize((int)(getScreenHeight()*0.8),(int)(getScreenHeight()*0.8));
        setResizable(false);

        textArea = new JTextArea(size,(int)(0.4375*size));
        System.out.println(textArea.getWidth()+", "+textArea.getHeight());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        sideTextArea = new JTextArea(20, 30);
        sideTextArea.setEditable(false);
        sideTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sideText = "";

        fastForwardButton.setBorder(null);
        fastForwardButton.setFocusPainted(false);
        fastForwardButton.setPreferredSize(new Dimension(0, getScreenHeight()/27)); // Width doesn't matter

        JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL,1,24,12);
        zoomSlider.addChangeListener(e -> textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, zoomSlider.getValue())));

        JPanel controlPanel = new JPanel(new GridLayout(2,1));
        controlPanel.add(zoomSlider);
        controlPanel.add(fastForwardButton);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension((int)(getScreenHeight()*0.7),(int)(getScreenHeight()*0.7)));

        Container pane = getContentPane();
        pane.add(scrollPane, BorderLayout.CENTER);
        pane.add(sideTextArea, BorderLayout.EAST);
        pane.add(controlPanel, BorderLayout.SOUTH);

        // Dark Theme
        fastForwardButton.setBackground(new Color(31,31,31));
        fastForwardButton.setForeground(new Color(0,255,0));
        setBackground(new Color(31,31,31));
        textArea.setBackground(new Color(0,0,0));
        textArea.setForeground(new Color(0,255,0));
        scrollPane.getVerticalScrollBar().setBackground(Color.black);
        scrollPane.getHorizontalScrollBar().setBackground(Color.black);
        scrollPane.getVerticalScrollBar().setForeground(new Color(31,31,31));
        scrollPane.getHorizontalScrollBar().setForeground(new Color(31,31,31));
        sideTextArea.setBackground(new Color(15,15,15));
        sideTextArea.setForeground(new Color(0,255,0));
        zoomSlider.setBackground(new Color(15,15,15));

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