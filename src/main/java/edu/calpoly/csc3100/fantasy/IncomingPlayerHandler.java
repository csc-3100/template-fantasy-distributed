package edu.calpoly.csc3100.fantasy;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Processes received MQTT player-state messages independently of the broker.
 * Students complete the topic, decoding, and self-message handling.
 *
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class IncomingPlayerHandler {
  private final String topicPrefix;
  private final String localUsername;
  private final Consumer<PlayerState> receiver;

  public IncomingPlayerHandler(String team, String localUsername,
                               Consumer<PlayerState> receiver) {
    this.topicPrefix = "csc3100/a04/" + Objects.requireNonNull(team) + "/players/";
    this.localUsername = Objects.requireNonNull(localUsername);
    this.receiver = Objects.requireNonNull(receiver);
  }

  public void accept(String topic, byte[] payload) {
    // TODO: reject topics outside this team's player namespace.
    // TODO: decode UTF-8 JSON using PlayerCodec.
    // TODO: ignore the local player's own state.
    // TODO: deliver valid remote state to receiver exactly once.
    // TODO: handle malformed JSON without terminating the MQTT callback.
  }
}
