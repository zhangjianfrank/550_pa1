import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Peer {
    private static final String DEFAULT_INDEX_SERVER_HOST = "127.0.0.1";
    private static final String DEFAULT_INDEX_SERVER_PORT = "8080";
    public static void main(String[] args) {
        String peerId = UUID.randomUUID().toString();
        System.out.println("peer initiates the registration of the server. peerId:"+peerId);

        Thread peerClientThread = new Thread(new PeerClient(peerId));
        peerClientThread.start();


    }



    public static class PeerClient implements Runnable{
        private final String peerId;
        public PeerClient(String peerId){
            this.peerId=peerId;
        }
        @Override
        public void run() {

            Socket peerClientSocket = null;
            BufferedReader input = null;


            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            IndexRequest indexRequest =null;
            IndexResponse indexServerResponse;

            try {
                input = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter Server IP Address (The default address is 127.0.0.1):");
                String serverAddress = input.readLine();
//                long startTime, endTime;
//                double time;
                if(serverAddress.trim().length() == 0 || "\n".equals(serverAddress)) {
                    serverAddress = DEFAULT_INDEX_SERVER_HOST;
                }

                if(!IPAddressValidator.isValidIP(serverAddress)) {
                    System.out.println("Invalid Server IP Address.");
                    System.exit(0);
                }


                System.out.print("Enter Server PORT (The default PORT is 8080):");
                String serverPort = input.readLine();
                if(serverPort.trim().length() == 0 || "\n".equals(serverPort)) {
                    serverPort = DEFAULT_INDEX_SERVER_PORT;
                }


                peerClientSocket = new Socket(serverAddress, Integer.parseInt(serverPort));

                out = new ObjectOutputStream(peerClientSocket.getOutputStream());
                out.flush();

                in = new ObjectInputStream(peerClientSocket.getInputStream());


                indexServerResponse = (IndexResponse) in.readObject();
                if(!indexServerResponse.isSuc()){
                    System.out.println("Connection failure.Address: "+serverAddress+",Port:"+serverPort);
                    System.exit(0);
                }

                System.out.println("The PEER has established a connection with server "+serverAddress+":"+serverPort+" .");
//                peerRequest = new Request();???
//                peerRequest.setRequestType("REPLICATION");
//                peerRequest.setRequestData(replicaChoice);
//                out.writeObject(peerRequest);
//
//                if (replicaChoice.equalsIgnoreCase("Y")) {
//                    // Read the Replication response from the server
//                    myIndexedLoc.add(REPLICATION_PATH);
//                    serverResponse = (Response) in.readObject();
//                    ConcurrentHashMap<String, ArrayList<String>> data = (ConcurrentHashMap<String, ArrayList<String>>) serverResponse.getResponseData();
//                    new ReplicationService(data).start();
//                }
//
//                // Previously indexed locations if any
//                serverResponse = (Response) in.readObject();
//                ArrayList<String> indexedLocations =  (ArrayList<String>) serverResponse.getResponseData();
//                if (indexedLocations != null) {
//                    for (String x : indexedLocations) {
//                        if (!myIndexedLoc.contains(x)) {
//                            myIndexedLoc.add(x);
//                        }
//                    }
//                }
//
                while (true) {
                    // Display different choices to the user
                    System.out.println("\nWhat do you want to do?");
                    System.out.println("1.Register files with indexing server.");
                    System.out.println("2.Lookup for a file at index server.");
                    System.out.println("3.Un-register all files of this peer from the indexing server.");
                    System.out.println("4.Print download log of this peer.");
                    System.out.println("5.Exit.");
                    System.out.print("Enter choice and press ENTER:");
                    int option;

                    try {
                        option = Integer.parseInt(input.readLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Wrong choice. Try again!!!");
                        continue;
                    }

                    switch (option) {

                        case 1:
                            System.out.println("\nPlease fill in the destination folder you want to sync:");
                            String directoryPath = input.readLine();

                            if(directoryPath.trim().length() == 0) {
                                System.out.println("Invalid path, please try again.");
                                continue;
                            }


                            List<String> files = FileUtils.listFilesInDirectory(directoryPath);

//                            File file = new File(directoryPath);
//                            if (file.isFile()) {
//                                myIndexedLoc.add(path.substring(0, path.lastIndexOf("/")));
//                                System.out.println(path.substring(0, path.lastIndexOf("/")));
//                                files.add(0, path.substring(0, path.lastIndexOf("/")));
//                            } else if (file.isDirectory()) {
//                                myIndexedLoc.add(path);
//                                files.add(0, path);
//                            }

                            // 1 because path is always there
                            if (files.size() > 1) {
//                                startTime = System.currentTimeMillis();

                                // Setup a Request object with Request Type = REGISTER and Request Data = files array list
                                indexRequest = new IndexRequest();
                                indexRequest.setRequestType(RequestTypeEnum.REGISTER.getCode());
                                indexRequest.setIndexRegister(new IndexRequest.IndexRegister());
                                indexRequest.getIndexRegister().setPeerId(this.peerId);
                                indexRequest.getIndexRegister().setFiles((ArrayList<String>) files);

                                out.writeObject(indexRequest);


                                indexServerResponse = (IndexResponse) in.readObject();
//                                endTime = System.currentTimeMillis();
//                                time = (double) Math.round(endTime - startTime) / 1000;

                                // If Response is success i.e. Response Code = 200, then print success message else error message
                                if (indexServerResponse.isSuc()) {
								/*indexedLocations =  (ArrayList<String>) serverResponse.getResponseData();
								for (String x : indexedLocations) {
									if (!myIndexedLoc.contains(x)) {
										myIndexedLoc.add(x);
									}
								}*/
                                    System.out.println((files.size() - 1) + " files registered with indexing server. ");
                                } else {
                                    System.out.println("Unable to register files with server. Please try again later.");
                                }
                            } else {
                                System.out.println("0 files found at this location. Nothing registered with indexing server.");
                            }
                            break;

                        // Handling file lookup on indexing server functionality
                        case 2:
//                            System.out.println("\nEnter name of the file you want to look for at indexing server:");
//                            String fileName = input.readLine();
//                            String hostAddress;
//
//                            startTime = System.currentTimeMillis();
//                            // Setup a Request object with Request Type = LOOKUP and Request Data = file to be searched
//                            peerRequest = new Request();
//                            peerRequest.setRequestType("LOOKUP");
//                            peerRequest.setRequestData(fileName);
//                            out.writeObject(peerRequest);
//
//                            serverResponse = (Response) in.readObject();
//                            endTime = System.currentTimeMillis();
//                            time = (double) Math.round(endTime - startTime) / 1000;
//
//                            // If Response is success i.e. Response Code = 200, then perform download operation else error message
//                            if (serverResponse.getResponseCode() == 200) {
//                                System.out.println("File Found. Lookup time: " + time + " seconds.");
//
//                                // Response Data contains the List of Peers which contain the searched file
//                                HashMap<Integer, String> lookupResults = (HashMap<Integer, String>) serverResponse.getResponseData();
//
//                                // Printing all Peer details that contain the searched file
//                                if (lookupResults != null) {
//                                    for (Map.Entry e : lookupResults.entrySet()) {
//                                        System.out.println("\nPeer ID:" + e.getKey().toString());
//                                        System.out.println("Host Address:" + e.getValue().toString());
//                                    }
//                                }
//
//                                // If the file is a Text file then we can print or else only download file
//                                if (fileName.trim().endsWith(".txt")) {
//                                    System.out.print("\nDo you want to download (D) or print this file (P)? Enter (D/P):");
//                                    String download = input.readLine();
//
//                                    // In case there are more than 1 peer, then we user will select which peer to use for download
//                                    if(lookupResults.size() > 1) {
//                                        System.out.print("Enter Peer ID from which you want to download the file:");
//                                        int peerId = Integer.parseInt(input.readLine());
//                                        hostAddress = lookupResults.get(peerId);
//                                    } else {
//                                        Map.Entry<Integer,String> entry = lookupResults.entrySet().iterator().next();
//                                        hostAddress = entry.getValue();
//                                    }
//
//                                    if (download.equalsIgnoreCase("D")) {
//                                        System.out.println("The file will be downloaded in the 'downloads' folder in the current location.");
//                                        // Obtain the searched file from the specified Peer
//                                        obtain(hostAddress, 20000, fileName, out, in);
//                                    } else if (download.equalsIgnoreCase("P")) {
//                                        // Obtain the searched file from the specified Peer and print its contents
//                                        obtain(hostAddress, 20000, fileName, out, in);
//                                        FileUtility.printFile(fileName);
//                                    }
//                                } else {
//                                    System.out.print("\nDo you want to download this file?(Y/N):");
//                                    String download = input.readLine();
//                                    if (download.equalsIgnoreCase("Y")) {
//                                        if(lookupResults.size() > 1) {
//                                            System.out.print("Enter Peer ID from which you want to download the file:");
//                                            int peerId = Integer.parseInt(input.readLine());
//                                            hostAddress = lookupResults.get(peerId);
//                                        } else {
//                                            Map.Entry<Integer,String> entry = lookupResults.entrySet().iterator().next();
//                                            hostAddress = entry.getValue();
//                                        }
//                                        // Obtain the searched file from the specified Peer
//                                        obtain(hostAddress, 20000, fileName, out, in);
//                                    }
//                                }
//                            } else {
//                                System.out.println((String) serverResponse.getResponseData());
//                                System.out.println("Lookup time: " + time + " seconds.");
//                            }
                            break;

                        // Handling de-registration of files from the indexing server
                        case 3:
//                            // Confirming user's un-register request
//                            System.out.print("\nAre you sure (Y/N)?:");
//                            String confirm = input.readLine();
//
//                            if (confirm.equalsIgnoreCase("Y")) {
//                                startTime = System.currentTimeMillis();
//                                // Setup a Request object with Request Type = UNREGISTER and Request Data = general message
//                                peerRequest = new Request();
//                                peerRequest.setRequestType("UNREGISTER");
//                                peerRequest.setRequestData("Un-register all files from index server.");
//                                out.writeObject(peerRequest);
//                                endTime = System.currentTimeMillis();
//                                time = (double) Math.round(endTime - startTime) / 1000;
//
//                                serverResponse = (Response) in.readObject();
//                                System.out.println((String) serverResponse.getResponseData());
//                                System.out.println("Time taken:" + time + " seconds.");
//                            }
                            break;

                        // Printing the download log
                        case 4:
//                            (new LogUtility("peer")).print();
                            break;

                        // Handling Peer exit functionality
                        case 5:
                            // Setup a Request object with Request Type = DISCONNECT and Request Data = general message
//                            peerRequest = new Request();
//                            peerRequest.setRequestType("DISCONNECT");
//                            peerRequest.setRequestData("Disconnecting from server.");
//                            out.writeObject(peerRequest);
//                            System.out.println("Thanks for using this system.");
//                            System.exit(0);
                            break;
                        default:
                            System.out.println("Incorrect selection, please try again!!!");
                            break;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    // Closing all streams. Close the stream only if it is initialized
                    if (out != null)
                        out.close();

                    if (in != null)
                        in.close();

                    if (peerClientSocket != null)
                        peerClientSocket.close();

                    if (input != null)
                        input.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
