import com.sun.org.glassfish.external.statistics.annotations.Reset;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jdk.nashorn.internal.runtime.ECMAException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.RuleBasedCollator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            Reservas reservas = new Reservas();
            reservas.adicionarVoo("Porto","Lisboa",500);
            reservas.adicionarVoo("Porto","Paris",500);
            reservas.adicionarVoo("Lisboa","Paris",500);
            reservas.adicionarVoo("Paris","Madrid",500);
            reservas.adicionarVoo("Porto","Madrid",500);
            reservas.adicionarVoo("Lisboa","Londres",500);
            reservas.adicionarVoo("Londres","Paris",500);
            reservas.adicionarVoo("Paris","Londres",500);
            reservas.adicionarVoo("Londres","Porto",500);
            Contas contas = new Contas();
            contas.addAdmin("diogo", "123");
            //if (reservas.getAllCaminhos("Porto","Paris")) return;
            while (true) {
                Socket socket = ss.accept();
                Connection connection = new Connection(socket);
                Worker worker = new Worker(connection,contas,reservas);
                new Thread(worker).start();

                /*
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String line;
                while ((line = in.readLine()) != null) {
                    out.println(line);
                    out.flush();
                }

                 */

                //socket.shutdownOutput();
                //socket.shutdownInput();
                //socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Worker implements Runnable {
        private final Connection connection;
        private final Contas contas;
        private final Reservas reservas;

        public Worker(Connection connection,Contas contas, Reservas reservas){
            this.connection = connection;
            this.contas = contas;
            this.reservas = reservas;
        }


        public void run() {
            boolean out = false;
            while (!out) {
                try {
                    Connection.FrameServidor frame = connection.receive();
                    if (frame.tag == 0) {
                        String username = frame.username;
                        String password = (String) frame.data;
                        System.out.println(username);
                        System.out.println(password);
                        boolean loginValido = contas.autenticarUser(username, password);
                        if (loginValido) {
                            String message = contas.isAdmin(username) ? "OK_ADMIN" : "OK_USER";
                            connection.send(0, message.getBytes());
                            break;
                        }
                        else connection.send(0,  "ERRO".getBytes());
                    }

                    if (frame.tag == 1) {
                        String username = frame.username;
                        String password = (String) frame.data;
                        try {
                            contas.adicionarUser(username, password);
                            connection.send(1,"OK".getBytes());
                        }
                        catch (Exception e){
                            connection.send(1,"Já existe um utilizador com esse username".getBytes());
                            System.out.println("ERRO");
                        }
                    }

                    if (frame.tag == 2){
                        @SuppressWarnings("unchecked") List<String> listDestinos = (List<String>) frame.data;
                        System.out.println(listDestinos);
                        Viagem viagem = reservas.marcarViagem(listDestinos, LocalDate.now(),LocalDate.now());
                        connection.send(2,String.valueOf(viagem.getIdReserva()).getBytes());
                        System.out.println(viagem);

                    }

                    if (frame.tag == 3){
                        int id = (Integer) frame.data;
                        String username = frame.username;
                        try {
                            contas.cancelaViagem(username, id);
                            connection.send(3,  "OK".getBytes());
                        }
                        catch (Exception e){
                            connection.send(3,  "Ids inválidos".getBytes());
                            System.out.println("ERRO");
                        }
                    }
                    if (frame.tag == 4){
                        List<Voo> voosDiarios = reservas.getVoosDiarios();
                        if(voosDiarios.size()==0) connection.send(4,  "Lista Vazia".getBytes());
                        else {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeInt(voosDiarios.size());
                            for (Voo voo : voosDiarios){
                                String origem = voo.getOrigem();
                                String destino = voo.getDestino();
                                oos.writeUTF(origem);
                                oos.writeUTF(destino);
                            }
                            oos.flush();
                            byte[] bytes= baos.toByteArray();
                            connection.send(4, bytes);
                        }
                    }

                    if (frame.tag == 5){
                        Voo voo = (Voo)frame.data;
                        reservas.adicionarVoo(voo);
                        connection.send(5, "OK".getBytes());
                    }
                    if (frame.tag == 10){
                        connection.close();
                        out = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
