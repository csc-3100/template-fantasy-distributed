package edu.calpoly.csc3100.fantasy;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MqttTransportTest {
  @Test void publisherSendsRetainedUtf8SnapshotAtQosOne() throws Exception {
    PlayerState state = new PlayerState("josé", "warrior", 2, 3, List.of("shoes"));
    List<Object[]> publications = new ArrayList<>();
    IMqttClient client = fakeClient((method, args) -> {
      if (method.equals("publish")) publications.add(args);
    });
    try (Publisher publisher = new Publisher(client, "team01")) {
      publisher.publish(state);
    }
    assertEquals(1, publications.size());
    Object[] args = publications.getFirst();
    assertEquals("csc3100/a04/team01/players/josé", args[0]);
    MqttMessage message = (MqttMessage) args[1];
    assertEquals(1, message.getQos());
    assertTrue(message.isRetained());
    assertArrayEquals(PlayerCodec.encode(state).getBytes(StandardCharsets.UTF_8), message.getPayload());
  }

  @Test void subscriberUsesTeamWildcardAndNeverEchoesRemoteSnapshot() throws Exception {
    List<PlayerState> published = new ArrayList<>();
    Blackboard board = new Blackboard(published::add);
    PlayerState local = new PlayerState("alice", "warrior", 0, 0, List.of());
    PlayerState remote = new PlayerState("bob", "warrior", 2, 3, List.of("shield"));
    board.addLocalPlayer(local);
    AtomicReference<IMqttMessageListener> listener = new AtomicReference<>();
    IMqttClient client = fakeClient((method, args) -> {
      if (method.equals("subscribe")) {
        assertEquals("csc3100/a04/team01/players/+", args[0]);
        assertEquals(1, args[1]);
        listener.set((IMqttMessageListener) args[2]);
      }
      assertNotEquals("publish", method);
    });
    try (Subscriber subscriber = new Subscriber(client, "team01", "alice", board::applyRemote)) {
      listener.get().messageArrived("csc3100/a04/team01/players/bob",
          new MqttMessage(PlayerCodec.encode(remote).getBytes(StandardCharsets.UTF_8)));
    }
    assertAll(() -> assertEquals(remote, board.getPlayer("bob")),
        () -> assertEquals(List.of(local), published));
  }

  private static IMqttClient fakeClient(java.util.function.BiConsumer<String, Object[]> calls) {
    return (IMqttClient) Proxy.newProxyInstance(IMqttClient.class.getClassLoader(),
        new Class<?>[]{IMqttClient.class}, (proxy, method, args) -> {
          calls.accept(method.getName(), args);
          return method.getName().equals("isConnected") ? false : null;
        });
  }
}
