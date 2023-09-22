import java.io.Serializable;
import java.util.ArrayList;

public class IndexResponse implements Serializable {
    private boolean suc;
    private String code;
    private String message;
    private ResultData data;
    public boolean isSuc() {
        return suc;
    }

    public void setSuc(boolean suc) {
        this.suc = suc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;

    }

    public ResultData getData() {
        return data;
    }

    public void setData(ResultData data) {
        this.data = data;
    }

    public static class ResultData implements Serializable{
        private String peerId;
        /**
         * register use
         */
        private ArrayList<String> files;

        private String fileServerIp;


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

        public String getFileServerIp() {
            return fileServerIp;
        }

        public void setFileServerIp(String fileServerIp) {
            this.fileServerIp = fileServerIp;
        }
    }


    public static IndexResponse failedResp(String message){
        IndexResponse indexResponse = new IndexResponse();
        indexResponse.setSuc(false);
        indexResponse.setCode("500");
        indexResponse.setMessage(message);
        return indexResponse;
    }

    public static IndexResponse sucResp(ResultData data){
        IndexResponse indexResponse = new IndexResponse();
        indexResponse.setSuc(true);
        indexResponse.setCode("200");
        indexResponse.setData(data);
        return indexResponse;
    }
    public static IndexResponse sucResp(String message){
        IndexResponse indexResponse = new IndexResponse();
        indexResponse.setSuc(true);
        indexResponse.setCode("200");
        indexResponse.setMessage(message);
        return indexResponse;
    }
}
