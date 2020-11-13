package assignment06;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Movimento implements Serializable {
    Date date;
    String causale; // Bonifico, Accredito, Bollettino, F24, PagoBancomat

    static final Set<String> causali_accepted = Set.of(
            "bonifico", "accredito", "bollettino", "f24", "pagobancomat");


    public Movimento(Date d, String c) {
        this.setCausale(c);
        this.setDate(d);
    }

    public Movimento() {}

    public void setDate(Date d) {
        this.date = d;
    }

    public void setCausale(String s) {
        if(this.causali_accepted.contains(s)) {
            this.causale = s;
        } else {
            System.err.println("ERRORE: causale non accettata");
            System.exit(-1);
        }
    }

    public String getCausale() { return this.causale; }

    public Long getDate() { return this.date.getTime(); }
}
