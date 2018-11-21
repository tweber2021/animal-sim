class Snake extends Animal {
    private int age =0;
    Snake(int ID, int x, int y, Genes genes) {
        super(ID, x, y, genes);
        setSymbol('s');
    }

    Move move(char[][] surroundings){
        age++;
        if(age %2==0){return Move.DOWN;} // Slithering pattern
        else{return Move.RIGHT;}
    }

    Attack attack(char opponent){ // Attack with paper or scissors
        return Attack.SCISSORS;
    }
}
