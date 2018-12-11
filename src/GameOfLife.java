import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

class GameOfLife { // Conway's Game of Life using Maps for game compatibility
    private String patternText = "";
    private Map pattern;
    private Map nextPattern;
    private int kills;
    private int generation;

    GameOfLife(int width, int height, int randomSize, boolean prompt){
        pattern = new Map(width,height);
        nextPattern = new Map(width,height);
        if(prompt){promptPattern();}
        else{randomize(randomSize);}
        nextPattern.setEqualTo(pattern);
    }

    Map gen(){ // Possible Improvement: Represent life cells as points and only iterate through those and their surrounding dead cells instead of looping through everything
        generation++;
        nextPattern.setEqualTo(pattern);
        for (int i = 0; i < pattern.getWidth(); i++) { // 2D array iteration
            for(int j = 0; j < pattern.getHeight(); j++){
                if((getNeighbors(i,j)<2||getNeighbors(i,j)>3) && pattern.read(i,j) == 'o'){setCell(i,j,false);} // Kill cells that don't have 2 or 3 neighbors
                if(getNeighbors(i,j)==3 && pattern.read(i,j) == ' '){setCell(i,j,true);} // Reactivate cells with exactly 3 neighbors
            }
        }
        pattern.setEqualTo(nextPattern);
        return pattern;
    }

    private int getNeighbors(int x, int y){
        int neighbors = 0;
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3; j++) {
                if(i==1 && j==1){continue;} // Exclude self
                int curX = x+i-1; int curY = y+j-1; // Current coordinates

                // Screen Wrap
                if(curX >= pattern.getWidth()){curX = curX%pattern.getWidth();}
                if(curX < 0){curX += pattern.getWidth();}
                if(curY >= pattern.getHeight()){curY = curY%pattern.getHeight();}
                if(curY < 0){curY += pattern.getHeight();}

                if(isAlive(curX,curY)){neighbors++;}
            }
        }
        return neighbors;
    }

    private void setCell(int x, int y, boolean alive){
        if(alive){nextPattern.set(x,y,'o',-1);}
        else{nextPattern.set(x,y,' ',-1);}
    }

    private boolean isAlive(int x, int y){ // Check if a cell is alive
        return pattern.read(x,y) == 'o';
    }

    private void randomize(int width){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                int ofsX = (pattern.getWidth()/2)-(width/2); // Center the Game of Life
                int ofsY = (pattern.getHeight()/2)-(width/2);
                if(Math.random()>0.5){pattern.set(i+ofsX,j+ofsY,'o',-1);}
                else{pattern.set(i+ofsX,j+ofsY,' ',-1);}
            }
        }
    }

    private void promptPattern() {
        AtomicBoolean readBoxInput = new AtomicBoolean();
        JFrame prompt = new JFrame("Enter a starting pattern.");
        prompt.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // You can't close this window!
        prompt.setResizable(false);
        prompt.setAlwaysOnTop(true);

        JTextArea textArea = new JTextArea(15,15);
        textArea.setBackground(new Color(0,0,0));
        textArea.setForeground(new Color(0,255,0));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(250,250));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(200,50));
        submitButton.setFocusPainted(false);
        submitButton.setBorder(null);
        submitButton.setBackground(new Color(31,31,31));
        submitButton.setForeground(new Color(0,255,0));
        AtomicBoolean pressed = new AtomicBoolean(false);
        submitButton.addActionListener(e -> prompt.dispatchEvent(new WindowEvent(prompt, WindowEvent.WINDOW_CLOSING)));

        JPanel patternPanel = new JPanel(new BorderLayout());
        patternPanel.add(new JLabel("Enter a custom Life pattern."));
        patternPanel.add(scrollPane);

        String[] choices = getPatternChoices();
        JComboBox<String> patternPicker = new JComboBox<>(choices);
        patternPicker.setPreferredSize(new Dimension(200,25));
        patternPicker.addActionListener(e -> {
            readBoxInput.set(false);
            int patternSelection = patternPicker.getSelectedIndex();
            switch(patternSelection){
                case 0:
                    patternText = "";
                    break;
                case 1:
                    patternText = "random";
                    break;
                case 2:
                    readBoxInput.set(true);
                    break;
            }
            if(patternSelection>2){
                patternText = getPatternFromFile(LifeExamples.getExample(patternSelection-3));
            }
            textArea.setText(patternText);
        });

        prompt.addWindowListener(new java.awt.event.WindowAdapter() { // Store the result even if the user closes the window
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                pressed.set(true);
                try{
                setFromText(patternText);}
                catch(Exception e){
                    //e.printStackTrace();
                    System.err.println("Pattern was imported with errors.");
                    setFromText("");
                }
            }
        });

        Container pane = prompt.getContentPane();
        pane.add(patternPicker,BorderLayout.NORTH);
        pane.add(patternPanel,BorderLayout.CENTER);
        pane.add(submitButton,BorderLayout.SOUTH);
        patternPanel.setVisible(true);

        prompt.pack();
        prompt.setLocationRelativeTo(null);
        prompt.setVisible(true);
        while(!pressed.get()) {
            textArea.setEditable(readBoxInput.get());
            if(readBoxInput.get()){
                patternText = textArea.getText();
                if(isRLE(patternText)){textArea.setText(getFromRLE(patternText));}
            }
        }
    }

    int getPopulation(){
        int population = 0;
        for (int i = 0; i < pattern.getWidth(); i++) {
            for (int j = 0; j < pattern.getHeight(); j++) {
                if(pattern.read(i,j) == 'o'){population++;}
            }
        }
        return population;
    }

    int getKills() {
        return kills;
    }

    void incKills() {
        kills++;
    }

    private void setFromText(String text){

        char[][] input = new char[pattern.getWidth()][pattern.getHeight()]; // Patterns can be as large as the map
        int x = 0;
        int y = 0;
        int width = 0;

        if(text.equals("random")){
            randomize(20);
            return;
        }

        // Generate from input
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if(currentChar=='O'){currentChar='o';}
            if(currentChar=='.'){currentChar=' ';}
            if((currentChar==' '||currentChar=='o')&&(x<input.length&&y<input[0].length)){
                input[x][y] = currentChar;
                x++;
            }
            else if(currentChar=='\n'){
                if(x>width){width=x;}
                x=0;
                y++;
            }
        }
        if(x>width){width=x;}
        if(width>=pattern.getWidth()){
            width = pattern.getWidth();
            System.err.println("Pattern was too wide.");
        }
        if(y+1>=pattern.getHeight()){
            y = pattern.getHeight()-2;
            System.err.println("Pattern was too tall.");
        }

        // Add to the board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j <= y; j++) {
                int ofsX = (pattern.getWidth()/2)-(width/2);
                int ofsY = (pattern.getHeight()/2)-((y+1)/2);
                if(input[i][j]!='o'){input[i][j]=' ';} // Spaces versus nothing makes a difference here
                pattern.set(i+ofsX,j+ofsY,input[i][j],-1);
            }
        }
    }

    int getGeneration() {
        return generation;
    }

    private String[] getExampleTitles(){
        String[] list = new String[LifeExamples.getNumExamples()];
        for (int i = 0; i < LifeExamples.getNumExamples(); i++) {
            list[i] = LifeExamples.getExampleTitle(i);
        }
        return list;
    }

    private String[] getPatternChoices(){ // Get the entire list of combo box options
        String[] options = new String[LifeExamples.getNumExamples()+3];
        options[0] = "None"; options[1] = "Random"; options[2] = "Custom";
        System.arraycopy(getExampleTitles(),0,options,3,LifeExamples.getNumExamples());
        return options;
    }

    private String getFromRLE(String RLEdata){ // Simple format commonly used for storing Life patterns
        StringBuilder plaintext = new StringBuilder(); // Convert from RLE to plaintext.
        // Could be faster, but we don't need to worry about speed too much here since we're not constantly using this
        int repeat = 1;
        int readPos = 0;
        while(readPos<RLEdata.length()){
            if(isNumber(RLEdata.charAt(readPos))){ // Look for repeat values
                int readAhead = 1;
                while(isNumber(RLEdata.charAt(readPos+readAhead))){ // Should not happen at EOF so it shouldn't overflow
                    readAhead++;
                }
                repeat = Integer.parseInt(RLEdata.substring(readPos,readPos+readAhead));
                readPos+=readAhead;
            }
            if(RLEdata.charAt(readPos)=='b'||RLEdata.charAt(readPos)=='o'||RLEdata.charAt(readPos)=='$'){
                for (int i = 0; i < repeat; i++) { // Repeat number of times the int specified
                    switch(RLEdata.charAt(readPos)){
                        case 'b': // Dead cell
                            plaintext.append(".");
                            //System.out.print(".");
                            break;
                        case 'o': // Live cell
                            plaintext.append("O");
                            //System.out.print("O");
                            break;
                        case '$': // Line break
                            plaintext.append("\n");
                            //System.out.println();
                    }
                }
            }
            repeat = 1;
            readPos++;
        }
        return plaintext.toString();
    }

    private boolean isNumber(char character){
        return ((int)character >= 48 &&(int)character <= 57); // ASCII values 48-57 are digits 0-9
    }

    private boolean isRLE(String data){ // Determine if a pattern is probably encoded in the RLE format to act on it accordingly.
        return (charInStr(data,'$')||charInStr(data,'b')||charInStr(data, '!'));
    }

    private boolean charInStr(String str, char chr){
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)==chr){return true;}
        }
        return false;
    }

    private String getPatternFromFile(String fileContents){ // Only .rle files are supported.
        StringBuilder data = new StringBuilder();
        int pos = 0;
        boolean commented = false; // Also used for the rule line
        while (pos<fileContents.length()){
            char current = fileContents.charAt(pos);
            switch(current){
                case '#':
                    commented = true;
                    break;
                case '\n':
                    if(commented){System.out.println();} // Separate comments
                    commented = false;
                    break;
                case 'x': // Cut out the rule line
                    if(!commented){
                        commented = true;
                    }
                    System.out.print(current);
                    break;
                default:
                    if(!commented){
                        data.append(current);
                    }
                    else{
                        System.out.print(current); // Display comments in the console
                }
            }
            pos++;
        }
        System.out.println();
        return getFromRLE(data.toString());
    }
}
