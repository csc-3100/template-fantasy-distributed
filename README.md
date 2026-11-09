# CSC 3100 — A04 Distributed Fantasy Adventure

> **Instructor starter template — intentionally incomplete.** Students use TDD to make the provided JUnit acceptance tests pass. The application is a small distributed visualization, **not a game engine**.

## Learning goals

Implement Factory, Decorator, and Observer patterns; synchronize two Java Swing clients through MQTT; practice test-driven development and GitHub Actions CI/CD; package a JAR and run the same GUI in Docker through noVNC.

## Required demonstration

Run **two instances of the same Swing application**:

1. Local Swing client. Use the same unique team ID in both commands and a different player name for each instance.

   ```sh
   TEAM=your-team PLAYER=alice mvn exec:java
   ```

2. Containerized Swing client. In a second terminal, package the JAR, build the image, and open the browser desktop.

   ```sh
   mvn -B -DskipTests package
   TEAM=your-team PLAYER=bob docker compose up --build
   ```

   Open [noVNC](http://localhost:6080/vnc.html?autoconnect=1). Click the Swing board to send it keyboard input. The browser endpoint binds to localhost. Run `docker compose down` to stop the container.

Both instances display the same board and both players. Each instance creates and controls its own named player. A movement command changes position by **exactly one tile**. Sword, Shield, and Shoes can be added and removed at runtime; each decorator implements `draw(Graphics2D, int, int)`, delegates to the wrapped character, and draws its equipment. No combat, collision, scoring, game engine, or autonomous actions are required.

## Architecture contract

- **Factory:** creates the base fantasy character.
- **Decorator:** composes Sword, Shield, and Shoes at runtime; draw methods compose visually as well as behaviorally.
- **Blackboard:** extends `PropertyChangeSupport` and owns the authoritative local view of player states. Preserve this inheritance-based lecture design.
- **Observer:** Swing listens to Blackboard changes and repaints on the Swing event-dispatch thread.
- **Publisher:** sends local player-state updates to the public HiveMQ broker.
- **Subscriber:** receives remote player-state updates and updates Blackboard without republishing.
- **MQTT:** `tcp://broker.hivemq.com:1883`, QoS 1, retained state on `csc3100/a04/<team>/players/<username>`; subscribe using the team wildcard. Use unique team and player identifiers. Retained state is not an online-presence indicator; disconnected players remain at their last reported position.

## TDD and submission

The instructor provides most JUnit 5 acceptance tests. Students must **add exactly three meaningful JUnit 5 tests** in a separate `StudentAcceptanceTest.java` file for features of their choice. Keep the instructor tests intact. Implement functionality by making tests pass, and keep tests independent of the live public broker using test doubles. The CI workflow must run tests before packaging the executable JAR, building the Docker image, and smoke-testing the noVNC endpoint and Java process. The instructor provides the Dockerfile, Compose, workflow, and noVNC configuration; students implement application functionality, not infrastructure.

### Starter-template status

The Java starter compiles. Run `mvn test-compile` to compile the starter and tests, then `mvn test` to run the offline acceptance suite. Failures are expected until you complete the TODOs in Blackboard, the decorators, PlayerCodec, and IncomingPlayerHandler. The instructor branch includes a `.starter-template` marker so CI can package and smoke-test the starter while its acceptance tests are intentionally failing. Delete that marker in your first team PR to require the full JUnit suite before packaging and building the image. The CI smoke test uses a private broker and checks the noVNC page, Java process, and Swing window.

### Behavior covered by the instructor tests

- Arrow keys move the local player one tile on a 16-column by 11-row board. Ignore zero, diagonal, multi-tile, and out-of-bounds commands. Local commands can only edit the local player.
- Equipment IDs are `sword`, `shield`, and `shoes`. Add each at most once and preserve the order of remaining items on removal. Ignore unknown IDs and attempts to remove absent items. Sword adds 5 attack, Shield adds 5 defense, and Shoes are visual only.
- Draw the wrapped character first, then draw the overlay within its 40-pixel tile. Keep Sword on the right, Shield on the left, and Shoes below the body so the overlays can be seen together.
- Each changed local snapshot produces one `players` event with the prior and new snapshots and one publish. Remote changes produce the same observer event without a publish. An identical remote snapshot produces no event. The Swing application applies incoming snapshots on the event-dispatch thread.
- The receiver accepts a UTF-8 JSON snapshot only when the team topic and player suffix match, the state is valid, and the player is remote. Reject malformed or incomplete snapshots, unsupported types and equipment, duplicate equipment, and coordinates outside the board. Ignore a bad message and continue receiving.

## Implementation checklist for instructor preparation

- [x] Refactor `GameModel` into observable `Blackboard extends PropertyChangeSupport`.
- [x] Add runtime removal and Shoes; test decorator drawing delegation.
- [x] Separate `Publisher` and `Subscriber`, preserving MQTT protocol and preventing echoes.
- [x] Render decorated players in the shared Swing board and support one-tile commands.
- [x] Replace Docker console mode with Swing + Xvfb + lightweight desktop + noVNC.
- [x] Provide complete instructor acceptance tests and require exactly three additional student tests.
- [x] Update CI to test, package, build container, and smoke-test noVNC.
- [ ] Verify IDE ↔ Docker bidirectional MQTT demonstration.

