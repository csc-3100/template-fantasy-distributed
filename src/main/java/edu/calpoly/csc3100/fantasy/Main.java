package edu.calpoly.csc3100.fantasy;

import javax.swing.*;
import java.util.List;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class Main {
  private Main() { }
  public static void main(String[] args) throws Exception {
    String username = System.getenv().getOrDefault("PLAYER", "player" + System.currentTimeMillis()%10000);
    String team = System.getenv().getOrDefault("TEAM", "demo");
    String broker = System.getenv().getOrDefault("MQTT_BROKER", "tcp://broker.hivemq.com:1883");
    final GameModel[] holder = new GameModel[1];
    try (MqttGateway gateway = new MqttGateway(broker, team, username, state -> {
      if (holder[0] != null) holder[0].applyRemote(state);
    })) {
      GameModel model = new GameModel(gateway);
      holder[0] = model;
      model.addLocalPlayer(new PlayerState(username, "warrior", 0, 0, List.of()));
      if (java.util.Arrays.asList(args).contains("--console")) {
        System.out.println("Console mode: w/a/s/d, sword, shield, quit (TODO: implement commands)");
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
          while (scanner.hasNextLine()) { if ("quit".equalsIgnoreCase(scanner.nextLine().trim())) break; }
        }
      } else {
        SwingUtilities.invokeAndWait(() -> {
          JFrame frame = new JFrame("Fantasy Adventure — " + username);
          frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          frame.add(new GamePanel(model)); frame.pack(); frame.setVisible(true);
        });
        while (java.awt.Frame.getFrames().length > 0 && java.util.Arrays.stream(java.awt.Frame.getFrames()).anyMatch(java.awt.Frame::isDisplayable)) Thread.sleep(200);
      }
    }
  }
}
