import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

class GameOfLife { // Conway's Game of Life using Maps for game compatibility
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

    Map gen(){ // TODO: Screen wrap
        generation++;
        nextPattern.setEqualTo(pattern);
        for (int i = 0; i < pattern.getWidth(); i++) { // 2D array iteration
            for(int j = 0; j < pattern.getHeight(); j++){
                if((getNeighbors(i,j)<2||getNeighbors(i,j)>3) && pattern.read(i,j) == 'o'){setCell(i,j,false);} // Kill cells that don't have 2 or 3 neighbors
                if(getNeighbors(i,j)==3 && pattern.read(i,j) == ' '){setCell(i,j,true);} // Reactivate cells with exactly 3 neighbors
            }
        }
        pattern.setEqualTo(nextPattern);
        pattern.setEqualTo(nextPattern);
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException ignored) {}*/
        return pattern;
    }

    private int getNeighbors(int x, int y){
        int neighbors = 0;
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3; j++) {
                if(i==1 && j==1){continue;} // Exclude self
                if(x+i-1<0 || x+i-1>=pattern.getWidth() || y+j-1<0 || y+j-1>=pattern.getHeight()){continue;} // Skip out of bounds points so no exceptions are thrown
                if(isAlive(x+i-1,y+j-1)){neighbors++;}
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

    // void setPattern(Map map){
    //    pattern = map;
    //}

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
        JFrame prompt = new JFrame("Enter a starting pattern.");
        prompt.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // You can't close this window!
        prompt.setResizable(false);
        prompt.setAlwaysOnTop(true);

        JTextArea textArea = new JTextArea(10,10);
        textArea.setBackground(new Color(0,0,0));
        textArea.setForeground(new Color(0,255,0));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(200,50));
        submitButton.setFocusPainted(false);
        submitButton.setBorder(null);
        submitButton.setBackground(new Color(31,31,31));
        submitButton.setForeground(new Color(0,255,0));
        AtomicBoolean pressed = new AtomicBoolean(false);
        submitButton.addActionListener(e -> {
            prompt.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            prompt.dispatchEvent(new WindowEvent(prompt, WindowEvent.WINDOW_CLOSING));
            setFromText(textArea.getText());
            pressed.set(true);
        });

        Container pane = prompt.getContentPane();
        pane.add(textArea, BorderLayout.NORTH);
        pane.add(submitButton, BorderLayout.SOUTH);

        prompt.pack();
        prompt.setLocationRelativeTo(null);
        prompt.setVisible(true);
        while(!pressed.get()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored){}
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
        System.out.println("Setting:");
        System.out.println(text);

        char[][] input = new char[39][39];
        int x = 0;
        int y = 0;
        int width = 0;

        // Generate from input
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if(currentChar=='O'){currentChar='o';}
            if(currentChar=='.'){currentChar=' ';}
            if(currentChar==' '||currentChar=='o'){
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
        System.out.println(width);

        if(width==0){
            randomize(20);
            return;
        }

        // Add to the board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j <= y; j++) {
                int ofsX = (pattern.getWidth()/2)-(width/2);
                int ofsY = (pattern.getHeight()/2)-(width/2);
                if(input[i][j]!='o'){input[i][j]=' ';} // Spaces versus nothing makes a difference here
                pattern.set(i+ofsX,j+ofsY,input[i][j],-1);
            }
        }
    }

    int getGeneration() {
        return generation;
    }
}
