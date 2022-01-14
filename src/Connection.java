import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable{
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final ReentrantLock readLock = new ReentrantLock();
    private final ReentrantLock writeLock = new ReentrantLock();


    public static class FrameCliente {
        public int tag;
        public byte[] data;

        public FrameCliente(int tag,byte[] data) {
            this.tag = tag;
            this.data = data;
        }
    }

    public static class FrameServidor{
        public int tag;
        public String username;
        public Object data;

        public FrameServidor(int tag, String username,Object data) {
            this.tag = tag;
            this.username =username;
            this.data = data;
        }
    }

    public Connection(Socket socket) throws IOException {
        this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }



    public void send (int tag, byte[]data) throws IOException {
        send(new FrameCliente(tag, data));
    }

    public void send (int tag,String username,byte[]data) throws IOException{
        try {
            writeLock.lock();
            this.dataOutputStream.writeInt(tag);
            this.dataOutputStream.writeUTF(username);
            this.dataOutputStream.writeInt(data.length);
            this.dataOutputStream.write(data);
            this.dataOutputStream.flush();
        }
        finally {
            writeLock.unlock();
        }
    }

    public void send (FrameCliente frame) throws IOException {
        try {
            writeLock.lock();
            this.dataOutputStream.writeInt(frame.tag);
            this.dataOutputStream.writeInt(frame.data.length);
            this.dataOutputStream.write(frame.data);
            this.dataOutputStream.flush();
        }
        finally {
            writeLock.unlock();
        }
    }

    public void sendViagem(int tag, String username, List<String> viagens)throws IOException {
        try {
            writeLock.lock();
            this.dataOutputStream.writeInt(tag);
            this.dataOutputStream.writeUTF(username);
            this.dataOutputStream.writeInt(viagens.size());
            for(String viagem : viagens)
                this.dataOutputStream.writeUTF(viagem);
            LocalDate date = LocalDate.now();
            this.dataOutputStream.writeUTF(date.toString());
            this.dataOutputStream.flush();

        }
        finally {
            writeLock.unlock();
        }
    }

    public void sendString(int tag, String username, String data) throws IOException {
        try {
            writeLock.lock();
            this.dataOutputStream.writeInt(tag);
            this.dataOutputStream.writeUTF(username);
            this.dataOutputStream.writeUTF(data);
            this.dataOutputStream.flush();
        }
        finally {
            writeLock.unlock();
        }
    }

    public void sendVoo(int tag, String username, String origem, String destino, int capacidade)throws IOException {
        try {
            writeLock.lock();
            this.dataOutputStream.writeInt(tag);
            this.dataOutputStream.writeUTF(username);
            this.dataOutputStream.writeUTF(origem);
            this.dataOutputStream.writeUTF(destino);
            this.dataOutputStream.writeInt(capacidade);
            this.dataOutputStream.flush();
        }
        finally {
            writeLock.unlock();
        }
    }


    public FrameCliente receiveCliente() throws IOException {
        FrameCliente f;
        readLock.lock();
        try {
            int tag;
            tag = this.dataInputStream.readInt();
            byte[] data = new byte[this.dataInputStream.readInt()];

            this.dataInputStream.readFully(data);

            f = new FrameCliente(tag, data);
            return f;

        } finally {
            readLock.unlock();
        }
    }


    public FrameServidor receive() throws IOException {
        int tag;
        String username;
        try {
            readLock.lock();
            tag = this.dataInputStream.readInt();
            username = this.dataInputStream.readUTF();
            switch (tag) {
                case 0:
                case 1:
                case 3: {
                    String string = this.dataInputStream.readUTF();
                    return new FrameServidor(tag, username, string);
                }
                case 2: {
                    int len = this.dataInputStream.readInt();
                    List<String> listViagem = new ArrayList<>();
                    for (int i = 0; i < len; i++) {
                        listViagem.add(dataInputStream.readUTF());
                    }
                    LocalDate localDate = LocalDate.parse(dataInputStream.readUTF());
                    System.out.println("DATE IS ->" + localDate);
                    return new FrameServidor(tag, username, listViagem);
                }
                case 4:
                    return new FrameServidor(tag,username,new byte[0]);
                case 5:
                    String origem = this.dataInputStream.readUTF();
                    String destino = this.dataInputStream.readUTF();
                    int capacidade = this.dataInputStream.readInt();
                    Voo voo = new Voo(origem,destino,capacidade);
                    return new FrameServidor(tag,username,voo);
            }
        }

        finally {
            readLock.unlock();
        }
        return null;
    }


    public void close() throws IOException {
        this.dataInputStream.close();
        this.dataOutputStream.close();
    }
}
