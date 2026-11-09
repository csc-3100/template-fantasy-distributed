package edu.calpoly.csc3100.fantasy;

import org.eclipse.paho.client.mqttv3.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * MQTT transport for player state. Incoming processing is isolated for unit tests.
 *
 * @author CSC 3100 Instructional Team
 * @version 1.1
 */
public final class MqttGateway implements GamePublisher, AutoCloseable {
  private final MqttClient client;
  private final String topicPrefix;

  public MqttGateway(String broker, String team, String username,
                     Consumer<PlayerState> incoming) throws MqttException {
    topicPrefix = "csc3100/a04/" + team + "/players/";
    IncomingPlayerHandler handler = new IncomingPlayerHandler(team, username, incoming);
    client = new MqttClient(broker, MqttClient.generateClientId());
    client.setCallback(new MqttCallback() {
      public void connectionLost(Throwable cause) {
        System.err.println("MQTT disconnected: " + cause);
      }
      public void deliveryComplete(IMqttDeliveryToken token) { }
      public void messageArrived(String topic, MqttMessage message) {
        handler.accept(topic, message.getPayload());
      }
    });
    client.connect();
    client.subscribe(topicPrefix + "+", 1);
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
    client.disconnect();
    client.close();
  }
}
