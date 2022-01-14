import java.io.Serializable;
import java.util.*;

public class Local implements Serializable {
    private String nome;
    private Map<Local,Voo> destinosPossiveis;


    public Local(String nome){
        this.nome = nome;
        this.destinosPossiveis = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public void adicionarDestino(Local local,Voo voo){
        destinosPossiveis.put(local,voo);
    }

    public boolean containsDestino(Local local){
        return destinosPossiveis.containsKey(local);
    }


    public List<List<String>> allPercursos(Local local){
        List<List<String>> res = new ArrayList<>();
        if(destinosPossiveis.containsKey(local)){
            res.add(Arrays.asList(nome, local.nome));
        }
        for (Local l : destinosPossiveis.keySet()){
            if (!l.equals(local)) {
                if (l.destinosPossiveis.containsKey(local)) res.add(Arrays.asList(nome , l.nome ,local.nome));
                for (Local l2 : l.destinosPossiveis.keySet()) {
                    if (!l2.equals(local)) {
                        if (l2.destinosPossiveis.containsKey(local))
                            res.add(Arrays.asList(nome , l.nome , l2.nome ,local.nome));
                    }
                }
            }
        }
        return res;
    }
}

