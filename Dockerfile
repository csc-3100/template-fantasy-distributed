FROM eclipse-temurin:21-jre-noble

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       fluxbox fonts-dejavu-core novnc procps websockify x11-utils x11vnc xvfb \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --create-home fantasy
WORKDIR /app
COPY target/fantasy-distributed-1.0-SNAPSHOT.jar game.jar
COPY --chmod=755 docker/start-swing.sh /usr/local/bin/start-swing

USER fantasy
ENV DISPLAY=:99
EXPOSE 6080
ENTRYPOINT ["/usr/local/bin/start-swing"]
