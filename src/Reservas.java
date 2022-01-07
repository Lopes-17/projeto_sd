import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Reservas {
    private List<Local> locais;
    private List<Voo> voosDiarios;
    private Map<LocalDate,List<Voo>> todosVoos;
    private Map<Integer,Viagem> reservas;






    public void adicionarVoo(String origem,String destino, int capacidade){
        Voo voo = new Voo(origem,destino,capacidade);
        adicionarVoo(voo);
    }

    public void adicionarVoo (Voo voo){
        voosDiarios.add(voo);
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
