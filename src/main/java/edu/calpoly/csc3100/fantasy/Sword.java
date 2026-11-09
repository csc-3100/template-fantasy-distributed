package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class Sword implements Character {
  private final Character wrapped;
  public Sword(Character wrapped) { this.wrapped = java.util.Objects.requireNonNull(wrapped); }
  @Override public void draw(java.awt.Graphics2D g, int x, int y) {
    wrapped.draw(g, x, y);
    // TODO: draw a visible sword to the right of the body, within the tile.
  }

  public int attack() { return wrapped.attack(); /* TODO: add 5 */ }
  public int defense() { return wrapped.defense(); }
  public String type() { return wrapped.type(); }
}
