import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Viagem {
    private int idReserva;
    private List<Voo> voos;
    private LocalDate dia;
    private Estado estado;

    public Viagem(int idReserva, List<Voo> voos, LocalDate dia){
        this.idReserva = idReserva;
        this.voos = voos.stream().collect(Collectors.toList());
        this.dia = dia;
        this.estado = Estado.MARCADA;
    }


    public enum Estado{
        MARCADA,
        CANCELADA,
        CONCLUIDA
    }


    public void addVoo (Voo voo){
        this.voos.add(voo);
    }


    public void cancelar(){
        this.estado = Estado.CANCELADA;
    }

    public List<Voo> getVoos(){
        return this.voos
                .stream()
                .collect(Collectors.toList());
    }


    public int getIdReserva(){
        return this.idReserva;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Viagem{" +
                "idReserva=" + idReserva +
                ", voos=" + voos +
                ", dia=" + dia +
                ", estado=" + estado +
                '}';
    }
}
