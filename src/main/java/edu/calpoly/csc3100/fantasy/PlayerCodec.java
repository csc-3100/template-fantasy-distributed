package edu.calpoly.csc3100.fantasy;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class PlayerCodec {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private PlayerCodec() { }
  public static String encode(PlayerState state) {
    try { return MAPPER.writeValueAsString(state); }
    catch (Exception e) { throw new IllegalArgumentException("Cannot encode player", e); }
  }
  public static PlayerState decode(String json) {
    // TODO: decode JSON and validate the required fields, type, board coordinates,
    // and equipment before returning a snapshot. See the README and acceptance tests.
    throw new IllegalArgumentException("Not implemented");
  }
}
