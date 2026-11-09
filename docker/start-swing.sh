#!/usr/bin/env bash
set -euo pipefail

pids=()
cleanup() {
  if (( ${#pids[@]} )); then
    kill "${pids[@]}" 2>/dev/null || true
    wait "${pids[@]}" 2>/dev/null || true
  fi
}
trap cleanup EXIT
trap 'exit 143' TERM
trap 'exit 130' INT

Xvfb "$DISPLAY" -screen 0 1024x768x24 -nolisten tcp &
pids+=("$!")
for ((attempt = 0; attempt < 50; attempt++)); do
  if xdpyinfo >/dev/null 2>&1; then break; fi
  sleep 0.1
done
xdpyinfo >/dev/null

fluxbox &
pids+=("$!")
x11vnc -display "$DISPLAY" -localhost -rfbport 5900 -forever -shared -nopw &
pids+=("$!")
websockify --web /usr/share/novnc 0.0.0.0:6080 127.0.0.1:5900 &
pids+=("$!")
java -jar /app/game.jar &
pids+=("$!")

# Exit if any required process stops, allowing Docker to report the failure.
wait -n "${pids[@]}"
