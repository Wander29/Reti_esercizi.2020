package assignment03_v2;

import java.util.Comparator;

public abstract class Utente extends AbstractLab {
    protected final int MAX_EXEC = 20;

    protected static final int PROF_PRIORITY = 3;
    protected static final int TESI_PRIORITY = 2;
    protected static final int STUD_PRIORITY = 1;
    protected Manager tutor;

    private int priority;

    public abstract void joinLab();
    public abstract void exitLab();

    protected void setPriority(int pri) {
        this.priority = pri;
    }

    protected int getPriority() {
        return this.priority;
    }
}
