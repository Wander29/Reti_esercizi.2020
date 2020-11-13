import java.util.ArrayList;

/**
 * Laboratorio modella un laboratorio in cui sono presenti ncomputers pc
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Laboratorio {
    /**
     * numero di computer presenti nel laboratorio
     */
    private final int ncomputers;
    /**
     * rappresenta l'insieme dei computer.
     * Ogni item dell'array è un booleano e può quindi assumere due valori {true, false}.
     *  true: un computer è disponibile
     *  false: un computer è occupato
     */
    private final ArrayList<Boolean> computers;

    /**
     *
     * @param ncomputers numero computer presenti nel laboratorio
     */
    public Laboratorio(int ncomputers){
        this.ncomputers = ncomputers;
        computers = new ArrayList<>(ncomputers);
        // inizializza tutti i computer allo stato disponibile
        for(int i=0; i<ncomputers; i++){
            computers.add(Boolean.TRUE);
        }
    }

    /**
     *
     * @param idx id di un pc
     * @return true se è disponibile, false altrimenti
     */
    public boolean isAvailable(int idx){
        return computers.get(idx);
    }

    /**
     * setta allo stato disponibile tutti i pc
     */
    public void releaseAll(){
        for(int i=0; i<ncomputers; i++){
            computers.set(i, Boolean.TRUE);
        }
    }

    /**
     * setta allo stato occupato tutti i pc
     */
    public void occupyAll(){
        for(int i=0; i<ncomputers; i++){
            computers.set(i, Boolean.FALSE);
        }
    }

    /**
     * rende disponibile il pc idx
     * @param idx id di un pc
     */
    public void releaseComputer(int idx){
        computers.set(idx, Boolean.TRUE);
    }

    /**
     * rende occupato il pc idx
     * @param idx id di un pc
     */
    public void occupyComputer(int idx){
        computers.set(idx, Boolean.FALSE);
    }

    /**
     *
     * @return true se tutti i computer sono disponibili, false altrimenti
     */
    public boolean isFree(){
        for (Boolean computer: computers) {
            if (!computer)  // il computer è occupato
                return false;
        }
        return true;
    }

    /**
     *
     * @return Laboratorio#ncomputers
     */
    public int getNcomputers() {
        return ncomputers;
    }

    /**
     *
     * @return  l'id del primo computer disponibile (se esiste)
     *          -1 se nessun computer è disponibile
     */
    public int getFirstAvailableComputer() {
        for(int i=0; i<this.ncomputers; i++) {
            if(computers.get(i)) // controlla lo stato del pc e verifica se è libero
                return i;
        }
        return -1;
    }
}
