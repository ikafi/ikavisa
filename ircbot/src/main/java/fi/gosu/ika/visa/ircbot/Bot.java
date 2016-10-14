package fi.gosu.ika.visa.ircbot;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Aikain on 13.10.2016.
 */
public class Bot extends PircBot {

    private MessageHandler messageHandler;
    private Game game;

    public Bot(String name, String login) {
        this.setName(name);
        this.setLogin(login);
        this.setVerbose(true);
        this.messageHandler = new MessageHandler(this);
    }

    public void connect() {
        try {
            this.connect("irc.OnlineGamesNet.net");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IrcException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void restart() {
        try {
            Process p = Runtime.getRuntime().exec("mvn exec:java");
            int exitCode = p.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            System.out.println("Finished with code: " + String.valueOf(exitCode));
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void quit() {
        this.quitServer();
        System.exit(0);
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        messageHandler.onMessage(channel, sender, login, hostname, message);
    }

    public boolean startGame(String channel, String sender, String login, String hostname, String[] args) {
        try {
            game = (Game) Class.forName("fi.gosu.ika.visa.ircbot.game." + (args[0].charAt(0) + "").toUpperCase() + (args[0].length() > 1 ? args[0].substring(1) : "")).newInstance();
            game.start(this, channel, sender, login, hostname, args);
            game.simpleHelp();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void stopGame() {
        if (game != null) {
            game.stop();
        }
    }

    public Game getGame() {
        return game;
    }

    public void clearGame() {
        this.game = null;
    }
}
