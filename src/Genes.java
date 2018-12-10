import java.util.Arrays;

class Genes {
    private byte[] code;

    // Constants:

    // Static offsets
    private final static int VERSION = 4;
    private final static int SKILL_1 = 9;
    private final static int SKILL_2 = 10;
    private final static int SKILL_3 = 11;
    private final static int LION_ATTACK = 16;
    private final static int WOLF_ATTACK = 17;
    private final static int BEAR_ATTACK = 18;
    private final static int MOVES_START = 23;

    // Additive offsets for Move chunks
    private final static int CONDITION_TYPE = 0;
    private final static int ENV_VAR_1 = 1;
    private final static int OPERATOR_1 = 2;
    private final static int SCALAR_1 = 3;
    private final static int ENV_VAR_2 = 4;
    private final static int OPERATOR_2 = 5;
    private final static int SCALAR_2 = 6;
    private final static int ACTION = 7;

    private final static byte[] staticGeneRange = new byte[]{
            // Individual byte gene limits go here. Zero indicates no change on mutation.
            0,0,0,0, // GENE
            0, // VERSION
            0,0,0,0, // SPEC
            0,0,0, // Skills, no change for now
            0,0,0,0, // ATCK
            3,3,3, // Attacks
            0,0,0,0, // MOVE
    };

    private final static byte[] moveChunkRange = new byte[]{
            // Individual byte gene limits go here. Zero indicates no change on mutation.
            3, // Conditional types
            20, // 20 environment variables
            4, // 4 comparison operators
            Byte.MAX_VALUE, // Scalar range
            20, // 20 environment variables
            4, // 4 comparison operators
            Byte.MAX_VALUE, // Scalar range
            5 // 8 different actions, but we're excluding special moves
    };

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

    final static byte[] TEST = new byte[]{ // Always move up
            71, 69, 78, 69, // GENE
            0, // Version 0
            83, 80, 69, 67, // SPEC
            0, 0, 0, // Skills
            65, 84, 67, 75, // ATCK
            1, 1, 1, // Attack paper for all enemies
            77, 79, 86, 69, // MOVE
            0, // Only evaluate first conditional
            0, 1, 1, // if zero is unequal to one (true)
            0, 0, 0, // we don't care about this because we're only doing one conditional
            1 // Move up
    };

    final static byte[] TEST2 = new byte[]{ // Alternate between moving right and down
            71, 69, 78, 69, // GENE
            0, // Version 0
            83, 80, 69, 67, // SPEC
            0, 0, 0, // Skills
            65, 84, 67, 75, // ATCK
            2, 2, 2, // Attack scissors for all enemies
            77, 79, 86, 69, // MOVE

            // Alternate between moving right and down

            // First conditional
            0, // Only evaluate first conditional
            11, 0, 0, // if time%2 == 0
            0, 0, 0, // we don't care about this because we're only doing one conditional
            2, // Move right

            // Second conditional
            0, // Condition 1 or Condition 2
            11, 0, 1, // if time%2 == 1
            0, 0, 0, // we don't care about this because we're only doing one conditional
            3 // Move down
    };

    Genes(byte[] code){
        this.code = Arrays.copyOf(code,code.length); // Prevent all animals from having the same genes
        this.mutate(0.2);
    }

    Genes(byte[] code, double mutationRate){
        this.code = Arrays.copyOf(code,code.length); // Prevent all animals from having the same genes
        this.mutate(mutationRate);
    }

    // TODO: Replication, error-checking function

    private byte getGene(int index){
        return code[index];
    }

    byte[] getCode(){
        return code;
    }

    Animal.Attack getAttack(char opponent){ // Return an animal's chosen attack based on the opposing species
        switch (opponent){
            case 'L': return Animal.Attack.values()[getGene(LION_ATTACK)];
            case 'W': return Animal.Attack.values()[getGene(WOLF_ATTACK)];
            case 'B': return Animal.Attack.values()[getGene(BEAR_ATTACK)];
            default: return Animal.Attack.values()[getGene(LION_ATTACK)];
        }
    }

    Animal.Move getMove(int energy, char[][] surroundings, int time){ // This is where the fun begins
        // The size of an proper genes object is 23 + 8c bytes
        if((code.length-23)%8!=0||code.length-23==0){ // If c is not divisible by 8 or is zero
            if((code.length-23)%8!=0){throw new IllegalArgumentException("Invalid Move Chunk Size.");} // Testing purposes
            return Animal.Move.STAND;
        }
        int chunks = (code.length-23)/8;
        //System.out.println(chunks+" chunks.");
        for (int i = 0; i < chunks; i++) { // Loop through all conditional chunks
            byte type = getChunkVal(i,CONDITION_TYPE);
            //System.out.println("Type "+type);
            boolean result_1 = evalCondition(getChunkVal(i,ENV_VAR_1),getChunkVal(i,OPERATOR_1),getChunkVal(i,SCALAR_1),energy,surroundings,time);
            boolean result_2 = evalCondition(getChunkVal(i,ENV_VAR_2),getChunkVal(i,OPERATOR_2),getChunkVal(i,SCALAR_2),energy,surroundings,time);
            boolean willAct;
            switch(type){
                case 0:
                    willAct = result_1;
                    break;
                case 1:
                    willAct = result_1 && result_2;
                    break;
                case 2:
                    willAct = result_1 || result_2;
                    break;
                default:
                    throw new IllegalArgumentException(type+" is an invalid conditional type.");
            }
            if(willAct){
                switch(getChunkVal(i,ACTION)){
                    case 0: return Animal.Move.STAND;
                    case 1: return Animal.Move.UP;
                    case 2: return Animal.Move.RIGHT;
                    case 3: return Animal.Move.DOWN;
                    case 4: return Animal.Move.LEFT;
                    default: throw new IllegalArgumentException(getChunkVal(i,ACTION)+" is an invalid action ID."); // Abilities to be added later
                }
            }
        }
        return Animal.Move.STAND;
    }

    private static boolean evalCondition(byte envVarID, byte operator, byte scalar, int energy, char[][] surroundings, int time){
        byte envVar;
        if(envVarID == 0){envVar = 0;} // Needed to not throw our exception
        else if(envVarID == 1){
            envVar = (byte)(energy/100);
        }
        else if(envVarID>1 && envVarID<11){
            envVar = (byte)surroundings[(envVarID-2)%3][(envVarID-2)/3]; // Automatically converted into character code
        }
        else if(envVarID>10 && envVarID<20){
            envVar = (byte) (time%(envVarID-9));
        }
        else{throw new IllegalArgumentException(envVarID+" is an invalid environment variable ID.");}
        switch(operator){
            case 0: return envVar == scalar;
            case 1: return envVar != scalar;
            case 2: return envVar > scalar;
            case 3: return envVar < scalar;
            default: throw new IllegalArgumentException(operator+" is an invalid operator ID.");
        }
    }

    private byte getChunkVal(int chunkNum, int relativePos){ // Get the offset for a value in a move chunk
        return getGene(getAbsPos(chunkNum,relativePos));
    }

    private int getAbsPos(int chunkNum, int relativePos){
        return MOVES_START+(chunkNum*8)+relativePos;
    }

    // Animal.Ability getAbility(int abilityID){}

    private void mutate(double mutationRate){
        // Chunk insertion/deletion
        if(Math.random()<mutationRate/10){
            addChunk();
        }
        else if(Math.random()<mutationRate/15 && code.length>23){ // Cannot add and remove chunks at the same time
            removeChunk();
        }

        // Simple Mutations
        for (int i = 0; i < MOVES_START; i++) {
            if(Math.random()<mutationRate && staticGeneRange[i]>0){
                code[i] = (byte)(Math.random()*staticGeneRange[i]);
            }
        }
        for (int i = 0; i < (code.length - MOVES_START)/8; i++) { // Iterate through movement chunks
            for (int j = 0; j < 8; j++) {
                if(Math.random()<mutationRate){ // Everything in the movement chunk is subject to mutation
                    code[getAbsPos(i,j)] = (byte)(Math.random()*moveChunkRange[j]);
                }
            }
        }
    }

    private void addChunk(/*int chunkID*/){
        //int chunks = (code.length-23)/8;
        //if(chunkID>=chunks){throw new IllegalArgumentException("Chunk "+chunkID+" is out of range "+chunks);}
        byte[] extendedCode = new byte[code.length+8];
        System.arraycopy(code, 0, extendedCode, 0, code.length);
        for (int i = 0; i < 8; i++) {
            extendedCode[code.length+i] = (byte)(Math.random()*moveChunkRange[i]);
        }
        code = Arrays.copyOf(extendedCode, extendedCode.length);
    }

    private void removeChunk(/*int chunkID*/){
        //int chunks = (code.length-23)/8;
        //if(chunkID>=chunks){throw new IllegalArgumentException("Chunk "+chunkID+" is out of range "+chunks);}
        byte[] shortenedCode = new byte[code.length-8];
        System.arraycopy(code, 0, shortenedCode, 0, code.length-8);
        code = Arrays.copyOf(shortenedCode, shortenedCode.length);

    }



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
                          Env. Variable 1     1         0-10        The ID of a given environment variable. (Condition 1)
                          Comparison Op. 1    1         0-3         An operator to compare the environment variable to the constant. (Condition 1)
                          Scalar 1            1         Full        A constant to compare the environment variable to. (Condition 1)
                          Env. Variable 2     1         0-10
                          Comparison Op. 2    1         0-3
                          Scalar 2            1         Full
                          Action              1         0-7         An action to execute if the conditional evaluates to be true.
      conditional2*
      conditional3*
          ...

          *: Sub-chunk

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
            1: Remaining Energy/100 (cap at 12700)
            2: Surroundings[0][0]
            3: Surroundings[1][0]
            4: Surroundings[2][0]
            5: Surroundings[0][1]
            6: Surroundings[1][1]
            7: Surroundings[2][1]
            8: Surroundings[0][2]
            9: Surroundings[1][2]
            10: Surroundings[2][2]
            11: Time%2
            12: Time%3
            13: Time%4
            14: Time%5
            15: Time%6
            16: Time%7
            17: Time%8
            18: Time%9
            19: Time%10

            Action IDs:
            0: Stand
            1: Move up
            2. Move right
            3. Move down
            4. Move left
            5. Ability 1
            6. Ability 2
            7. Ability 3

    */


}
