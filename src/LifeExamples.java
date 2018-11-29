import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

class LifeExamples { // Stores list of life examples and methods for easy access
    /*private static String[] exampleTitles = new String[]{
            "R-Pentomino",
            "Gosper Glider Gun",
            "Backrake",
            "119P4H1V0",
            "295P5H1V1",
            "4-8-12 Diamond",
            "Enterprise",
            "Gliders by the Dozen",
            "Orion"
    };*/
    private static String[] exampleTitles = getExampleTitles();

    private static String getExamplePath(int ID){
        return "src/life/"+exampleTitles[ID]+".rle";
    }

    static String getExample(int ID){
        try {
            return readTextFile(ID);
        } catch (FileNotFoundException e) {
            return "Not Found";
        }
    }

    static String getExampleTitle(int ID){
        return exampleTitles[ID];
    }

    static int getNumExamples(){
        File folder = new File("src/life/");
        return Objects.requireNonNull(folder.list()).length; // Require non-null to avoid nullPointerException
    }

    private static String readTextFile(int ID) throws FileNotFoundException {
        return new Scanner(new File(getExamplePath(ID))).useDelimiter("\\A").next();
    }

    private static String[] getExampleTitles(){
        String[] paths = new String[getNumExamples()];
        File folder = new File("src/life/");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".rle")); // Only include files with the RLE extension
        assert files != null; // Avoid another exception
        for (int i = 0; i < getNumExamples(); i++) {
            String fileName = files[i].getName();
            paths[i] = fileName.substring(0,fileName.length()-4); // Cut off file extension
        }
        return paths;
    }
}
