package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
class CharacterTest {
  @Test void factoryCreatesWarrior() { assertEquals("warrior", CharacterFactory.create("warrior").type()); }
  @Test void factoryRejectsUnknown() { assertThrows(IllegalArgumentException.class, () -> CharacterFactory.create("dragon")); }
  @Test void swordAddsAttack() { assertEquals(15, new Sword(new Warrior()).attack()); }
  @Test void shieldAddsDefense() { assertEquals(10, new Shield(new Warrior()).defense()); }
  @Test void decoratorsStack() { Character c = new Shield(new Sword(new Warrior())); assertAll(() -> assertEquals(15,c.attack()), () -> assertEquals(10,c.defense())); }

  @Test void factoryWrapsInEquipmentOrder() {
    assertInstanceOf(Shoes.class, character(List.of("sword", "shield", "shoes")));
    assertInstanceOf(Sword.class, character(List.of("shoes", "shield", "sword")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"sword", "shield", "shoes"})
  void eachDecoratorDelegatesThenDrawsWithinTile(String item) {
    BufferedImage image = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
    int[] calls = {0};
    Character base = new WarriorSpy(calls);
    Character decorated = switch (item) {
      case "sword" -> new Sword(base);
      case "shield" -> new Shield(base);
      case "shoes" -> new Shoes(base);
      default -> throw new AssertionError(item);
    };
    Graphics2D g = image.createGraphics();
    try {
      decorated.draw(g, 20, 20);
    } finally {
      g.dispose();
    }
    assertEquals(1, calls[0]);
    int changed = 0;
    for (int y = 0; y < 80; y++) {
      for (int x = 0; x < 80; x++) {
        if (x >= 20 && x < 60 && y >= 20 && y < 60) {
          if (image.getRGB(x, y) != Color.WHITE.getRGB()) changed++;
        } else {
          assertEquals(0, image.getRGB(x, y), "Drawing must stay within the tile");
        }
      }
    }
    assertTrue(changed > 0, "Equipment should be drawn on top of the wrapped character");
  }

  @Test void rebuildingAfterRemovalErasesOnlyRemovedOverlay() {
    BufferedImage bare = draw(List.of());
    BufferedImage sword = draw(List.of("sword"));
    BufferedImage shoes = draw(List.of("shoes"));
    BufferedImage both = draw(List.of("sword", "shoes"));
    int swordPixels = 0;
    int shoePixels = 0;
    for (int y = 0; y < 40; y++) {
      for (int x = 0; x < 40; x++) {
        if (sword.getRGB(x, y) != bare.getRGB(x, y)) {
          swordPixels++;
          assertEquals(sword.getRGB(x, y), both.getRGB(x, y));
        }
        if (shoes.getRGB(x, y) != bare.getRGB(x, y)) {
          shoePixels++;
          assertEquals(shoes.getRGB(x, y), both.getRGB(x, y));
          assertEquals(bare.getRGB(x, y), sword.getRGB(x, y));
        }
      }
    }
    assertTrue(swordPixels > 0 && shoePixels > 0);
  }

  private static Character character(List<String> equipment) {
    return CharacterFactory.create(new PlayerState("alice", "warrior", 0, 0, equipment));
  }

  private static BufferedImage draw(List<String> equipment) {
    BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    try {
      character(equipment).draw(g, 0, 0);
    } finally {
      g.dispose();
    }
    return image;
  }

  private record WarriorSpy(int[] calls) implements Character {
    public int attack() { return 10; }
    public int defense() { return 5; }
    public String type() { return "warrior"; }

    public void draw(Graphics2D g, int x, int y) {
      assertEquals(20, x);
      assertEquals(20, y);
      calls[0]++;
      g.setColor(Color.WHITE);
      g.fillRect(x, y, 40, 40);
    }
  }
}
