import java.util.*;

/**
 * Scrivere un programma che attiva un thread T che effettua il calcolo approssimato di π.
 * Il programma principale riceve in input da linea di comando un parametro che indica il grado di accuratezza (accuracy) per il calcolo di π ed il tempo massimo di attesa dopo cui il programma principale interrompe il thread T.
 * Il thread T effettua un ciclo infinito per il calcolo di π usando la serie di Gregory-Leibniz (π = 4/1 – 4/3 + 4/5 - 4/7 + 4/9 - 4/11 ...).
 * Il thread esce dal ciclo quando una delle due condizioni seguenti risulta verificata:
 * 1) il thread è stato interrotto
 * 2) la differenza tra il valore stimato di π ed il valore Math.PI (della libreria JAVA) è minore di accuracy
 */

/**
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class Main {
	public static void main(String[] args) throws InputMismatchException{
		long time; 		// time indica il tempo massimo impiegato per il calcolo espresso in ms
		double acc; 	//acc indica la soglia di approssimazione
		System.out.println("Inserisci prima tempo e poi accuratezza");

		// prende in input time e accuracy
		Scanner input = new Scanner(System.in);
		try {	
			time = input.nextLong();
			acc	= input.nextDouble();
		} catch (InputMismatchException e) { 	// fatal error
			throw e;
		}
		finally {
			input.close();
		}
		// creazione del thread che si occupa del calcolo
		CalcoloPiGreco t = new CalcoloPiGreco(acc);
		t.start();

		try{
			t.join(time);
		}
		catch (InterruptedException e) {
			System.out.println("Join interrupted");
		}

		// interrompe il thread se risulta ancora attivo
		if (t.isAlive()) {
			t.interrupt();
			System.out.println("Tempo scaduto");
		}
		else
			System.out.printf("Calcolo eseguito correttamente \nValore di pi greco approssimato %f\n", t.getPi());
	}
}
