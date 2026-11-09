package edu.calpoly.csc3100.fantasy;

import javax.swing.*;
import java.awt.*;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class GamePanel extends JPanel {
  private final GameModel model;
  public GamePanel(GameModel model) {
    this.model = model;
    setPreferredSize(new Dimension(640, 480));
    setFocusable(true);
    model.addListener(event -> repaint());
    // TODO: bind arrow keys to movement of the local player.
  }
  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawString("Fantasy Adventure — TODO: draw players", 20, 30);
  }
}
