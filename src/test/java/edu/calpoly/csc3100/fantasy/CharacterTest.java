package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
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
}
