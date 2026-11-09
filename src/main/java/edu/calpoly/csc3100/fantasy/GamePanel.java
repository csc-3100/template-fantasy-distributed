package edu.calpoly.csc3100.fantasy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/** Draws the board and binds commands for the local player. */
public final class GamePanel extends JPanel {
  static final int TILE = 40;
  private final Blackboard blackboard;

  public GamePanel(Blackboard blackboard, String username) {
    this.blackboard = blackboard;
    setPreferredSize(new Dimension(Blackboard.COLUMNS * TILE, Blackboard.ROWS * TILE));
    blackboard.addPropertyChangeListener(Blackboard.PLAYERS, event -> {
      if (SwingUtilities.isEventDispatchThread()) repaint();
      else SwingUtilities.invokeLater(this::repaint);
    });
    bind("UP", () -> blackboard.moveLocal(username, 0, -1));
    bind("DOWN", () -> blackboard.moveLocal(username, 0, 1));
    bind("LEFT", () -> blackboard.moveLocal(username, -1, 0));
    bind("RIGHT", () -> blackboard.moveLocal(username, 1, 0));
  }

  private void bind(String key, Runnable command) {
    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
    getActionMap().put(key, new AbstractAction() {
      @Override public void actionPerformed(ActionEvent event) {
        command.run();
      }
    });
  }

  @Override protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    Graphics2D g = (Graphics2D) graphics.create();
    try {
      g.setColor(Color.LIGHT_GRAY);
      for (int row = 0; row < Blackboard.ROWS; row++) {
        for (int col = 0; col < Blackboard.COLUMNS; col++) {
          g.drawRect(col * TILE, row * TILE, TILE, TILE);
        }
      }
      for (PlayerState state : blackboard.players()) {
        int x = state.x() * TILE;
        int y = state.y() * TILE;
        CharacterFactory.create(state).draw(g, x, y);
        g.setColor(Color.BLACK);
        g.drawString(state.player(), x + 2, y + 38);
      }
    } finally {
      g.dispose();
    }
  }
}
