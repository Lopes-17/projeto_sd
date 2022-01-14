import java.util.*;

public class Local {
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


    public void allPercursos(Local local){
        if(destinosPossiveis.containsKey(local)){
            System.out.println(nome + " ->" + local.nome);
        }
        for (Local l : destinosPossiveis.keySet()){
            if (!l.equals(local)) {
                if (l.destinosPossiveis.containsKey(local)) System.out.println(nome + " ->" + l.nome + "->" + local.nome);
                for (Local l2 : l.destinosPossiveis.keySet()) {
                    if (!l2.equals(local)) {
                        if (l2.destinosPossiveis.containsKey(local))
                            System.out.println(nome + " ->" + l.nome + "->" + l2.nome + "->" + local.nome);
                    }
                }
            }
        }
    }
}

