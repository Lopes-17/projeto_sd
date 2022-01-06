public class Viagem {
    private String origem;
    private String destino;
    private int capacidade;

    public Viagem(){
        this.origem = "";
        this.destino = "";
        this.capacidade = 0;
    }

    public Viagem(String origem, String destino, int capacidade){
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
