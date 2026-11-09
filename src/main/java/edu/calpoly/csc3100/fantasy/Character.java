package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public interface Character {
  void draw(java.awt.Graphics2D g, int x, int y);
  int attack();
  int defense();
  String type();
}
