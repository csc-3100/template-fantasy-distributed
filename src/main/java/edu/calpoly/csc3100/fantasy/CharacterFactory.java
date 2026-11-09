package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class CharacterFactory {
  private CharacterFactory() { }
  public static Character create(String type) {
    if ("warrior".equalsIgnoreCase(type)) return new Warrior();
    throw new IllegalArgumentException("Unsupported character: " + type);
  }
}
