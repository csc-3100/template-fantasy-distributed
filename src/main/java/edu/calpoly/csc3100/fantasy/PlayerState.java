package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public record PlayerState(String player, String type, int x, int y, java.util.List<String> equipment) {
  public PlayerState { equipment = java.util.List.copyOf(equipment); }
}
