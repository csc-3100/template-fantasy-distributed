package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Instructor acceptance tests for broker-independent MQTT message processing.
 *
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
class IncomingPlayerHandlerTest {
  private static byte[] message(String player) {
    return PlayerCodec.encode(new PlayerState(player, "warrior", 2, 4, List.of()))
        .getBytes(StandardCharsets.UTF_8);
  }

  @Test void deliversRemotePlayerExactlyOnce() {
    List<PlayerState> received = new ArrayList<>();
    IncomingPlayerHandler handler = new IncomingPlayerHandler("team01", "alice", received::add);
    handler.accept("csc3100/a04/team01/players/bob", message("bob"));
    assertAll(() -> assertEquals(1, received.size()),
        () -> assertEquals("bob", received.get(0).player()));
  }

  @Test void ignoresOwnState() {
    List<PlayerState> received = new ArrayList<>();
    IncomingPlayerHandler handler = new IncomingPlayerHandler("team01", "alice", received::add);
    handler.accept("csc3100/a04/team01/players/alice", message("alice"));
    assertTrue(received.isEmpty());
  }

  @Test void ignoresOtherTeams() {
    List<PlayerState> received = new ArrayList<>();
    IncomingPlayerHandler handler = new IncomingPlayerHandler("team01", "alice", received::add);
    handler.accept("csc3100/a04/team02/players/bob", message("bob"));
    assertTrue(received.isEmpty());
  }

  @Test void invalidJsonDoesNotCrashSubscriber() {
    List<PlayerState> received = new ArrayList<>();
    IncomingPlayerHandler handler = new IncomingPlayerHandler("team01", "alice", received::add);
    assertDoesNotThrow(() -> handler.accept("csc3100/a04/team01/players/bob",
        "invalid json".getBytes(StandardCharsets.UTF_8)));
    assertTrue(received.isEmpty());
  }
}
