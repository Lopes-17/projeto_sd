import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class User {
    private String nome;
    private String pass;
    private Map<Integer,Viagem> viagens; // key -> idReserva

    public User(String nome, String pass){
        this.nome = nome;
        this.pass = pass;
        this.viagens = new HashMap<>();
    }

    public void cancelaViagem (int idReserva){
        Viagem v = viagens.get(idReserva);
        v.cancelar();
    }

    public List<Voo> getVoos(){
        List<Voo> allVoos = new ArrayList<Voo>();
        for (Viagem v : this.viagens.values()){
            allVoos.addAll(v.getVoos());
        }
        return allVoos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    /*public User clone() {
        return new Voo(this);
    }
    */


}
