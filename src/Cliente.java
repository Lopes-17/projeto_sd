import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cliente {


    private static void menuCliente (Demultiplexer demultiplexer, String username){
        boolean out = false;
        while (!out){
            System.out.println("MENU CLIENTE");
            System.out.println("1.Reservar viagem");
            System.out.println("2.Cancelar viagem");
            System.out.println("3.Voos Existentes");
            System.out.println("4.Terminar Sessão");
            System.out.println("Escolha uma opção");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String opcao = null;
            try {
                opcao = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                switch (opcao) {
                    case "1": {
                        //Reservar viagem
                        System.out.println("Origem");
                        String origem = in.readLine();
                        List<String> listVoos = new ArrayList<>();
                        listVoos.add(origem);
                        while (true){
                            System.out.println("Próximo Destino");
                            String destino = in.readLine();
                            listVoos.add(destino);
                            System.out.println("Quer adicionar mais locais?");
                            System.out.println("0.Não");
                            System.out.println("1.Sim");
                            String ans = in.readLine();
                            if (ans.equals("0"))break;
                        }
                        demultiplexer.sendViagem(2,username,listVoos);
                        String idReserva = new String(demultiplexer.receive(2));
                        System.out.println("ID da Reserva ->" + idReserva);

                        break;
                    }
                    case "2": {
                        //Cancelar Viagem
                        System.out.println("Id Viagem:");
                        String id = in.readLine();
                        demultiplexer.send(3, username, id.getBytes());
                        break;
                    }
                    case "3": {
                        //Voos Existentes
                        demultiplexer.send(4, username, new byte[0]);
                        byte[]array = demultiplexer.receive(4);
                        ByteArrayInputStream bis = new ByteArrayInputStream(array);
                        ObjectInput input = new ObjectInputStream(bis);
                        int len = input.readInt();
                        System.out.println("VOOS EXISTENTES");
                        for (int i = 0; i < len;i++){
                            String origem = input.readUTF();
                            String destino = input.readUTF();
                            System.out.println(origem +"->" + destino);
                        }
                        break;
                    }
                    case "4": {
                        //Terminar Sessão
                        demultiplexer.send(10,username, new byte[0]);
                        out = true;
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //0 - Iniciar Sessao
    //1 - Criar Conta
    //2 - Reservar Viagem
    //3 - Cancelar Viagem
    //4 - Voos Existentes
    //5 - Inserir Voo
    //6 - Encerrar Dia
    //10- Terminar Sessão



    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            Demultiplexer demultiplexer = new Demultiplexer(new Connection(socket));
            demultiplexer.start();
            String opcao;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("LOGIN");
            System.out.println("1.Inicar Sessão");
            System.out.println("2.Criar Conta");

            opcao = in.readLine();
            boolean loggedIn = false;
            boolean isAdmin = false;
            String username = null;

            while (true) {
                if (!loggedIn) {
                    switch (opcao) {
                        case "1": {
                            System.out.println("INICIAR SESSÃO");
                            System.out.println("Username");
                            username = in.readLine();
                            System.out.println("Password");
                            String pass = in.readLine();
                            demultiplexer.sendString(0,username,pass);
                            String resposta = new String(demultiplexer.receive(0));
                            if (resposta.startsWith("OK")) {
                                loggedIn = true;
                                if (resposta.equals("OK_ADMIN")) isAdmin = true;
                                System.out.println("LOGIN VÁLIDO");
                            } else System.out.println("LOGIN INVÁLIDO");
                            break;
                        }
                        case "2": {
                            System.out.println("CRIAR CONTA");
                            System.out.println("Introduza o username: ");
                            username = in.readLine();
                            System.out.println("Introduza a password");
                            String pass = in.readLine();
                            demultiplexer.sendString(1,username,pass);
                            String resposta = new String(demultiplexer.receive(1));
                            if (resposta.equals("OK")){
                                loggedIn = true;
                                System.out.println("Conta criada com sucesso");
                            }
                            else System.out.println(resposta);
                        }
                    }
                }
                else {
                    if (isAdmin) {
                        System.out.println("Menu ADMIN");
                        System.out.println("1.Inserir voo");
                        System.out.println("2.Encerrar dia");
                        System.out.println("3.Terminar sessão");
                        System.out.println("Escolha uma opção");
                        opcao = in.readLine();
                        switch (opcao){
                            case "1":{
                                System.out.println("INSERIR VOO");
                                System.out.print("Origem:");
                                String origem = in.readLine();
                                System.out.print("Destino:");
                                String destino = in.readLine();
                                System.out.println("Capacidade");
                                int capacidade = Integer.parseInt(in.readLine());
                                demultiplexer.sendVoo(5,username,origem,destino,capacidade);
                                break;
                            }
                            case "2":{
                                //Encerrar dia
                                System.out.println("ENCERRAR DIA");
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                                String dateStr = in.readLine();
                                LocalDate date = LocalDate.parse(dateStr, formatter);
                                break;
                            }
                            case "3":{
                                //Terminar sessao
                                break;
                            }
                        }

                    }
                    else menuCliente(demultiplexer,username);
                    break;
                }
            }


            /*
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                out.flush();

                String response = in.readLine();
                System.out.println("Server response: " + response);
            }
             */
            //socket.shutdownOutput();
            //socket.shutdownInput();
            //socket.close();
            demultiplexer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
