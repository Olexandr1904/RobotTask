import org.apache.log4j.Logger;

/**
 * Class with decrease charge logic and checking finish condition
 */
class Service extends Thread {

    static final Logger log = Logger.getLogger(Service.class);

    static private String resultString;

    private static final int CHARGE_UPDATE_TIME = 1000;
    private static final int DEADLOCK_CHECKING_DELAY = 2000;

    private boolean serviceStop = false;

    @Override
    public void run() {

        int nonWorkingBots;
        resultString = "";

        while (!serviceStop) {
            nonWorkingBots = 0;
            try {
                sleep(CHARGE_UPDATE_TIME);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }

            for (Bot bot: Modelling.getBotList()) {
                // count non working bots, when charge =0 or charge =100
                if (bot.getCharge() == 100 || bot.isStop()) {
                    nonWorkingBots++;
                }
                // spend charge and set stop flag if charge =0
                if (bot.getCharge() > 0) {
                    bot.setCharge(bot.getCharge() - 10);
                    bot.setIsWorking(true);
                    if (bot.getCharge() == 0) {
                        bot.setStop(true);
                    }
                }
            }
            // check non working bots quantity, and stop thread if non working bots = bots quantity
            if (nonWorkingBots == Modelling.getBotList().size()) {
                for (Bot bot: Modelling.getBotList()) {
                    if (bot.getCharge() > 50){
                        resultString = resultString + bot.getName() + " charge " + (bot.getCharge() + 10) + "; ";
                    } else {
                        resultString = resultString + bot.getName() + " charge " + bot.getCharge() + "; ";
                    }
                    bot.setStop(true);
                    serviceStop = true;
                }
                stopWorking();
                log.info(resultString);
            }
        }
    }

//    Stop working the whole threads if app has deadlock
    private  void stopWorking() {
//        delay for waiting until the whole threads had completed
//        if the're not in a deadlock
        try {
            sleep(DEADLOCK_CHECKING_DELAY);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        // if thread has charge and yet alive this is deadlock.
        for (Bot bot: Modelling.getBotList()) {
            if (bot.isAlive()) {
                log.error("Deadlock");
                System.exit(1);
            }
        }
    }

}