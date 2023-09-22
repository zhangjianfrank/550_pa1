import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> listFilesInDirectory(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }

        return fileNames;
    }


}