class Snake extends Animal {
    private int age =0;
    Snake(int ID, int x, int y, Genes genes) {
        super(ID, x, y, genes);
        setSymbol('s');
    }

    Move move(char[][] surroundings){
        age++;
        if(!anyMice(surroundings)) {
            if (age % 2 == 0) {
                return Move.DOWN;
            } // Slithering pattern
            else {
                return Move.RIGHT;
            }
        }
        return Move.STAND;
    }

    Attack attack(char opponent){ // Attack with paper or scissors
        return Attack.SCISSORS;
    }

    private boolean anyMice(char[][] surroundings){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(surroundings[i][j]=='m'){return true;}
            }
        }
        return false;
    }
}
