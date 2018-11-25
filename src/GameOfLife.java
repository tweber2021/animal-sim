class GameOfLife { // Conway's Game of Life using Maps for game compatibility
    private Map pattern;
    private Map nextPattern;

    GameOfLife(int width, int height, int randomSize){
        pattern = new Map(width,height);
        nextPattern = new Map(width,height);
        randomize(randomSize);
        nextPattern.setEqualTo(pattern);
    }

    Map gen(){
        nextPattern.setEqualTo(pattern);
        for (int i = 0; i < pattern.getWidth(); i++) { // 2D array iteration
            for(int j = 0; j < pattern.getHeight(); j++){
                if((getNeighbors(i,j)<2||getNeighbors(i,j)>3) && pattern.read(i,j) == 'o'){setCell(i,j,false);} // Kill cells that don't have 2 or 3 neighbors
                if(getNeighbors(i,j)==3 && pattern.read(i,j) == ' '){setCell(i,j,true);} // Reactivate cells with exactly 3 neighbors
            }
        }
        pattern.setEqualTo(nextPattern);
        pattern.setEqualTo(nextPattern);
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

    int getPopulation(){
        int population = 0;
        for (int i = 0; i < pattern.getWidth(); i++) {
            for (int j = 0; j < pattern.getHeight(); j++) {
                if(pattern.read(i,j) == 'o'){population++;}
            }
        }
        return population;
    }
}
