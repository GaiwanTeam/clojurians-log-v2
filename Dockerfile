FROM clojure:openjdk-11-tools-deps-slim-bullseye

WORKDIR /src

COPY ./deps.edn /src/deps.edn

RUN clojure -P

COPY . /src

CMD ["clojure", "-A:dev", "-X:run-prod"]
