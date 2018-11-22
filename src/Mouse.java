class Mouse extends Animal {
    private byte cycle;
    Mouse(int ID, int x, int y, Genes genes) {
        super(ID, x, y, genes);
        setSymbol('m');

    }

    Move move(char[][] surroundings){ // Run in circles, kinda
        if(anyMice(surroundings)){return Move.STAND;} // Hide with other mice
        if(Math.random()<0.25){cycle++;}
        if(cycle>3){cycle=0;}
        switch(cycle){
            case 0: return Move.DOWN;
            case 1: return Move.RIGHT;
            case 2: return Move.UP;
            default: return Move.LEFT; // Address missing return statement
        }
    }

    Attack attack(char opponent){ // Attack with rock or scissors
        return Attack.ROCK;
    }

    private boolean anyMice(char[][] surroundings){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(surroundings[i][j]=='m'&&!(i==1&&j==1)){return true;}
            }
        }
        return false;
    }
}