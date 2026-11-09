package edu.calpoly.csc3100.fantasy;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Objects;
import java.util.function.Consumer;

/** Subscribes to team snapshots and forwards valid remote state to the application. */
public final class Subscriber implements AutoCloseable {
  private final IMqttClient client;

  public Subscriber(String broker, String team, String username,
                    Consumer<PlayerState> receiver) throws MqttException {
    this(new MqttClient(broker, MqttClient.generateClientId(), new MemoryPersistence()),
        team, username, receiver);
  }

  Subscriber(IMqttClient client, String team, String username,
             Consumer<PlayerState> receiver) throws MqttException {
    this.client = client;
    IncomingPlayerHandler handler = new IncomingPlayerHandler(team, username, receiver);
    try {
      client.connect();
      client.subscribe(topicPrefix(team) + "+", 1,
          (topic, message) -> handler.accept(topic, message.getPayload()));
    } catch (MqttException failure) {
      try {
        close();
      } catch (MqttException cleanup) {
        failure.addSuppressed(cleanup);
      }
      throw failure;
    }
  }

  static String topicPrefix(String team) {
    return "csc3100/a04/" + Objects.requireNonNull(team) + "/players/";
  }

  @Override public void close() throws MqttException {
    try {
      if (client.isConnected()) client.disconnect();
    } finally {
      client.close();
    }
  }
}
