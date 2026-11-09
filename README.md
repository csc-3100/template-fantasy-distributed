# CSC 3100 — A04 Distributed Fantasy Adventure

> **Instructor starter template — intentionally incomplete.** Students use TDD to make the provided JUnit acceptance tests pass. The application is a small distributed visualization, **not a game engine**.

## Learning goals

Implement Factory, Decorator, and Observer patterns; synchronize two Java Swing clients through MQTT; practice test-driven development and GitHub Actions CI/CD; package a JAR and run the same GUI in Docker through noVNC.

## Required demonstration

Run **two instances of the same Swing application**:

1. Local client from the IDE with `mvn exec:java`.
2. Containerized client with `docker compose up --build`, viewed at `http://localhost:6080/vnc.html`.

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

The instructor provides most JUnit 5 acceptance tests. Students must **add three meaningful tests** for features of their choice, without weakening instructor tests. Implement functionality by making tests pass, and keep tests independent of the live public broker using test doubles. The CI workflow must run tests before packaging the executable JAR, building the Docker image, and smoke-testing the noVNC endpoint and Java process. The instructor provides the Dockerfile, Compose, workflow, and noVNC configuration; students implement application functionality, not infrastructure.

### Starter-template status

**This branch records the approved A04 contract, not a finished runnable template.** The existing `main` implementation still uses `GameModel` composition, a `MqttGateway`, and a console Docker entrypoint; those must be refactored before release. Existing tests and CI also reflect the previous assignment (23 tests and four student tests). Do not distribute this branch as the final A04 starter until implementation, test coverage, and Docker/noVNC verification are complete.

## Implementation checklist for instructor preparation

- [ ] Refactor `GameModel` into observable `Blackboard extends PropertyChangeSupport`.
- [ ] Add runtime removal and Shoes; test decorator drawing delegation.
- [ ] Separate `Publisher` and `Subscriber`, preserving MQTT protocol and preventing echoes.
- [ ] Render decorated players in the shared Swing board and support one-tile commands.
- [ ] Replace Docker console mode with Swing + Xvfb + lightweight desktop + noVNC.
- [ ] Provide complete instructor acceptance tests and require exactly three additional student tests.
- [ ] Update CI to test, package, build container, and smoke-test noVNC.
- [ ] Verify IDE ↔ Docker bidirectional MQTT demonstration.

