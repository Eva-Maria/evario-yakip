
import lenz.htw.yakip.Server;

import java.io.IOException;

/**
 * Created by eve on 4/11/16.
 */
public class Launcher {

    static final String SERVER_IS_ALREADY_RUNNING_DO_NOTHING = "Server is already running. Do nothing.";
    static final String MODE_AUTO = "auto";
    static final String MODE_FULLAUTO = "fullauto";

    static final Runnable serverLauncher = () -> {
        try {
            Server.main(Config.SERVER_ARGS);
        } catch (Exception e) {
            System.out.println(SERVER_IS_ALREADY_RUNNING_DO_NOTHING);
        }
    };

    public static void main(String... args) throws IOException {
        String hostName = Config.HOSTNAME;
        long seed = Config.CLIENT_SEED;
//        wrapSystemOut();

        if (args.length >= 1) {
            hostName = args[0];
        }
        if (args.length >= 2) {
            if (args[1].equals(MODE_AUTO)) {
                new Thread(serverLauncher).start();
                waitForServer();
                new Client(hostName, seed);
                new Client(hostName, seed);
                new Client(hostName, seed);
                return;
            }

            if (args[1].equals(MODE_FULLAUTO)) {
                new Thread(serverLauncher).start();
                waitForServer();
                new Client(hostName, seed);
                new Client(hostName, seed);
                new Client(hostName, seed);
                new Client(hostName, seed);
                return;
            }
        }

        new Client(hostName, seed);
    }

    private static void waitForServer() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
