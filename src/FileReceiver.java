import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.Arrays;

public class FileReceiver {

    private static final int FILE_STREAM_PORT = 10001;

    private static final int FILE_BUFFER_SIZE = 4096;

    public String receiveFile(String fileFullName,String fileName, String ipAddress, int sockPort) throws IOException {
        DatagramSocket commandSocket = null;
        ServerSocket fileStreamListener = null;
        Socket fileStreamSocket = null;
        DataInputStream fileInputStream = null;
        DataOutputStream fileOutputStream = null;

        try {
            // Send command for requesting files
            commandSocket = new DatagramSocket();
            byte[] outputDataBuffer = ("GET " + FILE_STREAM_PORT + " " + fileFullName + " ").getBytes();
            DatagramPacket outputPacket = new DatagramPacket(outputDataBuffer,
                    outputDataBuffer.length, InetAddress.getByName(ipAddress), sockPort);
            commandSocket.send(outputPacket);

            byte[] inputDataBuffer = new byte[FILE_BUFFER_SIZE];
            DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
            commandSocket.receive(inputPacket);
            String command = new String(Arrays.copyOf(inputPacket.getData(), inputPacket.getLength()));

            if (!command.startsWith("ACCEPT")) {
                throw new IOException("Failed to obtain the " + fileName + " file from the server (" + ipAddress + ").");
            }

            Path downloadPath = FileUtils.getUserDownloadPath();
            Path filePath = downloadPath.resolve(fileName);

            // Opening port for receiving file stream
            fileStreamListener = new ServerSocket(FILE_STREAM_PORT);
            fileStreamSocket = fileStreamListener.accept();

            File downloadFile = filePath.toFile();

            // Receiving Data Stream
            fileInputStream = new DataInputStream(new BufferedInputStream(fileStreamSocket.getInputStream()));
            fileOutputStream = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(downloadFile))));
            byte[] fileBuffer = new byte[FILE_BUFFER_SIZE];
            int bytesRead;

            while (true) {
                bytesRead = fileInputStream.read(fileBuffer);
                if (bytesRead == -1) {
                    break;
                }
                fileOutputStream.write(fileBuffer, 0, bytesRead);
            }
            fileOutputStream.flush();
            return downloadFile.getPath();
        } finally {
            try {
                if (commandSocket != null) {
                    commandSocket.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileStreamSocket != null) {
                    fileStreamSocket.close();
                }
                if (fileStreamListener != null) {
                    fileStreamListener.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
