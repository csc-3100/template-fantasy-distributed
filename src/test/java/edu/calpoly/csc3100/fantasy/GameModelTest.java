package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
class GameModelTest {
  private static final class FakePublisher implements GamePublisher {
    final List<PlayerState> sent = new ArrayList<>();
    public void publish(PlayerState state) { sent.add(state); }
  }
  private static PlayerState alice() { return new PlayerState("alice","warrior",2,3,List.of()); }
  @Test void localJoinPublishes() { FakePublisher p=new FakePublisher(); GameModel m=new GameModel(p); m.addLocalPlayer(alice()); assertEquals(1,p.sent.size()); }
  @Test void movementChangesPosition() { GameModel m=new GameModel(s->{}); m.addLocalPlayer(alice()); m.moveLocal("alice",1,0); assertEquals(3,m.getPlayer("alice").x()); }
  @Test void movementPreservesOtherCoordinate() { GameModel m=new GameModel(s->{}); m.addLocalPlayer(alice()); m.moveLocal("alice",1,0); assertEquals(3,m.getPlayer("alice").y()); }
  @Test void movementPublishesOnce() { FakePublisher p=new FakePublisher(); GameModel m=new GameModel(p); m.addLocalPlayer(alice()); m.moveLocal("alice",1,0); assertEquals(2,p.sent.size()); }
  @Test void movementNotifiesObserver() { GameModel m=new GameModel(s->{}); m.addLocalPlayer(alice()); AtomicInteger count=new AtomicInteger(); m.addListener(e->count.incrementAndGet()); m.moveLocal("alice",1,0); assertEquals(1,count.get()); }
  @Test void remoteStateDoesNotPublish() { FakePublisher p=new FakePublisher(); GameModel m=new GameModel(p); m.applyRemote(new PlayerState("bob","warrior",4,5,List.of())); assertTrue(p.sent.isEmpty()); }
  @Test void remoteStateUpdatesPlayer() { GameModel m=new GameModel(s->{}); m.applyRemote(new PlayerState("bob","warrior",4,5,List.of())); assertEquals(4,m.getPlayer("bob").x()); }
  @Test void remoteStateNotifiesObserver() { GameModel m=new GameModel(s->{}); AtomicInteger count=new AtomicInteger(); m.addListener(e->count.incrementAndGet()); m.applyRemote(alice()); assertEquals(1,count.get()); }
  @Test void equipmentAddsItem() { GameModel m=new GameModel(s->{}); m.addLocalPlayer(alice()); m.equipLocal("alice","sword"); assertTrue(m.getPlayer("alice").equipment().contains("sword")); }
  @Test void equipmentPublishesOnce() { FakePublisher p=new FakePublisher(); GameModel m=new GameModel(p); m.addLocalPlayer(alice()); m.equipLocal("alice","shield"); assertEquals(2,p.sent.size()); }
}
