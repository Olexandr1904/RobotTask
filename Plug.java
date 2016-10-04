import org.apache.log4j.Logger;

/**
 * Class with plug get and release logic
 */
class Plug {

    static final Logger log = Logger.getLogger(Plug.class);

    private static int i = 0;

    private boolean inUse;
    private int id = ++i;
    private String type;

    public Plug() {
        inUse = false;
        if ((id % 2) == 0) {
            type = "Cable ";
        } else {
            type = "Plug ";
        }
    }

    // get plug
    public synchronized void get() {
        while (inUse) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        inUse = true;
        notify();
    }

    // release plug
    public synchronized void put() {
        while (!inUse) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        inUse = false;
        notify();
    }

    public boolean isInUse() {
        return inUse;
    }

    @Override
    public String toString() {
        return (type + String.valueOf(id / 2));
    }

}

