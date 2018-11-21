class Cat extends Animal {
    Cat(int ID, int x, int y, Genes genes) {
        super(ID, x, y, genes);
        setSymbol('c');
    }

    Move move(char[][] surroundings){ // Attack boars
        if(surroundings[0][1]=='m'){ // Left
            return Move.LEFT;
        }
        if(surroundings[2][1]=='m'){ // Right
            return Move.RIGHT;
        }
        if(surroundings[1][0]=='m'){ // Up
            return Move.UP;
        }
        if(surroundings[1][2]=='m'){ // Down
            return Move.DOWN;
        }

        // Avoid Snakes
        if(anySnakes(surroundings)){
            return Move.DOWN;
        }
        return Move.STAND; // Sleep if nothing is found
    }

    Attack attack(char opponent){ // Attack with paper
        return Attack.PAPER;
    }

    private boolean anySnakes(char[][] surroundings){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(surroundings[i][j]=='s'){return true;}
            }
        }
        return false;
    }
}
