class Game {
    private boolean fastForward = false; // give our toggle speed method access to the speed
    private boolean visible;
    private static char[] TEAMS = new char[]{'s', 'm', 'c'};

    Game(boolean visible) {
        this.visible = visible;
    }

    char run() { // Future (outside of demo build): return array of top genes to be manipulated
        int width = 90; // Original: 112
        int height = (int) (0.4375 * width);
        SimWindow window = new SimWindow("AnimalSim", width); // construct a SimWindow, 7:16
        Map map = new Map(width, height);
        if (visible) {
            window.setVisible(true);
            window.setMap(map);
            window.sidePrintln("Animal Simulation");
        }

        Animal[] animals = new Animal[300];

        for (int i = 0; i < animals.length; i++) {
            if (i < animals.length / 3) {
                animals[i] = new Mouse(i, (int) (Math.random() * width), (int) (Math.random() * height), new Genes(new byte[]{0}));
            } else if (i < (animals.length / 3) * 2) {
                animals[i] = new Cat(i, (int) (Math.random() * width), (int) (Math.random() * height), new Genes(new byte[]{0}));
            } else {
                animals[i] = new Snake(i, (int) (Math.random() * width), (int) (Math.random() * height), new Genes(new byte[]{0}));
            }
        }

        if (visible) {
            window.fastForwardButton.addActionListener(e -> {
                toggleSpeed();
                if (window.fastForwardButton.getText().equals("Fast Forward")) {
                    window.fastForwardButton.setText("Normal Speed");
                } else {
                    window.fastForwardButton.setText("Fast Forward");
                }
            });
        }

        while (remainingTeams(animals) > 1) { // Check if at least two teams are playing
            long time = System.currentTimeMillis();
            for (Animal animal : animals) {
                if (animal.isAlive()) {
                    refreshMap(map, animals);
                    moveAnimal(animal, map, animals, animal.move(map.getSurroundings(animal.getX(), animal.getY())), width, height);
                }
            }
            // Refresh display
            refreshMap(map, animals);
            if (visible) {
                window.setMap(map);
                refreshSideText(animals, window);
            }
            map.clear();

            if (!fastForward && visible) {
                wait((int) (80 - (System.currentTimeMillis() - time)));
            } // Let 80ms pass before the next moves
        }
        if (visible) {
            refreshMap(map, animals);
            window.setMap(map);
            window.sidePrintln("Game Over", 2);
            window.fastForwardButton.setEnabled(false);
            window.updateSideText();
        }
        return getWinner(animals);
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

    private static void moveAnimal(Animal animal, Map map, Animal[] animals, Animal.Move move, int xlim, int ylim) {
        if (animal.getEnergy() <= 0) {
            animal.die();
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

        if (move != Animal.Move.STAND) {
            animal.setEnergy(animal.getEnergy() - 10);
        } // Lose energy, sleeping uses less
        else {
            animal.setEnergy(animal.getEnergy() - 3);
        }

        int destID = map.readID(desiredX, desiredY);
        if (destID != -1 && destID != animal.getID() && animals[destID].getSymbol() != animal.getSymbol()) { // If another animal with a different species is in the same space...
            // Fight!
            Animal defender = animals[map.readID(desiredX, desiredY)];
            if (rockPaperScissors(animal.attack(defender.getSymbol()), defender.attack(animal.getSymbol()))) { // If the attacker (this animal) wins
                defender.die();
                animal.setEnergy(animal.getEnergy() + defender.getEnergy() / 2); // Winner gets half of the opponents energy
                animal.incKills();
            } else {
                animal.die();
                defender.setEnergy(defender.getEnergy() + animal.getEnergy() / 2);
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
            return Math.random() > 0.5;
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

    private static int getTeamMaxEnergy(Animal[] animals, char team) { // Get the energy of the animal with the most energy on the team
        int maxEnergy = 0;
        for (Animal animal : animals) {
            if (animal.isAlive() && animal.getSymbol() == team && animal.getEnergy() > maxEnergy) {
                maxEnergy = animal.getEnergy();
            }
        }
        return maxEnergy;
    }

    private static int getTeamMaxKills(Animal[] animals, char team){
        int maxKills = 0;
        for (Animal animal : animals) {
            if (animal.getSymbol() == team && animal.getKills() > maxKills) {
                maxKills = animal.getKills();
            }
        }
        return maxKills;
    }

    private static void refreshSideText(Animal[] animals, SimWindow window) { // At least it's easy to debug ¯\_(ツ)_/¯
        window.clearSideText();
        window.sidePrintln("Animal Simulation");
        window.sidePrintln(countAlive(animals) + " Alive", 1);
        window.sidePrintln(countAlive(animals, 'm') + " Mice");
        window.sidePrintln(countAlive(animals, 'c') + " Cats");
        window.sidePrintln(countAlive(animals, 's') + " Snakes");
        window.sidePrintln("Highest Energy Left", 1);
        window.sidePrintln("Mice: " + getTeamMaxEnergy(animals, 'm') + " E");
        window.sidePrintln("Cats: " + getTeamMaxEnergy(animals, 'c') + " E");
        window.sidePrintln("Snakes: " + getTeamMaxEnergy(animals, 's') + " E");
        window.sidePrintln("Highest Kills", 1);
        window.sidePrintln("Mice: "+ getTeamMaxKills(animals, 'm'));
        window.sidePrintln("Cats: "+ getTeamMaxKills(animals, 'c'));
        window.sidePrintln("Snakes: "+ getTeamMaxKills(animals, 's'));
        window.updateSideText();
    }

    private void toggleSpeed() {
        fastForward = !fastForward;
    }

    private char getWinner(Animal[] animals){
        for (char team : TEAMS) {
            if (countAlive(animals, team) > 0) {
                return team;
            }
        }
        return ' ';
    }
}
