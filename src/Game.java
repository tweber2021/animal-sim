import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Game {
    private boolean fastForward; // give our toggle speed method access to the speed
    private boolean visible;
    private static char[] TEAMS = new char[]{'B', 'W', 'L'};
    private int width;
    private int numAnimals;
    private Animal[] placement;
    private int placementPos = 0;
    private GameOfLife conway;
    private boolean combatEnabled;

    Game(boolean visible, int width, int numAnimals, boolean combatEnabled, GameOfLife conway) {
        this.visible = visible;
        this.width = width;
        this.numAnimals = numAnimals;
        this.conway = new GameOfLife(conway.getInitialPattern());
        this.combatEnabled = combatEnabled;
        fastForward = false;
    }

    Animal[] run(Genes[] genePool) { // Run games for a certain number of iterations
        placement = new Animal[numAnimals];
        int height = (int) (0.4375 * width);
        SimWindow window = new SimWindow("AnimalSim", width); // construct a SimWindow, 7:16 textbox ratio
        Map map = new Map(width, height);

        if (visible) {
            window.setVisible(true);
            window.setClosable(false); // Don't allow the user to close the window while taking pattern input
            window.setMap(map);
            window.sidePrintln("Animal Simulation");
            window.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    visible = false;
                }
            });
        }

        Animal[] animals = new Animal[numAnimals];

        int totalEnergy;
        if(combatEnabled){
            totalEnergy = 2000;
        }
        else{
            totalEnergy = 50000; // Emphasis on movement, requires more energy
        }
        for (int i = 0; i < animals.length; i++) {
            animals[i] = new Animal(i, (int)(Math.random() * width), (int)(Math.random() * height), totalEnergy, genePool[i].getCode());
        }
        window.centerView();

        if (visible) {
            window.fastForwardButton.addActionListener(e -> {
                toggleSpeed();
                if (window.fastForwardButton.getText().equals("Fast Forward")) {
                    window.fastForwardButton.setText("Normal Speed");
                } else {
                    window.fastForwardButton.setText("Fast Forward");
                }
            });
            window.setClosable(true);
        }

        while ((remainingTeams(animals) > 1||!combatEnabled) && countAlive(animals) > 1) { // Check if at least two teams are playing, or combat is disabled to keep going
            long time = System.currentTimeMillis();
            refreshMap(map, animals);
            map.overlay(conway.getPattern());
            for (Animal animal : animals) {
                if (animal.isAlive()) {
                    moveAnimal(animal, map, animals, animal.move(map.getSurroundings(animal.getX(), animal.getY())), width, height);
                    if(animal.getSkill() == 0){ // Speed ability
                        moveAnimal(animal, map, animals, animal.move(map.getSurroundings(animal.getX(), animal.getY())), width, height);
                    }
                }
            }

            // Refresh display
            refreshMap(map, animals);
            doConwayStuff(animals, map, conway);
            if (visible) {
                window.setMap(map);
                refreshSideText(animals, conway, window);
            }
            map.clear();

            if (!fastForward && visible) {
                wait((int) (80 - (System.currentTimeMillis() - time)));
            } // Let 80ms pass before the next moves
        }
        if (visible) {
            refreshMap(map, animals);
            doConwayStuff(animals, map, conway);
            window.setMap(map);
            window.sidePrintln("Game Over", 2);
            window.fastForwardButton.setEnabled(false);
            window.updateSideText();
        }
        addWinnersToPlacement(animals);
        return placement;
    }

    private static int countAlive(Animal[] animals) {
        int count = 0;
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                count++;
            }
        }
        return count;
    }

    private static int countAlive(Animal[] animals, char symbol) {
        int count = 0;
        for (Animal animal : animals) {
            if (animal.isAlive() && animal.getSymbol() == symbol) {
                count++;
            }
        }
        return count;
    }

    private static void refreshMap(Map map, Animal[] animals) {
        map.clear();
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                map.set(animal.getX(), animal.getY(), animal.getSymbol(), animal.getID());
            }
        }
    }

    private void moveAnimal(Animal animal, Map map, Animal[] animals, Animal.Move move, int xlim, int ylim) {
        if (animal.getEnergy() <= 0 && animal.isAlive()) {
            terminate(animal);
        } // If no energy, die
        int desiredX = animal.getX();
        int desiredY = animal.getY();
        switch (move) {
            case UP:
                desiredY--;
                break;
            case RIGHT:
                desiredX++;
                break;
            case DOWN:
                desiredY++;
                break;
            case LEFT:
                desiredX--;
                break;
            case STAND:
                break;
        }
        if (desiredX < 0) {
            desiredX = xlim - 1;
        }
        if (desiredX >= xlim) {
            desiredX = 0;
        } // Screen wrap
        if (desiredY < 0) {
            desiredY = ylim - 1;
        }
        if (desiredY >= ylim) {
            desiredY = 0;
        }

        int energyUsage;
        if (move != Animal.Move.STAND) {
            energyUsage = 10;
        } // Lose energy, sleeping uses less
        else {
            energyUsage = 5;
        }
        if(animal.getSkill() == 2){ // Energy efficiency ability
            energyUsage /= 2;
        }
        animal.setEnergy(animal.getEnergy() - energyUsage);

        int destID = map.readID(desiredX, desiredY);
        if (destID != -1 && destID != animal.getID() && animals[destID].getSymbol() != animal.getSymbol() && combatEnabled) { // If another animal with a different species is in the same space...
            // Fight!
            Animal defender = animals[map.readID(desiredX, desiredY)];
            if (rockPaperScissors(animal.attack(defender.getSymbol()), defender.attack(animal.getSymbol()))) { // If the attacker (this animal) wins
                terminate(defender);
                animal.setEnergy((int) (animal.getEnergy() + defender.getEnergy() * 0.80)); // Winner gets half of the opponents energy
                animal.incKills();
            } else {
                terminate(animal);
                defender.setEnergy((int) (defender.getEnergy() + animal.getEnergy() * 0.80));
                defender.incKills();
            } // Otherwise, RIP
        }

        animal.setPos(desiredX, desiredY);
    }

    private static void wait(int ms) {
        if (ms < 1) {
            return;
        }
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }

    private static boolean rockPaperScissors(Animal.Attack attack, Animal.Attack defense) { // Returns whether the attacker wins
        if (attack == defense) {
            return true; // Attacker has advantage
        } // 50/50 if there's a tie
        switch (attack) {
            case ROCK:
                return defense == Animal.Attack.SCISSORS;
            case PAPER:
                return defense == Animal.Attack.ROCK;
            case SCISSORS:
                return defense == Animal.Attack.PAPER;
        }
        return false;
    }

    private static int remainingTeams(Animal[] animals) {
        int numTeams = 0;
        for (char team : TEAMS) {
            if (countAlive(animals, team) > 0) {
                numTeams++;
            }
        }
        return numTeams;
    }

    private void addWinnersToPlacement(Animal[] animals){
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                addToPlacement(animal);
            }
        }
    }

    private static int getTeamMaxEnergy(Animal[] animals, char team) { // Get the energy of the animal with the most energy on the team
        int maxEnergy = 0;
        for (Animal animal : animals) {
            if (animal.isAlive() && animal.getSymbol() == team && animal.getEnergy() > maxEnergy) {
                maxEnergy = animal.getEnergy();
            }
        }
        return maxEnergy;
    }

    private static int getTeamMaxKills(Animal[] animals, char team) {
        int maxKills = 0;
        for (Animal animal : animals) {
            if (animal.getSymbol() == team && animal.getKills() > maxKills) {
                maxKills = animal.getKills();
            }
        }
        return maxKills;
    }

    private static void refreshSideText(Animal[] animals, GameOfLife conway, SimWindow window) { // At least it's easy to debug ¯\_(ツ)_/¯
        window.clearSideText();
        window.sidePrintln("Animal Simulation");
        window.sidePrintln(countAlive(animals) + " Alive", 1);
        window.sidePrintln(countAlive(animals, 'W') + " Wolves");
        window.sidePrintln(countAlive(animals, 'L') + " Lions");
        window.sidePrintln(countAlive(animals, 'B') + " Bears");
        window.sidePrintln("Highest Energy Left", 1);
        window.sidePrintln("Wolves: " + getTeamMaxEnergy(animals, 'W') + " E");
        window.sidePrintln("Lions: " + getTeamMaxEnergy(animals, 'L') + " E");
        window.sidePrintln("Bears: " + getTeamMaxEnergy(animals, 'B') + " E");
        window.sidePrintln("Highest Kills", 1);
        window.sidePrintln("Wolves: " + getTeamMaxKills(animals, 'W'));
        window.sidePrintln("Lions: " + getTeamMaxKills(animals, 'L'));
        window.sidePrintln("Bears: " + getTeamMaxKills(animals, 'B'));
        window.sidePrintln("Game Of Life", 1);
        window.sidePrintln("Generation: " + conway.getGeneration());
        window.sidePrintln("Population: " + conway.getPopulation());
        window.sidePrintln("Kills: " + conway.getKills());
        window.updateSideText();
    }

    private void toggleSpeed() {
        fastForward = !fastForward;
    }

    private void doConwayStuff(Animal[] animals, Map map, GameOfLife conway) {
        map.overlay(conway.gen());
        for (Animal animal : animals) {
            if (map.read(animal.getX(), animal.getY()) == 'o' && animal.isAlive()) {
                terminate(animal);
                conway.incKills();
            }
        }
    }

    private void addToPlacement(Animal animal){
        if(animal.isAlive()){
            placement[placementPos] = new Animal(animal);
            placementPos++;
        }
    }

    private void terminate(Animal animal){
        if(animal.canUseSkill() && animal.getSkill() == 1){ // Extra life ability
            animal.useSkill();
        }
        else{
            addToPlacement(animal);
            animal.die();
        }
    }
}
