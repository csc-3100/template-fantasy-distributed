# CSC 3100 — Distributed Fantasy Adventure (A04)

**Instructor starter template — intentionally incomplete.** The code compiles, but instructor acceptance tests are initially expected to fail.

## First step after using this template

Create **one repository per two-student team** using **Use this template**. In your first feature PR, **delete `.starter-template`**. This activates the complete `mvn clean verify` CI gate automatically; you do **not** need to edit the instructor-provided YAML. The instructor template's CI checks compilation only so its incomplete exercises do not appear broken. **Your team's CI must run and pass all tests before merging completed feature PRs.** Early feature PRs may have failing tests while implementation is in progress; do not merge them until required checks pass.

## Team responsibilities and pull requests

| Partner A | Partner B |
|---|---|
| GUI + Observer | MQTT integration |
| Character + equipment behavior | Console controls |
| Game logic unit tests | MQTT message tests + Docker demonstration |

**One substantive PR per assigned feature per student**, each approved by the other partner. Each PR is a development log: ownership, implementation, tests, and evidence. Both partners integrate, review, and demonstrate the result. Write **four additional meaningful JUnit tests**, two per student. Do not modify instructor tests to make them pass.

## Build and run

Requirements: Java 21, Maven, Docker with Compose.

- Compile without running intentionally failing starter tests: `mvn -DskipTests package`
- Run acceptance tests (fail until implemented): `mvn clean verify`
- Start Swing desktop: `mvn exec:java`
- Start Docker console: `docker compose run --build --rm game`
- Use unique player and team names: `PLAYER=bob TEAM=team01 docker compose run --build --rm game`

The Dockerfile, Compose YAML, and GitHub Actions workflow are **provided**. Students implement application behavior, not deployment configuration.

## Game behavior to implement

**GUI:** Render a grid and named players, move one cell per arrow key, show equipment and attributes, and repaint on model changes via Observer.

**Console:** Implement interactive `w`, `a`, `s`, `d`, `sword`, `shield`, `quit` commands, one action per command. Print remote player updates.

**Patterns:** Use Factory for character creation, Decorator for Sword (+5 attack) and Shield (+5 defense), and Observer for model-to-view updates.

**MQTT:** Publish retained player states at QoS 1 on `csc3100/a04/<team>/players/<username>`; subscribe to the team wildcard topic. New players appear in existing clients, remote moves/equipment update other clients, and remote updates **must not** republish. A disconnected player remains at their last known position. Retained state does not prove current online presence.

**Tests:** 23 instructor JUnit tests cover characters, equipment, movement, Observer, JSON, and incoming MQTT message handling **without a live broker**. Add four student-written tests. For integration, demonstrate one GUI client and one interactive Docker console client.

## README deliverables

Replace this starter README with a brief project description, team member responsibilities, execution instructions, test instructions, CI badge, and design decisions. Include a screenshot at `docs/screenshot.png` embedded directly in the README, showing **at least two named players** in the Swing board. Explain why the model is independent of Swing, how Observer and Decorator are used, and why tests do not require the public broker.

## Next week's individual lab

Each student must independently identify the MQTT receiver, trace how the model changes, explain how Observer updates Swing, run and explain a JUnit test, and make a small instructor-requested code change. This is **a separate individual assessment**, not part of A04 homework grading.
