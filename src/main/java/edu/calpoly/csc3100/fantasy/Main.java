package edu.calpoly.csc3100.fantasy;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Runs one Swing client with its own publisher and subscriber. */
public final class Main {
  private Main() { }

  public static void main(String[] args) throws Exception {
    String username = System.getenv().getOrDefault("PLAYER",
        "player-" + UUID.randomUUID().toString().substring(0, 8));
    String team = System.getenv().getOrDefault("TEAM", "demo");
    String broker = System.getenv().getOrDefault("MQTT_BROKER", "tcp://broker.hivemq.com:1883");
    CountDownLatch closed = new CountDownLatch(1);

    try (Publisher publisher = new Publisher(broker, team)) {
      ExecutorService outgoing = Executors.newSingleThreadExecutor();
      Blackboard blackboard = new Blackboard(state -> outgoing.execute(() -> publisher.publish(state)));
      try (Subscriber subscriber = new Subscriber(broker, team, username,
          state -> SwingUtilities.invokeLater(() -> blackboard.applyRemote(state)))) {
        SwingUtilities.invokeAndWait(() -> {
          blackboard.addLocalPlayer(new PlayerState(username, "warrior", 0, 0, List.of()));
          showWindow(blackboard, username, closed);
        });
        closed.await();
      } finally {
        outgoing.shutdown();
        if (!outgoing.awaitTermination(10, TimeUnit.SECONDS)) outgoing.shutdownNow();
      }
    }
  }

  private static void showWindow(Blackboard blackboard, String username, CountDownLatch closed) {
    JFrame frame = new JFrame("Fantasy Adventure — " + username);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosed(WindowEvent event) {
        closed.countDown();
      }
    });
    frame.add(new GamePanel(blackboard, username), BorderLayout.CENTER);

    JPanel controls = new JPanel();
    for (String item : List.of("sword", "shield", "shoes")) {
      JCheckBox toggle = new JCheckBox(item);
      toggle.addActionListener(event -> {
        if (toggle.isSelected()) blackboard.equipLocal(username, item);
        else blackboard.unequipLocal(username, item);
      });
      controls.add(toggle);
    }
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setVisible(true);
  }
}
