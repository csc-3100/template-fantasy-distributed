package edu.calpoly.csc3100.fantasy;

import org.eclipse.paho.client.mqttv3.*;
import java.nio.charset.StandardCharsets;

/**
 * MQTT transport for player state. Incoming processing is isolated for unit tests.
 *
 * @author CSC 3100 Instructional Team
 * @version 1.1
 */
public final class Publisher implements GamePublisher, AutoCloseable {
  private final IMqttClient client;
  private final String topicPrefix;

  public Publisher(String broker, String team) throws MqttException {
    this(new MqttClient(broker, MqttClient.generateClientId(),
        new org.eclipse.paho.client.mqttv3.persist.MemoryPersistence()), team);
    try {
      client.connect();
    } catch (MqttException failure) {
      client.close();
      throw failure;
    }
  }

  Publisher(IMqttClient client, String team) {
    this.client = client;
    topicPrefix = Subscriber.topicPrefix(team);
  }

  public void publish(PlayerState state) {
    try {
      MqttMessage message = new MqttMessage(
          PlayerCodec.encode(state).getBytes(StandardCharsets.UTF_8));
      message.setQos(1);
      message.setRetained(true);
      client.publish(topicPrefix + state.player(), message);
    } catch (MqttException e) {
      throw new IllegalStateException("MQTT publish failed", e);
    }
  }

  public void close() throws MqttException {
    try {
      if (client.isConnected()) client.disconnect();
    } finally {
      client.close();
    }
  }
}
