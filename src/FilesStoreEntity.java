import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FilesStoreEntity implements Serializable {

    private Date addTime;
    private String peerId;
    private String fileServerAddress;
    private Integer fileServerPort;

    private Map<String,List<String>> pathFilesMapping;

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getFileServerAddress() {
        return fileServerAddress;
    }

    public void setFileServerAddress(String fileServerAddress) {
        this.fileServerAddress = fileServerAddress;
    }

    public Integer getFileServerPort() {
        return fileServerPort;
    }

    public void setFileServerPort(Integer fileServerPort) {
        this.fileServerPort = fileServerPort;
    }

    public Map<String, List<String>> getPathFilesMapping() {
        return pathFilesMapping;
    }

    public void setPathFilesMapping(Map<String, List<String>> pathFilesMapping) {
        this.pathFilesMapping = pathFilesMapping;
    }
}
