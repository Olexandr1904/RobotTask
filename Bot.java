import org.apache.log4j.Logger;

/**
 * Class of Robot. Behaviour depend on input parameter.
 * Each Robot spend 10% of charge every 1000ms
 */
class Bot extends Thread {

    private static final int CHARGE_UP_SLEEP = 500;
    private static final int GREEDY_BEHAVIOUR_SLEEP_TIME = 500;
    private static final int GENTLEMEN_BEHAVIOR_SLEEP_TIME = 200;

    static final Logger log = Logger.getLogger(Bot.class);

    private static int i = 0;

    private int robotId = i++;
    private int behaviour;
    private boolean isWorking, stop;
    private Plug plugLeft, plugRight;
    volatile private int charge;

    public Bot(Plug plugLeft, Plug plugRight, int behaviour) {
        this.plugLeft = plugLeft;
        this.plugRight = plugRight;
        this.behaviour = behaviour;
        isWorking = true;
        stop = false;
        charge = 50;
    }

    @Override
    public void run() {
        setName("Robot" + robotId);
        setPriority(MAX_PRIORITY);
        while (!stop) {
            switch (behaviour) {
                case 1:
                    chargingRandom();
                    break;
                case 2:
                    greedy();
                    break;
                case 3:
                    gentleman();
                default:
                    break;
            }
        }
        if (charge > 80) {
            log.info(getName() + " final charge 100. Behaviour " + behaviour);
        } else {
            log.info(getName() + " final charge " + charge + ". Behaviour " + behaviour);
        }
    }

    // First type of behaviour.
    // If charge <100 get both parts of plug charge for 10% and go to sleep for 100-300ms
    private void chargingRandom() {
        if (charge <= 100) {
//            if (!plugLeft.isInUse() && !plugRight.isInUse()) {
                plugLeft.get();
                log.info(getName() + " get " + plugLeft);
                plugRight.get();
                log.info(getName() + " get " + plugRight);
                chargeUp();
                plugLeft.put();
                log.info(getName() + " put " + plugLeft);
                plugRight.put();
                log.info(getName() + " put " + plugRight);
                try {
                    sleep((long) (Math.random() * 200));
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }

//            }
        }
    }

    // Second type of behaviour.
    // If charge <60% get both parts of plug, charge himself for 100% unless check condition every 500ms
    private void greedy() {
        if (charge < 60 && !stop) {
//            if (!plugLeft.isInUse() && !plugRight.isInUse()) {
            plugLeft.get();
            log.info(getName() + " get " + plugLeft);
            plugRight.get();
            log.info(getName() + " get " + plugRight);
            do {
                chargeUp();
            } while (charge < 100);
            plugLeft.put();
            log.info(getName() + " put " + plugLeft);
            plugRight.put();
            log.info(getName() + " put " + plugRight);
//            }
        } else {
            try {
                sleep(GREEDY_BEHAVIOUR_SLEEP_TIME);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    // Third type of behaviour.
    // if neighbour robot has lower charge it release appropriate plug and go to sleep for 200ms
    private void gentleman() {
        int rightCharge, leftCharge;
//        if (!plugLeft.isInUse() && !plugRight.isInUse()) {
            plugLeft.get();
            log.info(getName() + " get " + plugLeft);
            plugRight.get();
            log.info(getName() + " get " + plugRight);

            // check for first robot
            if (robotId == 0) {
                rightCharge = Modelling.getBotList().get(Modelling.getBotList().size() - 1).charge; //lower Robot
            } else {
                rightCharge = Modelling.getBotList().get(robotId - 1).charge; //lower Robot
            }
            // check for last robot
            if (robotId == Modelling.getBotList().size() - 1) {
                leftCharge = Modelling.getBotList().get(0).charge; //higher Robot
            } else {
                leftCharge = Modelling.getBotList().get(robotId + 1).charge; //higher Robot
            }

            // check neighbours charge
            if (charge <= leftCharge && charge <= rightCharge) {
                chargeUp();
                plugLeft.put();
                log.info(getName() + " put " + plugLeft);
                plugRight.put();
                log.info(getName() + " put " + plugRight);
            } else if (charge > rightCharge || charge > leftCharge) {
                plugLeft.put();
                log.info(getName() + " put " + plugLeft);
                plugRight.put();
                log.info(getName() + " put " + plugRight);
                try {
                    sleep(GENTLEMEN_BEHAVIOR_SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
//        }
    }

    //up charge for 10% and go sleep to 500ms
    private void chargeUp() {
        if (!stop) {
            if (charge < 90) {
                charge = charge + 10;
                isWorking = true;
            } else {
                charge = 100;
                isWorking = false;
            }
            if (charge > 10) {
                log.info(getName() + " charge " + charge);
            } else {
                log.info(getName() + " charge 0");
            }
            try {
                sleep(CHARGE_UP_SLEEP);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    // getters and setters
    public boolean isWorking() {
        return isWorking;
    }

    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}