package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class Shield implements Character {
  private final Character wrapped;
  public Shield(Character wrapped) { this.wrapped = java.util.Objects.requireNonNull(wrapped); }
  public int attack() { return wrapped.attack(); }
  public int defense() { return wrapped.defense(); /* TODO: add 5 */ }
  public String type() { return wrapped.type(); }
}
