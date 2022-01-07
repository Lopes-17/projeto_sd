import java.util.HashMap;

public class Contas {
    HashMap<String,String> contas;



    public Contas(){
        contas = new HashMap<>();
    }


    public boolean existe (String username){
        return contas.containsKey(username);
    }


    public void adicionarUser(String username,String password)throws Exception{
        if (existe(username)) throw new Exception("Utilizador com nome  \"" + username + "\" já existe");//TODO: Criar Exception
        this.contas.put(username, password);
    }
}
