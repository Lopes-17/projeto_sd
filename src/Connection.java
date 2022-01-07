import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable{
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final ReentrantLock readLock = new ReentrantLock();
    private final ReentrantLock writeLock = new ReentrantLock();

    public static class Frame {
        public int tag;
        public String username;
        public byte[] data;

        public Frame(int tag, String username, byte[] data) {
            this.tag = tag;
            this.username = username;
            this.data = data;
        }

    }

    public Connection(Socket socket) throws IOException {
        this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }



    public void send (int tag,String username, byte[]data) throws IOException {
        send(new Frame(tag, username, data));
    }

    public void send (Frame frame) throws IOException {
        try {
            writeLock.lock();
            this.dataOutputStream.writeInt(frame.tag);
            this.dataOutputStream.writeUTF(frame.username);
            this.dataOutputStream.writeInt(frame.data.length);
            this.dataOutputStream.write(frame.data);
            this.dataOutputStream.flush();
        }
        finally {
            writeLock.lock();
        }
    }

    public Frame receive() throws IOException {
        int tag;
        String username;
        byte[]data;
        try {
            readLock.lock();
            tag = this.dataInputStream.readInt();
            username = this.dataInputStream.readUTF();
            int length = this.dataInputStream.readInt();
            data = new byte[length];
            this.dataInputStream.readFully(data);
        }
        finally {
            readLock.unlock();
        }
        return new Frame(tag, username, data);
    }


    public void close() throws Exception {
        this.dataInputStream.close();
        this.dataOutputStream.close();
    }
}
