package assignment06;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Banca implements Serializable {
    private String name;
    ArrayList<ContoCorrente> conti;
    static final Date start_date = new Date(2019, 01, 01);
    static final Date max_date = new Date(2020, 12, 31);
    static final int ASCII_OFFSET = 97;

    public Banca() {
        conti = new ArrayList<>();
        this.setName("StaBanca");
    }

    public void setName(String s) {
        this.name = s;
    }

    public String getName() { return this.name; }

    public ArrayList<ContoCorrente> getConti() { return this.conti; }

    public void fillWithStuff() {
        for(int i = 0; i < (ThreadLocalRandom.current().nextInt(10, 50)); i++) {
        // for(int i = 0; i < 3; i++) {
            ContoCorrente cc = (new ContoCorrente("Ian" + (char)(i + ASCII_OFFSET), "surname" + (char)(i + ASCII_OFFSET)));
            for(int j = 0; j < (ThreadLocalRandom.current().nextInt(2, 10)); j++) {
            // for(int j = 0; j < 1; j++) {
                cc.addMovimento(new Date(ThreadLocalRandom.current().
                        nextLong(start_date.getTime(), max_date.getTime())), causaleCasuale());
            }
            conti.add(cc);

        }
    }

    public static String causaleCasuale() {
        // "bonifico", "Accredito", "Bollettino", "F24", "PagoBancomat");
        String causale = null;
        switch(ThreadLocalRandom.current().nextInt(0, 5)) {
            case 0:
                causale = "bonifico";
                break;
            case 1:
                causale = "accredito";
                break;
            case 2:
                causale = "bollettino";
                break;
            case 3:
                causale = "f24";
                break;
            case 4:
                causale = "pagobancomat";
                break;
        }
        return causale;
    }

}
