/**
 * UfficioPostaleMigliorato modella il comportamento di un ufficio postale. Con l'utilizzo di chiamate bloccanti è
 * stato possibile evitare l'attesa attiva
 * @author Samuel Fabrizi
 * @version 1.1
 */
class UfficioPostaleMigliorato extends UfficioPostale {
    private final Object lock = new Object ();

    /**
     * @param k dimensione della sala degli sportelli
     */
    public UfficioPostaleMigliorato(int k) {
        super(k);
    }

    @Override
    public void aperturaUfficio(int n) {
        this.sportelli = new UfficioPostaleExecutor(
                this.numeroSportelli,
                this.numeroSportelli,
                0L,
                this.salaSportelli,
                this.lock
        );

        System.out.println("Ufficio aperto");

        for(int i=0; i<n; i++){
            this.salaAttesa.add(new Cliente(i));
        }

    }

    @Override
    public void aperturaSportelli() {
        while(!salaAttesa.isEmpty()) {
            Cliente c = salaAttesa.poll();

            // System.out.printf("Cliente numero %d: prova ad entrare nella sala sportelli, persone nella sala sportelli = %d \n", c.getNumero(), salaSportelli.size());

            sportelli.execute(c);

            System.out.printf(
                    "Cliente numero %d: entra nella sala sportelli, persone nella sala sportelli = %d \n",
                    c.getNumero(),
                    salaSportelli.size()
            );
        }
    }
}