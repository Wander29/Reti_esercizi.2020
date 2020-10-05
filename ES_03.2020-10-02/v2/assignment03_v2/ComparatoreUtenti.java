package assignment03_v2;

import java.util.Comparator;

public class ComparatoreUtenti implements Comparator<Utente> {

    @Override
    public int compare(Utente ut1, Utente ut2) {
        return ut1.getPriority() - ut2.getPriority();
    }
}
