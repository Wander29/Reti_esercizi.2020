package assignment06;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ContoCorrente implements Serializable {

    private String nome;
    private String cognome;

    private ArrayList<Movimento> movimenti;

    public ContoCorrente(String n, String c) {
        this.cognome = c;
        this.nome = n;
        movimenti = new ArrayList<>();
    }
    public ContoCorrente() {
        movimenti = new ArrayList<>();
    }

    public void setNome(String n){
        this.nome = n;
    }

    public void setCognome(String c){
        this.cognome = c;
    }

    public void addMovimento(Date d, String s) {
        movimenti.add(new Movimento(d, s));
    }

    public String getNome() { return this.nome; }

    public String getCognome() { return this.cognome; }

    public ArrayList<Movimento> getMovimenti() {
        return movimenti;
    }
}
