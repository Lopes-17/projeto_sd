import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Reservas {
    private Map<String,Local> locais;
    private List<Voo> voosDiarios;
    private Map<LocalDate,List<Voo>> todosVoos;
    private Map<LocalDate,List<Viagem>> todasViagens;

    //TODO Adicionar Locks (?)


    public Reservas(){
        this.locais = new HashMap<>();
        this.voosDiarios = new ArrayList<Voo>();
        this.todosVoos = new HashMap<LocalDate, List<Voo>>();
        this.todasViagens = new HashMap<LocalDate, List<Viagem>>();
    }

    public List<Voo> getVoosDiarios(){
        return new ArrayList<>(this.voosDiarios);
    }


    public void adicionarVoo(String origem,String destino, int capacidade){
        locais.putIfAbsent(origem,new Local(origem));
        locais.putIfAbsent(destino,new Local(destino));

        Voo voo = new Voo(origem,destino,capacidade);
        locais.get(origem).adicionarDestino(locais.get(destino),voo);

        adicionarVoo(voo);
    }

    public void adicionarVoo (Voo voo){
        String origem = voo.getOrigem();
        String destino = voo.getDestino();
        
        locais.putIfAbsent(origem,new Local(origem));
        locais.putIfAbsent(destino,new Local(destino));

        locais.get(origem).adicionarDestino(locais.get(destino),voo);
        voosDiarios.add(voo);
    }

    public boolean getAllCaminhos(String origem, String destino){
        Local localOrigem = locais.get(origem);
        Local localDestino = locais.get(destino);
        localOrigem.allPercursos(localDestino);
        return true;
    }

    public Viagem marcarViagem(List<String>destinos,LocalDate inicio, LocalDate fim) {
        String origem = destinos.get(0);
        List<Voo> listVoo = new ArrayList<>();
        for (LocalDate date = inicio; date.isBefore(fim) || date.isEqual(fim); date = date.plusDays(1)) {
            for (int i = 1; i < destinos.size();i++) {
                todosVoos.putIfAbsent(date,new ArrayList<>(voosDiarios));
                List<Voo> voosDia = todosVoos.get(date);
                String destino = destinos.get(i);
                for (Voo voo : voosDia) {
                    if (voo.getOrigem().equals(origem) && voo.getDestino().equals(destino)){
                        listVoo.add(voo);
                        break;
                    }
                }
            }
            if (listVoo.size() == destinos.size() -1) {
                int numReserva = todasViagens.size();
                Viagem viagem = new Viagem(numReserva,listVoo,date);
                todasViagens.putIfAbsent(date,new ArrayList<>());
                List<Viagem> list = todasViagens.get(date);
                list.add(viagem);
                //Criar Viagem
                return viagem;
            }
        }
        return null;
    }



    public void cancelarDia(LocalDate dia){
        List<Viagem> listaViagens = todasViagens.get(dia);
        for (Viagem viagem : listaViagens){
            viagem.cancelar();
        }
        List<Voo> listaVoo = todosVoos.get(dia);
        for (Voo voo : listaVoo){
            voo.cancela();
        }
    }

    public void getViagem(String origem,String destino){
    /*
        List<String> destinosPossiveis = voosDiarios.stream().
                filter(voo -> voo.getOrigem().equals(origem)).
                map(Voo::getDestino).
                collect(Collectors.toList());


     */
    }




    
}
