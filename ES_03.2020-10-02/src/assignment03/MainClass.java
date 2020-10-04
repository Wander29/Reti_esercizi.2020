package assignment03;


/*
Il laboratorio di Informatica del Polo Marzotto è utilizzato da tre tipi di utenti
studenti, tesisti e professori ed ogni utente deve fare una richiesta al tutor per accedere al laboratorio.
I computers del laboratorio sono numerati da 1 a 20. Le richieste di accesso sono diverse a seconda del
tipo dell'utente:

    - [professori] accedono in modo esclusivo a tutto il laboratorio, poichè hanno necessità di utilizzare
    tutti i computers per effettuare prove in rete.
    - [tesisti] richiedono l'uso esclusivo di un solo computer, identificato dall'indice i, poichè su quel
    computer è istallato un particolare software necessario per lo sviluppo della tesi.
    - [studenti] richiedono l'uso esclusivo di un qualsiasi computer.

I professori hanno priorità su tutti nell'accesso al laboratorio, i tesisti hanno priorità sugli studenti.
Nessuno può essere interrotto mentre sta usando un computer.

Scrivere un programma JAVA che simuli il comportamento degli utenti e del tutor.
Il programma riceve in ingresso il numero di studenti, tesisti e professori che utilizzano il
laboratorio ed attiva un thread per ogni utente. Ogni utente accede k volte al laboratorio,
con k generato casualmente. Simulare l'intervallo di tempo che intercorre tra un accesso ed il
successivo e l'intervallo di permanenza in laboratorio mediante il metodo sleep.

Il tutor deve coordinare gli accessi al laboratorio.
Il programma deve terminare quando tutti gli utenti hanno completato i loro accessi al laboratorio.

*/


    // attento a setPriority
    // un solo professore per volta, non più prof insieme

    // priorityQueue -> può essere un modo, daie, però


public class MainClass {

	private static final int NUM_PC = 20;
	
	public static void main(String[] args) {
		Laboratorio lab = new Laboratorio(NUM_PC);
		
	}

}








