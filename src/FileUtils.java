import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> listFilesInDirectoryOrFile(String filePath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(filePath);

        if (directory.exists() ) {
            if(directory.isDirectory()){
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            fileNames.add(file.getName());
                        }
                    }
                }
            }
            if(directory.isFile()){
                fileNames.add(directory.getName());
            }
        }

        return fileNames;
    }

    public static void readAndOutputFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}