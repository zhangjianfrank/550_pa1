import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FilesStoreEntity implements Serializable {
    private List<String> files;
    private Date addTime;
    private String peerId;
    private String fileServerAddress;
    private Integer fileServerPort;
    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

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
}
