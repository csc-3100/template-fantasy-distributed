package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
class PlayerCodecTest {
  @Test void encodedJsonContainsPlayer() { assertTrue(PlayerCodec.encode(new PlayerState("alice","warrior",1,2,List.of())).contains("alice")); }
  @Test void decodeRestoresCoordinates() { PlayerState p=PlayerCodec.decode("{\"player\":\"bob\",\"type\":\"warrior\",\"x\":4,\"y\":7,\"equipment\":[]}"); assertAll(()->assertEquals(4,p.x()),()->assertEquals(7,p.y())); }
  @Test void roundTripPreservesEquipment() { PlayerState p=new PlayerState("alice","warrior",1,2,List.of("sword")); assertEquals(p,PlayerCodec.decode(PlayerCodec.encode(p))); }
  @Test void malformedJsonIsRejected() { assertThrows(IllegalArgumentException.class,()->PlayerCodec.decode("not json")); }
}
