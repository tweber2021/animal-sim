class Genes {
    private byte[] code;

    // Constants:

    // Offsets
    private final static int VERSION = 4;
    private final static int SKILL_1 = 9;
    private final static int SKILL_2 = 10;
    private final static int SKILL_3 = 11;
    private final static int LION_ATTACK = 16;
    private final static int WOLF_ATTACK = 17;
    private final static int BEAR_ATTACK = 18;

    // Lengths
    private final static int MAGIC_WORD_LENGTH = 4;
    private final static int CONDITIONAL_LENGTH = 8;


    // Genes
    final static byte[] TEMPLATE = new byte[]{ // Blank Slate
            71, 69, 78, 69, // GENE
            0, // Version 0
            83, 80, 69, 67, // SPEC
            0, 0, 0, // Skills
            65, 84, 67, 75, // ATCK
            0, 0, 0, // Attack rock for all enemies
            77, 79, 86, 69 // MOVE
            // Empty MOVE chunk
    };

    Genes(byte[] code){
        this.code = code;
    }

    // TODO: Determine environment variable and action IDs, reading and interpreting conditionals, replication with mutations

    private byte getGene(int index){
        return code[index];
    }

    Animal.Attack getAttack(char opponent){ // Return an animal's chosen attack based on the opposing species
        switch (opponent){
            case 'L': return Animal.Attack.values()[getGene(LION_ATTACK)];
            case 'W': return Animal.Attack.values()[getGene(WOLF_ATTACK)];
            case 'B': return Animal.Attack.values()[getGene(BEAR_ATTACK)];
            default: return Animal.Attack.values()[getGene(LION_ATTACK)];
        }
    }

    // Animal.Ability getAbility(int abilityID){}



    /* Genes Binary Format

      Chunk               Field             Length      Range       Contents

      HEADER
                          Magic Word          4         None        The character sequence "GENE".
                          Version             1         Full        Version of the data format.
      SKILL
                          Skill chunk ID      4         None        The character sequence "SPEC".
                          Skill 1             1         0-2         An animals's special ability.
                          Skill 2             1         0-2
                          Skill 3             1         0-2
      ATTACK
                          Attack chunk ID     4         None        The character sequence "ATCK".
                          Lion Attack         1         0-2         An attack thrown for encounters with lions.
                          Wolf Attack         1         0-2
                          Bear Attack         1         0-2
      MOVE
                          Move chunk ID       4         None        The character sequence "MOVE".
      conditional*
                          Format              1         0-2         The format of the conditional.
      condition1**
                          Env. Variable       1         ???         The ID of a given environment variable.
                          Comparison Op.      1         0-3         An operator to compare the environment variable to the constant.
                          Quantity            1         Full        A constant to compare the environment variable to.
      condition2**
      conditional* (cont)
                          Action              1         ???         An action to execute if the conditional evaluates to be true.
      conditional2*
      conditional3*
          ...

          *: Sub-chunk
          **: Sub-sub-chunk

          Size = 23 + 8c bytes, where c is equal to the number of conditionals present in the MOVE chunk.



            Possible Mutations:
            Byte Shift: A field that has any range outside of the HEADER is changed by ±1.
            Insertion: Inserts a randomized conditional chunk at the end of the MOVE chunk.
            Deletion: Removes a conditional chunk at the end of the MOVE chunk.

            All three mutation types have separate mutation rates - probabilities that they will happen.

            Conditional Formats:
            0: Condition 1
            1: Condition 1 && Condition 2
            2: Condition 1 || Condition 2

            Comparison Operators
            0: ==
            1: !=
            2: >
            3: <

            Environment Variables:
            0: Nothing
            1: Energy
            TODO

            Action IDs:
            TODO

    */


}
