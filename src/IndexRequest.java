import java.io.Serializable;
import java.util.ArrayList;

public class IndexRequest implements Serializable {
    /**
     * Request type
     * 1: register, 2: unregister, 3: lookup,
     */
    private Integer requestType;

    private IndexRegister indexRegister;

    private IndexSearch indexSearch;

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public IndexRegister getIndexRegister() {
        return indexRegister;
    }

    public void setIndexRegister(IndexRegister indexRegister) {
        this.indexRegister = indexRegister;
    }

    public IndexSearch getIndexSearch() {
        return indexSearch;
    }

    public void setIndexSearch(IndexSearch indexSearch) {
        this.indexSearch = indexSearch;
    }

    public static class IndexRegister implements Serializable{
        private String peerId;
        private ArrayList<String> files;

        public String getPeerId() {
            return peerId;
        }

        public void setPeerId(String peerId) {
            this.peerId = peerId;
        }

        public ArrayList<String> getFiles() {
            return files;
        }

        public void setFiles(ArrayList<String> files) {
            this.files = files;
        }
    }

    public static class IndexSearch implements Serializable{
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }


}
