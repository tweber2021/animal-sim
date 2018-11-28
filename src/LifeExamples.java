class LifeExamples { // Stores list of life examples and methods for easy access
    private static String[] exampleTitles = new String[]{
            "R-Pentomino",
            "Gosper Glider Gun",
            "Backrake",
            "119P4H1V0",
            "295P5H1V1",
            "4-8-12 Diamond",
            "Enterprise",
            "Gliders by the Dozen",
            "Orion"
    };
    private static String[] examples = new String[]{ // I was too lazy to write a file parser, for now anyway
            ".oo\n" +
                    "oo\n" +
                    ".o",
            "........................O\n" +
                    "......................O.O\n" +
                    "............OO......OO............OO\n" +
                    "...........O...O....OO............OO\n" +
                    "OO........O.....O...OO\n" +
                    "OO........O...O.OO....O.O\n" +
                    "..........O.....O.......O\n" +
                    "...........O...O\n" +
                    "............OO",
            ".....OOO...........OOO\n" +
                    "....O...O.........O...O\n" +
                    "...OO....O.......O....OO\n" +
                    "..O.O.OO.OO.....OO.OO.O.O\n" +
                    ".OO.O....O.OO.OO.O....O.OO\n" +
                    "O....O...O..O.O..O...O....O\n" +
                    "............O.O\n" +
                    "OO.......OO.O.O.OO.......OO\n" +
                    "............O.O\n" +
                    "......OOO.........OOO\n" +
                    "......O...O.........O\n" +
                    "......O.O....OOO\n" +
                    "............O..O....OO\n" +
                    "...............O\n" +
                    "...........O...O\n" +
                    "...........O...O\n" +
                    "...............O\n" +
                    "............O.O",
            ".................................O.\n" +
                    "................O...............O.O\n" +
                    "......O.O......O.....OO........O...\n" +
                    "......O....O....O.OOOOOO....OO.....\n" +
                    "......O.OOOOOOOO..........O..O.OOO.\n" +
                    ".........O.....O.......OOOO....OOO.\n" +
                    "....OO.................OOO.O.......\n" +
                    ".O..OO.......OO........OO..........\n" +
                    ".O..O..............................\n" +
                    "O..................................\n" +
                    ".O..O..............................\n" +
                    ".O..OO.......OO........OO..........\n" +
                    "....OO.................OOO.O.......\n" +
                    ".........O.....O.......OOOO....OOO.\n" +
                    "......O.OOOOOOOO..........O..O.OOO.\n" +
                    "......O....O....O.OOOOOO....OO.....\n" +
                    "......O.O......O.....OO........O...\n" +
                    "................O...............O.O\n" +
                    ".................................O.",
            ".............OO.....................................\n" +
                    ".....OO....OO.O.O...................................\n" +
                    "....OOO....OOOO.....................................\n" +
                    "...OO......OO.....O.................................\n" +
                    "..OO..OO...O..O..O..................................\n" +
                    ".OO.....O.......O..OO...............................\n" +
                    ".OO.O...OOOO........................................\n" +
                    "....O...OO..OO.O....................................\n" +
                    ".....OOO....O.O.....................................\n" +
                    "......OO...OO..O....................................\n" +
                    "......O.....O.......................................\n" +
                    ".OOOO.O..O..O...O...................................\n" +
                    ".OOO...OOOOO..OOOOOOO.O.............................\n" +
                    "O.O....O..........O..OO.............................\n" +
                    "OOO.O...O...O.....OOO...............................\n" +
                    ".......O.O..O.......OO..............................\n" +
                    ".O...O.....OO........OO..O.O........................\n" +
                    "....O.......O........OOO.O.OOO......................\n" +
                    "...O........OOO......O....O.........................\n" +
                    ".....O......O.O.....O.O.............................\n" +
                    ".....O......O.OO...O....O...........................\n" +
                    ".............O.OOOO...O.....O..O....................\n" +
                    "............OO..OO.O.O...O.OOO......................\n" +
                    ".................O......O..OOO...OOO................\n" +
                    "....................O..O......OO....................\n" +
                    "................OO....O..O..........OO..............\n" +
                    "..................O.............O...O...............\n" +
                    "................OO....OO........O...................\n" +
                    ".................O...OOO........O.O.O.O.............\n" +
                    ".................O....OO........O.....OO............\n" +
                    "........................O........O..OOO.............\n" +
                    ".....................O..O........O........O.........\n" +
                    "..........................OOOO........OO...O........\n" +
                    ".......................O......OO......OO...O........\n" +
                    ".......................O....O............O..........\n" +
                    ".......................O...............O............\n" +
                    ".........................OO.O.O.......O..O..........\n" +
                    ".........................O....O.........OOO.........\n" +
                    "............................OOO.OO..O...O...O.OO....\n" +
                    ".............................O..OO.O.....O...O..O...\n" +
                    ".....................................OO..O...O......\n" +
                    "..................................O.OO.OO.O..OO...O.\n" +
                    "...............................O.....O...O.......O.O\n" +
                    "................................OO............OO...O\n" +
                    "......................................O.......OO....\n" +
                    ".......................................OOO...OO..O..\n" +
                    "......................................O..O.OOO......\n" +
                    "......................................O....OO.......\n" +
                    ".......................................O............\n" +
                    "..........................................O..O......\n" +
                    ".........................................O..........\n" +
                    "..........................................OO........",
                    "....OOOO....\n" +
                    "............\n" +
                    "..OOOOOOOO..\n" +
                    "............\n" +
                    "OOOOOOOOOOOO\n" +
                    "............\n" +
                    "..OOOOOOOO..\n" +
                    "............\n" +
                    "....OOOO....",
            ".......OOO...........\n" +
                    ".....O.OO............\n" +
                    "....OOOO.............\n" +
                    "...OO.....O..........\n" +
                    "..OOO..O.O.O.........\n" +
                    ".OO...O.O..O.........\n" +
                    ".O.O.OOOOO...........\n" +
                    "OO.O.O...O...........\n" +
                    "O........OO..........\n" +
                    ".OO..O...O.O.........\n" +
                    "....OO..O.OO......O..\n" +
                    "...........OO.....OOO\n" +
                    "............O..OOO..O\n" +
                    "............O..O..OO.\n" +
                    ".............O.OO....\n" +
                    "............OO.......\n" +
                    "............OO.......\n" +
                    "...........O.........\n" +
                    "............O.O......\n" +
                    "...........O..O......\n" +
                    ".............O.......",
            "OO..O\n" +
                    "O...O\n" +
                    "O..OO",
            "...OO.........\n" +
                    "...O.O........\n" +
                    "...O..........\n" +
                    "OO.O..........\n" +
                    "O....O........\n" +
                    "O.OO......OOO.\n" +
                    ".....OOO....OO\n" +
                    "......OOO.O.O.\n" +
                    ".............O\n" +
                    "......O.O.....\n" +
                    ".....OO.O.....\n" +
                    "......O.......\n" +
                    "....OO.O......\n" +
                    ".......O......\n" +
                    ".....OO......."
    };

    static String getExample(int ID){
        return examples[ID];
    }

    static String getExampleTitle(int ID){
        return exampleTitles[ID];
    }

    static int getNumExamples(){
        return exampleTitles.length;
    }
}