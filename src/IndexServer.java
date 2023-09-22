import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexServer {

    private final ConcurrentMap<String,FilesStoreEntity> indexFilesStore = new ConcurrentHashMap<>();
    private final ConcurrentMap<String,String> searchFilesMapping = new ConcurrentHashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock rLock = rwLock.readLock();
    private final Lock wLock = rwLock.writeLock();
    private static final int DEFAULT_SERVER_PORT=8080;
    public static void main(String[] args) throws IOException {
        new IndexServer().startServer(DEFAULT_SERVER_PORT);
    }

    public void startServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService workerThreadPool = Executors.newFixedThreadPool(10);
        System.out.println("The index service is started successfully. port: "+DEFAULT_SERVER_PORT);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket);
                // 将客户端连接交给工作线程池处理
                workerThreadPool.submit(() -> handleClient(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket)  {
        try {

            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            String clientIp = clientSocket.getInetAddress().getHostAddress();

            this.writeSucResult(IndexResponse.sucResp("Connection established successfully"),outputStream);

            while(true){
                IndexRequest peerRequest = (IndexRequest) inputStream.readObject();

                if(peerRequest==null){
                    this.writeResult(false,null,"The request object is empty",outputStream);
                    return ;
                }

                IndexResponse indexResponse;
                switch (RequestTypeEnum.getEnumByCode(peerRequest.getRequestType())){
                    case REGISTER:
                        IndexRequest.IndexRegister indexRegister =  peerRequest.getIndexRegister();
                        if(indexRegister==null || indexRegister.getPeerId()==null || "".equals(indexRegister.getPeerId()) || indexRegister.getFiles()==null || indexRegister.getFiles().size()<1){

                            this.writeFailedResult("The request IndexRegister is empty",outputStream);
                            return ;
                        }

                        indexResponse = register(indexRegister.getPeerId(),clientIp,indexRegister.getFiles());
                        this.writeSucResult(indexResponse,outputStream);

                        break;
                    case UNREGISTER:
                        IndexRequest.IndexRegister unRegister =  peerRequest.getIndexRegister();
                        if(unRegister==null || unRegister.getPeerId()==null || "".equals(unRegister.getPeerId())  ){
                            this.writeFailedResult("The request IndexRegister is empty",outputStream);
                            return ;
                        }

                        indexResponse = unRegister(unRegister.getPeerId(),clientIp);

                        this.writeSucResult(indexResponse,outputStream);
                        break;
                    case LOOKUP:
                        IndexRequest.IndexSearch indexSearch =  peerRequest.getIndexSearch();
                        if(indexSearch==null || indexSearch.getFileName()==null){
                            this.writeFailedResult("The request IndexSearch is empty",outputStream);
                            return ;
                        }
                        indexResponse = lookup(indexSearch.getFileName());

                        this.writeSucResult(indexResponse,outputStream);
                        break;
                    case DISCONNECT:
                        this.writeSucResult(IndexResponse.sucResp(""),outputStream);
                        break;
                    default:
                        this.writeFailedResult("Unrecognized request type,requestType:"+peerRequest.getRequestType(),outputStream);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("server handleClient error,msg:"+e.getMessage());
            e.printStackTrace();
        }finally {
            if(clientSocket!=null){
                try{
                    clientSocket.close();
                }catch (IOException e){
                    System.out.println("Couldn't close a socket.");
                }

            }
        }
    }

    private void writeSucResult(IndexResponse indexResponse,ObjectOutputStream out) throws IOException {
        this.writeResult(true,indexResponse,null,out);
    }
    private void writeFailedResult(String message,ObjectOutputStream out) throws IOException {
        this.writeResult(false,null,message,out);
    }
    private void writeResult(boolean suc,IndexResponse indexResponse,String message,ObjectOutputStream out) throws IOException {
        if(suc){
            out.writeObject(indexResponse);
        }else{
            System.out.println(message);
            out.writeObject(IndexResponse.failedResp(message));
        }
    }

    private IndexResponse register(String peerId, String peerAddress, ArrayList<String> files) {
        System.out.println("Registering files:"+files+" from Peer(" + peerAddress+"), peerId:"+peerId);

        String peerKeyStr = "peerId:" + peerId + "_" + "ip:" + peerAddress;
        List<String> fileList;
        FilesStoreEntity filesStoreEntity;
        Date date = new Date();

        wLock.lock();
        try{
            if(indexFilesStore.containsKey(peerKeyStr)){
                filesStoreEntity = indexFilesStore.get(peerKeyStr);
                filesStoreEntity.setAddTime(date);
                filesStoreEntity.setClientIp(peerAddress);
                fileList = filesStoreEntity.getFiles();
            }else{
                fileList = new ArrayList<>(files);
                filesStoreEntity = new FilesStoreEntity();
                filesStoreEntity.setAddTime(date);
                filesStoreEntity.setClientIp(peerAddress);
                filesStoreEntity.setPeerId(peerId);
                filesStoreEntity.setFiles(fileList);
                indexFilesStore.put(peerKeyStr,filesStoreEntity);
            }

            if(fileList!=null && fileList.size()>0){
                for(String file:fileList){
                    searchFilesMapping.put(file,peerKeyStr);
                }

            }

        }catch (Exception e){
            System.out.println("save files to indexFilesStore error,error:"+e.getMessage());
            return IndexResponse.failedResp("save files to indexFilesStore error,error:"+e.getMessage());
        }finally {
            wLock.unlock();
        }

        System.out.println("The peer (ID:"+peerId+", IP:"+peerAddress+") sent "+files.size()+" files to the server. ");

        IndexResponse.ResultData resultData = new IndexResponse.ResultData();
        resultData.setPeerId(peerId);
        resultData.setFiles((ArrayList<String>) fileList);
        return IndexResponse.sucResp(resultData);

    }

    private IndexResponse unRegister(String peerId, String peerAddress) {

        String peerKey = "peerId:" + peerId + "_" + "ip:" + peerAddress;

        wLock.lock();
        try{
            FilesStoreEntity filesStoreEntity = indexFilesStore.remove(peerKey);
            System.out.println("indexFilesStore remove peerKey:"+ peerKey);
            if(filesStoreEntity!=null){
                List<String> fileList = filesStoreEntity.getFiles();
                if(fileList!=null && fileList.size()>0){
                    for(String file:fileList){
                        searchFilesMapping.remove(file);
                        System.out.println("searchFilesMapping remove fileKey:"+ file);
                    }
                }
            }

        }catch (Exception e){
            System.out.println("save files to indexFilesStore error,error:"+e.getMessage());
            return IndexResponse.failedResp("save files to indexFilesStore error,error:"+e.getMessage());
        }finally {
            wLock.unlock();
        }
        IndexResponse.ResultData resultData = new IndexResponse.ResultData();
        resultData.setPeerId(peerId);
        return IndexResponse.sucResp(resultData);
    }
    private IndexResponse lookup(String fileName) {
        System.out.println("lookup file "+fileName);

        rLock.lock();
        try{
            String peerKey = this.searchFilesMapping.get(fileName);
            if(peerKey==null){
                System.out.println("No file found, name:"+fileName);
                return IndexResponse.failedResp("No file found, name:"+fileName);
            }

            FilesStoreEntity filesStoreEntity = this.indexFilesStore.get(peerKey);
            if(filesStoreEntity==null){
                System.out.println("No files found, name:"+fileName+" ,peerKey:"+peerKey);
                return IndexResponse.failedResp("No file found, name:"+fileName+" ,peerKey:"+peerKey);
            }

            IndexResponse.ResultData resultData = new IndexResponse.ResultData();
            resultData.setPeerId(filesStoreEntity.getPeerId());
            resultData.setFileServerIp(filesStoreEntity.getClientIp());
            return IndexResponse.sucResp(resultData);
        }catch (Exception e){
            System.out.println("call lookup error,error:"+e.getMessage());
            return IndexResponse.failedResp("call lookup error,error:"+e.getMessage());
        }finally {
            rLock.unlock();
        }
    }
}
