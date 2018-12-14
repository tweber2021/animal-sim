class Animal {

    private Genes genes;
    private int x;
    private int y;
    private int energy;
    private int ID;
    private int kills;
    private int age;
    private char symbol;
    private int skill;
    private boolean alive;
    private boolean canUseSkill = true;


    // Establish constants for movement and attacks
    enum Move{UP, DOWN, LEFT, RIGHT, STAND}
    enum Attack{ROCK, PAPER, SCISSORS, NOTHING}

    Animal(int ID, int x, int y, byte[] code){
        this.genes = new Genes(code);
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.symbol = (char)code[4];
        this.skill = code[9];
        energy = 2000;
        age = 0;
        alive = true;
    }

    Animal(Animal otherAnimal){ // Clone an animal
        this.genes = new Genes(otherAnimal.genes.getCode());
        this.x = otherAnimal.x;
        this.y = otherAnimal.y;
        this.ID = otherAnimal.ID;
        this.symbol = otherAnimal.symbol;
        this.skill = otherAnimal.skill;
        this.energy = otherAnimal.energy;
        this.kills = otherAnimal.kills;
        this.age = otherAnimal.age;
        this.alive = otherAnimal.alive;
        this.canUseSkill = otherAnimal.canUseSkill;
    }

    Move move(char[][] surroundings){
        age++;
        return genes.getMove(energy, surroundings,age);
    }

    Attack attack(char opponent){
        Attack chosenAttack = genes.getAttack(opponent);
        // Force animals to only be able to throw two types of moves to give the species themselves a rock-paper-scissors dynamic
        // Animals can only throw attacks neutral or weak to the animal they're weak against, to encourage them to avoid that animal
        if(symbol == 'W' && chosenAttack == Attack.ROCK){
            chosenAttack = Attack.SCISSORS;
        }
        else if(symbol == 'L' && chosenAttack == Attack.PAPER){
            chosenAttack = Attack.ROCK;
        }
        else if(symbol == 'B' && chosenAttack == Attack.SCISSORS){
            chosenAttack = Attack.PAPER;
        }
        return chosenAttack;
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

    final int getAge(){
        return age;
    }

    final Genes getGenes(){
        return genes;
    }

    final int getSkill() {
        return skill;
    }

    final boolean canUseSkill(){
        return canUseSkill;
    }

    final void useSkill(){
        canUseSkill = false;
    }
}
