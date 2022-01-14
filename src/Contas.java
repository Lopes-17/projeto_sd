import java.util.HashMap;

public class Contas {
    private HashMap<String,User> contas;



    public Contas(){
        contas = new HashMap<>();
    }


    public boolean existe (String username){
        return contas.containsKey(username);
    }

    public boolean cancelaViagem(String username, int idViagem){
        if (!contas.containsKey(username)) return false;
        User user = contas.get(username);
        user.cancelaViagem(idViagem);
        return true;
    }

    public void adicionarUser(String username,String password)throws Exception{
        if (existe(username)) throw new Exception("Utilizador com nome  \"" + username + "\" já existe");//TODO: Criar Exception
        User user = new User(username,password);
        this.contas.put(username, user);
    }

    public boolean autenticarUser(String username, String password) {
        if (!contas.containsKey(username)) return false;
        User user = contas.get(username);
        String pass = contas.get(username).getPass();
        return pass.equals(password);
    }

    public boolean isAdmin (String username){
        User user = contas.get(username);
        return user instanceof Admin;
    }

    public void addAdmin (String username,String password) throws Exception {
        Admin admin = new Admin(username, password);
        if (existe(username)) throw new Exception("Já existe");
        this.contas.put(username,admin);
    }
}
