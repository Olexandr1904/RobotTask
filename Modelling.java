import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with start logic
 */
public class Modelling {

    private static List<Bot> botList;
    static final Logger log = Logger.getLogger(Bot.class);


    public static void startModelling(String[] args) {

        botList = new ArrayList<Bot>(args.length);
        List<Plug> plugList = new ArrayList <Plug> (args.length);

        // check input arguments and create plug collection
        if (args.length < 2) {
            log.error("Wrong arguments");
            System.exit(1);
        }
        for (String arg : args) {
                    try {
                        if (Integer.parseInt(arg) < 1 || Integer.parseInt(arg) > 3) {
                            log.error("Wrong arguments");
                            System.exit(1);
                        } else {
                    plugList.add(new Plug());
                }
            } catch (NumberFormatException e) {
                log.error("Wrong arguments");
                System.exit(1);
            }
        }

        //  create bot collection. different rules for creation first bot
        for (int i = 0; i < args.length; i++) {
            if (i == 0) {
                botList.add(new Bot(plugList.get(args.length - 1), plugList.get(i), Integer.parseInt(args[i])));
            } else {
                botList.add(new Bot(plugList.get(i - 1), plugList.get(i), Integer.parseInt(args[i])));
            }
        }

        //  start threads
        for (int i = 0; i < args.length; i++) {
            botList.get(i).start();
        }

        //  start service for checking exit condition
        Service botService = new Service();
        botService.start();
    }

    public static List<Bot> getBotList() {
        return botList;
    }

}
