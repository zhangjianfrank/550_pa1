import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static DirectoryEntity listFilesInDirectoryOrFile(String filePath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(filePath);
        DirectoryEntity directoryEntity =null;
        if (directory.exists()) {
            directoryEntity = new DirectoryEntity();
            if(directory.isDirectory()){
                directoryEntity.setFileDirectory(directory.getPath()+ File.separator);
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
                directoryEntity.setFileDirectory(directory.getParent() +File.separator);
                fileNames.add(directory.getName());
            }
            directoryEntity.setFileNames(fileNames);
        }

        return directoryEntity;
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

    public static class DirectoryEntity{
        private List<String> fileNames;
        private String fileDirectory;

        public List<String> getFileNames() {
            return fileNames;
        }

        public void setFileNames(List<String> fileNames) {
            this.fileNames = fileNames;
        }

        public String getFileDirectory() {
            return fileDirectory;
        }

        public void setFileDirectory(String fileDirectory) {
            this.fileDirectory = fileDirectory;
        }
    }
}