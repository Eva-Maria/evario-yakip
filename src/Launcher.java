
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

        if (args.length >= 1) {
            if (args[0].equals(MODE_AUTO)) {
                new Thread(serverLauncher).start();
                new Client();
                new Client();
                new Client();
                return;
            }

            if (args[0].equals(MODE_FULLAUTO)) {
                new Thread(serverLauncher).start();
                new Client();
                new Client();
                new Client();
                new Client();
                return;
            }
        }

        new Client();
    }
}
