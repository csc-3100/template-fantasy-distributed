package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class Warrior implements Character {
  @Override public void draw(java.awt.Graphics2D g, int x, int y) {
    g.setColor(java.awt.Color.BLUE);
    g.fillOval(x + 10, y + 8, 20, 20);
  }

  public int attack() { return 10; }
  public int defense() { return 5; }
  public String type() { return "warrior"; }
}
