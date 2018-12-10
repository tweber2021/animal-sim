class Animal {

    private Genes genes;
    private int x;
    private int y;
    private int energy;
    private int ID;
    private int kills;
    private int age;
    private char symbol;
    private boolean alive;


    // Establish constants for movement and attacks
    enum Move{UP, DOWN, LEFT, RIGHT, STAND}
    enum Attack{ROCK, PAPER, SCISSORS, NOTHING}
    // enum Ability{SPEED, L2, L3, PACK, BLASTER, W3, BUILD, PIZZA, B3} // List of animal abilities. 2 character names are placeholders.

    Animal(int ID, char symbol, int x, int y, byte[] code){
        this.genes = new Genes(code);
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.symbol = symbol;
        energy = 2000;
        age = 0;
        alive = true;
    }

    // TODO: Abilities

    Animal(Animal otherAnimal){ // Clone an animal
        this.genes = new Genes(otherAnimal.genes.getCode(),0);
        this.x = otherAnimal.x;
        this.y = otherAnimal.y;
        this.ID = otherAnimal.ID;
        this.symbol = otherAnimal.symbol;
        this.energy = otherAnimal.energy;
        this.kills = otherAnimal.kills;
        this.age = otherAnimal.age;
        this.alive = otherAnimal.alive;
    }

    Move move(char[][] surroundings){
        age++;
        return genes.getMove(energy, surroundings,age);
    }

    Attack attack(char opponent){
        return genes.getAttack(opponent);
    }

    final void die(){
        alive = false;
    }

    final int getX(){
        return x;
    }

    final int getY(){
        return y;
    }

    final int getID(){
        return ID;
    }

    final void setPos(int x,int y){
        this.x = x;
        this.y = y;
    }

    final boolean isAlive(){
        return alive;
    }

    final char getSymbol(){
        return symbol;
    }

    final int getEnergy(){
        return energy;
    }

    final void setEnergy(int energy){
        this.energy = energy;
    }

    final void incKills(){this.kills++;}

    final int getKills(){
        return kills;
    }
}
