package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class CharacterFactory {
  private CharacterFactory() { }
  public static Character create(PlayerState state) {
    Character character = create(state.type());
    for (String item : state.equipment()) {
      character = switch (item) {
        case "sword" -> new Sword(character);
        case "shield" -> new Shield(character);
        case "shoes" -> new Shoes(character);
        default -> throw new IllegalArgumentException("Unsupported equipment: " + item);
      };
    }
    return character;
  }

  public static Character create(String type) {
    if ("warrior".equalsIgnoreCase(type)) return new Warrior();
    throw new IllegalArgumentException("Unsupported character: " + type);
  }
}
