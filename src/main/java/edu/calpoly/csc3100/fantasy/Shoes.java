package edu.calpoly.csc3100.fantasy;

/**
 * Visual footwear that preserves the wrapped character’s stats.
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class Shoes implements Character {
  private final Character wrapped;
  public Shoes(Character wrapped) { this.wrapped = java.util.Objects.requireNonNull(wrapped); }
  @Override public void draw(java.awt.Graphics2D g, int x, int y) {
    wrapped.draw(g, x, y);
    // TODO: draw a visible pair of shoes below the body, within the tile.
  }

  public int attack() { return wrapped.attack(); }
  public int defense() { return wrapped.defense(); }
  public String type() { return wrapped.type(); }
}
