public class Lab {

        //useful constants used to replace "true" and "false" into more readable words
        private static final boolean AVAILABLE = true;
        private static final boolean NOT_AVAILABLE = false;

        private boolean professorIsBooking; //true if a prof has booked or is booking all the computers
        private final Object mutex = new Object();    //mutex used to handle race conditions
        //computers[i] is true if the computer number i is available, false otherwise
        //the array is final because I do not want to have more or less computers during runtime
        private final Boolean[] computers;
        
        public Lab(int size) {
            //TODO throw exception if size is not a valid integer
            computers = new Boolean[size];
        }
        
        //private method to book a computer without syncronization
        private void book(int index) {
            //book the computer number "index" by setting that computer to not available
            computers[index] = NOT_AVAILABLE;
        }

        //private method to leave a computer without syncronization
        private void leave(int index) {
            //set the computer number "index" as available
            computers[index] = AVAILABLE;
        }

        //private method to find an available computer without syncronization
        private int findAvailable() {
            for (int i = 0; i < computers.length; i++) {
                if (computers[i] == AVAILABLE) return i;
            }
            return -1;
        }

        //public method to book a computer with syncronization
        public int bookOne() throws InterruptedException {
            int index = -1;
            synchronized (mutex) {
                //find an available computer
                index = findAvailable();
                //Avoid booking if a prof is booking
                while (professorIsBooking || index == -1) {
                    mutex.wait();
                    if (!professorIsBooking)    //avoid finding an available computer if a prof is booking
                        index = findAvailable();
                }
                //book the computer
                book(index);
            }
            return index;   //return the booked computer's index
        }

        //public method to book all the computers with syncronization
        public void bookAll() throws InterruptedException {
            synchronized (mutex) {
                //Avoid multiple prof
                while (professorIsBooking) {
                    mutex.wait();
                }
                professorIsBooking = true;
                //book each computer until every computer is booked
                int counter = 0;
                while (counter != computers.length) {
                    //wait until the computer is available
                    while (computers[counter] == NOT_AVAILABLE) {
                        mutex.wait();
                    }
                    //book the computer
                    book(counter);
                    //go next
                    counter++;
                }
            }
        }

        //public method to leave all the computers with syncronization
        public void leaveAll() {
            synchronized (mutex) {
                //call leave(index) for each computer
                professorIsBooking = false;
                for (int i = 0; i < computers.length; i++) {
                    leave(i);
                }
                //wake up everybody
                mutex.notifyAll();
            }
        }

        //public method to leave a computer with syncronization
        public void leaveOne(int index) {
            synchronized (mutex) {
                //leave the computer
                leave(index);
                //wake up everybody
                mutex.notifyAll();
            }
        }
    }

    //Generic user of the laboratory
    public abstract class Utente implements Runnable {

        protected Lab lab;

        //Important constructor because thanks to it the subclasses must get a reference to the lab object
        public Utente(Lab lab) { 
            this.lab = lab;
        }

        @Override
        public void run() {
            int i = 0;
            int k = ThreadLocalRandom.current().nextInt(20);
            while (i < k) {
                try {
                    joinLab();
                    useLab();
                    leaveLab();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                i++;
            }
        }

        protected abstract void joinLab() throws InterruptedException;

        protected abstract void useLab() throws InterruptedException;

        protected abstract void leaveLab();
    }

    public class Professor extends Utente {

        public Professor(Lab lab) {
            super(lab);
        }

        @Override
        protected void joinLab() throws InterruptedException {
            //Random waiting before booking the lab
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
        }

        @Override
        protected void useLab() throws InterruptedException {
            Thread.sleep(ThreadLocalRandom.current().nextInt(2000));
            lab.bookAll();
            System.out.println("Just booked all the computer. I'm a professor, not a useless student :)");
        }

        @Override
        protected void leaveLab() {
            lab.leaveAll();
            System.out.println("Just left all the computers");
        }
    }

    public class Student extends Utente {
        private int bookedPC;
        
        public Student(Lab lab) {
            super(lab);
        }

        @Override
        protected void joinLab() throws InterruptedException {
            bookedPC = lab.bookOne();
            System.out.printf("Just booked the pc number %d", bookedPC);
        }

        @Override
        protected void useLab() throws InterruptedException {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
        }

        @Override
        protected void leaveLab() {
            lab.leaveOne(bookedPC);
            System.out.printf("Just left the pc number %d", bookedPC);
        }
    }