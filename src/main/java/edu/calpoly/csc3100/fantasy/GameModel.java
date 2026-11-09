package edu.calpoly.csc3100.fantasy;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class GameModel {
  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
  private final Map<String,PlayerState> players = new LinkedHashMap<>();
  private final GamePublisher publisher;
  public GameModel(GamePublisher publisher) { this.publisher = Objects.requireNonNull(publisher); }
  public void addListener(PropertyChangeListener listener) { changes.addPropertyChangeListener(listener); }
  public PlayerState getPlayer(String username) { return players.get(username); }
  public Collection<PlayerState> players() { return List.copyOf(players.values()); }
  public void addLocalPlayer(PlayerState state) {
    players.put(state.player(), state);
    changes.firePropertyChange("players", null, state);
    publisher.publish(state);
  }
  public void moveLocal(String username, int dx, int dy) {
    // TODO: update coordinates, notify observers, publish exactly once.
  }
  public void applyRemote(PlayerState state) {
    // TODO: update remote state and notify observers WITHOUT publishing.
  }
  public void equipLocal(String username, String item) {
    // TODO: add equipment, notify observers, publish exactly once.
  }
}
