import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class dataset {
    // public final so it doesn't get accidently modified

    public final ArrayList<breastData> data;

    // Takes in a filename, depending on how u run the program, it'll either be:
    // "src/main/java/Breast_train.csv" or "Breast_train.csv"
    public dataset(String filename) {
        File file = new File(filename);
        data = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(file);

            // Skip the header line
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line == "") break;

                data.add(new breastData(line));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }   
    }
}
