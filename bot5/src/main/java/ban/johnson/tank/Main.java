package ban.johnson.tank;

import com.google.gson.Gson;
import ban.johnson.tank.command.Command;
import ban.johnson.tank.entities.GameState;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final String ROUNDS_DIRECTORY = "rounds";
    private static final String STATE_FILE_NAME = "state.json";

    /**
     * Read the current state, feed it to the bot, get the output and print it to
     * stdout
     *
     * @param args the args
     **/
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();
        Bot bot = new Bot();

        while (true) {
            try {
                int roundNumber = sc.nextInt();

                String statePath = String.format("./%s/%d/%s", ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
                String state = new String(Files.readAllBytes(Paths.get(statePath)));

                GameState gameState = gson.fromJson(state, GameState.class);
                Command command = bot.run(gameState);

                System.out.println(String.format("C;%d;%s", roundNumber, command.render()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
