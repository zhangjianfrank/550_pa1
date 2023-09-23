import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileServer {
    public static final int FILE_SERVER_DEFAULT_PORT=10000;

    /**
     * The buffer size of the file stream.
     */
    private static final int INPUT_BUFFER_SIZE = 4096;

    public void startFileServer(int port) throws IOException {

        DatagramSocket commandSocket = new DatagramSocket(port);
        ExecutorService workerThreadPool = Executors.newFixedThreadPool(10);
        System.out.println("\nThe file server is started successfully. port: "+port);
        while (true) {
            try {
                byte[] inputDataBuffer = new byte[INPUT_BUFFER_SIZE];
                DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
                commandSocket.receive(inputPacket);
                // The client connection is handled by the worker thread pool
                workerThreadPool.submit(() -> accept(commandSocket,inputPacket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void accept(DatagramSocket commandSocket,DatagramPacket inputPacket)  {

        String command = new String(inputPacket.getData());
        String ipAddress = inputPacket.getAddress().toString().substring(1);
        int port = inputPacket.getPort();
        byte[] outputDataBuffer;
        try {

            if ( command.startsWith("GET ") ) {
                String[] commands = command.split(" ");
                int sendStreamPort = Integer.parseInt(commands[1]);
                String fileName = commands[2];
                //Check whether the file exists
                if (FileUtils.isFileExists(fileName)) {
                    outputDataBuffer = "ACCEPT".getBytes();
                    sendDatagram(commandSocket, outputDataBuffer, ipAddress, port);
                    TimeUnit.MILLISECONDS.sleep(200);
                    sendFileStream(ipAddress,sendStreamPort,fileName);
                } else {
                    outputDataBuffer = "ERROR".getBytes();
                    sendDatagram(commandSocket, outputDataBuffer, ipAddress, port);
                }
            }

        } catch ( Exception ex ) {
            System.err.println("accept error,message: "+ex.getMessage());
        } finally {

        }

    }

    private void sendDatagram(DatagramSocket clientSocket, byte[] outputBuffer, String ipAddress, int port)
            throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        DatagramPacket outputPacket = new DatagramPacket(outputBuffer, outputBuffer.length, inetAddress, port);
        clientSocket.send(outputPacket);
    }


//
//    private void sendFileStream( String ipAddress,int port,String fileName) {
//
//        Socket clientFileSocket;
//        DataInputStream fileInputStream = null;
//        DataOutputStream fileOutputStream = null;
//
//        try {
//            clientFileSocket = new Socket(ipAddress, port);
//            fileInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
//            fileOutputStream = new DataOutputStream(clientFileSocket.getOutputStream());
//
//            byte[] fileBuffer = new byte[INPUT_BUFFER_SIZE];
//            while (true) {
//                int bytesRead = fileInputStream.read(fileBuffer);
//                if ( bytesRead == -1 ) {
//                    break;
//                }
//                fileOutputStream.write(fileBuffer, 0, bytesRead);
//            }
//            fileOutputStream.flush();
//        } catch ( IOException ex ) {
////            LOGGER.catching(ex);
//        } finally {
//            // Close Socket and DataStream
//            try {
//                if ( fileInputStream != null ) {
//                    fileInputStream.close();
//                }
//                if ( fileOutputStream != null ) {
//                    fileOutputStream.close();
//                }
//                if ( socket != null ) {
//                    socket.close();
//                }
//            } catch ( IOException ex ) {
////                LOGGER.catching(ex);
//            }
//        }
//    }



    private void sendFileStream(String ipAddress, int port, String fileName) {
        Socket clientFileSocket = null;
        DataInputStream fileInputStream = null;
        DataOutputStream fileOutputStream = null;

        try {
            clientFileSocket = new Socket(ipAddress, port);
            fileInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            fileOutputStream = new DataOutputStream(clientFileSocket.getOutputStream());

            byte[] fileBuffer = new byte[INPUT_BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(fileBuffer)) != -1) {
                fileOutputStream.write(fileBuffer, 0, bytesRead);
            }
            fileOutputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Close Socket and DataStream
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (clientFileSocket != null) {
                    clientFileSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
