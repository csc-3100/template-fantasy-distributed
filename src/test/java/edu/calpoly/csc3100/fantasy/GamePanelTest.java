package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GamePanelTest {
  @ParameterizedTest
  @CsvSource({"UP,2,1", "DOWN,2,3", "LEFT,1,2", "RIGHT,3,2"})
  void arrowBindingMovesLocalPlayerOneTile(String key, int x, int y) throws Exception {
    AtomicReference<PlayerState> result = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      Blackboard board = new Blackboard(state -> {});
      board.addLocalPlayer(new PlayerState("alice", "warrior", 2, 2, List.of()));
      GamePanel panel = new GamePanel(board, "alice");
      Object binding = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
          .get(KeyStroke.getKeyStroke(key));
      panel.getActionMap().get(binding)
          .actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, key));
      result.set(board.getPlayer("alice"));
    });
    assertEquals(new PlayerState("alice", "warrior", x, y, List.of()), result.get());
  }
}
