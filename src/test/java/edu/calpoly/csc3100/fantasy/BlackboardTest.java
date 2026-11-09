package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlackboardTest {
  private static final PlayerState ALICE = new PlayerState("alice", "warrior", 2, 3, List.of());
  private final List<PlayerState> sent = new ArrayList<>();
  private final List<PropertyChangeEvent> events = new ArrayList<>();
  private final Blackboard board = new Blackboard(sent::add);

  BlackboardTest() {
    board.addPropertyChangeListener(Blackboard.PLAYERS, events::add);
    board.addLocalPlayer(ALICE);
  }

  @Test void joiningStoresNotifiesAndPublishes() {
    assertInstanceOf(PropertyChangeSupport.class, board);
    assertChange(null, ALICE, 1);
  }

  @ParameterizedTest
  @CsvSource({"1,0", "-1,0", "0,1", "0,-1"})
  void movesOneTileAndPublishesOnce(int dx, int dy) {
    board.moveLocal("alice", dx, dy);
    assertChange(ALICE, new PlayerState("alice", "warrior", 2 + dx, 3 + dy, List.of()), 2);
  }

  @ParameterizedTest
  @CsvSource({"0,0", "1,1", "2,0", "0,-2", "-2147483648,0"})
  void rejectsNonCardinalSteps(int dx, int dy) {
    board.moveLocal("alice", dx, dy);
    assertChange(null, ALICE, 1);
  }

  @ParameterizedTest
  @CsvSource({"0,3,-1,0", "15,3,1,0", "2,0,0,-1", "2,10,0,1"})
  void rejectsStepsOffBoard(int x, int y, int dx, int dy) {
    PlayerState edge = new PlayerState("alice", "warrior", x, y, List.of());
    board.addLocalPlayer(edge);
    board.moveLocal("alice", dx, dy);
    assertChange(ALICE, edge, 2);
  }

  @Test void remoteChangesNotifyWithoutPublishing() {
    PlayerState bob = new PlayerState("bob", "warrior", 4, 5, List.of());
    PlayerState updated = new PlayerState("bob", "warrior", 4, 6, List.of("shoes"));
    var snapshot = board.players();
    board.applyRemote(bob);
    board.applyRemote(updated);
    board.applyRemote(updated);
    assertEquals(updated, board.getPlayer("bob"));
    assertEquals(List.of(ALICE), List.copyOf(snapshot));
    assertEquals(List.of(ALICE), sent);
    assertEquals(3, events.size());
    assertEquals(bob, events.getLast().getOldValue());
    assertEquals(updated, events.getLast().getNewValue());
  }

  @Test void remoteStateCannotOverwriteLocalPlayer() {
    board.applyRemote(new PlayerState("alice", "warrior", 5, 6, List.of("sword")));
    assertChange(null, ALICE, 1);
  }

  @Test void localCommandsCannotModifyRemotePlayer() {
    PlayerState bob = new PlayerState("bob", "warrior", 4, 5, List.of("shield"));
    board.applyRemote(bob);
    board.moveLocal("bob", 1, 0);
    board.equipLocal("bob", "sword");
    board.unequipLocal("bob", "shield");
    assertEquals(bob, board.getPlayer("bob"));
    assertEquals(List.of(ALICE), sent);
    assertEquals(2, events.size());
  }

  @ParameterizedTest
  @CsvSource({"sword", "shield", "shoes"})
  void equipsEachItemOnce(String item) {
    board.equipLocal("alice", item);
    board.equipLocal("alice", item);
    assertChange(ALICE, new PlayerState("alice", "warrior", 2, 3, List.of(item)), 2);
  }

  @Test void removesMiddleItemAndPreservesRemainingOrder() {
    PlayerState equipped = new PlayerState("alice", "warrior", 2, 3,
        List.of("sword", "shield", "shoes"));
    board.addLocalPlayer(equipped);
    board.unequipLocal("alice", "shield");
    board.unequipLocal("alice", "shield");
    assertChange(equipped, new PlayerState("alice", "warrior", 2, 3,
        List.of("sword", "shoes")), 3);
  }

  @Test void unsupportedAndMissingEquipmentLeaveStateUnchanged() {
    board.equipLocal("alice", "helmet");
    board.unequipLocal("alice", "shoes");
    assertChange(null, ALICE, 1);
  }

  @Test void movementPreservesEquipment() {
    PlayerState equipped = new PlayerState("alice", "warrior", 2, 3, List.of("shoes"));
    board.addLocalPlayer(equipped);
    board.moveLocal("alice", 0, 1);
    assertChange(equipped, new PlayerState("alice", "warrior", 2, 4, List.of("shoes")), 3);
  }

  private void assertChange(PlayerState oldState, PlayerState newState, int count) {
    assertEquals(newState, board.getPlayer(newState.player()));
    assertEquals(count, sent.size());
    assertEquals(newState, sent.getLast());
    assertEquals(count, events.size());
    PropertyChangeEvent event = events.getLast();
    assertSame(board, event.getSource());
    assertEquals(Blackboard.PLAYERS, event.getPropertyName());
    assertEquals(oldState, event.getOldValue());
    assertEquals(newState, event.getNewValue());
  }
}
