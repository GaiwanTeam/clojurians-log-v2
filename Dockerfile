FROM clojure:openjdk-11-tools-deps-slim-bullseye

RUN apt-get update -yq \
    && apt-get install curl gnupg -yq \
    && curl -sL https://deb.nodesource.com/setup_14.x | bash \
    && apt-get install nodejs -yq

WORKDIR /src

COPY ./deps.edn /src/deps.edn

RUN clojure -P

COPY ./package.json /src/package.json
COPY ./package-lock.json /src/package-lock.json
RUN npm install

COPY . /src

RUN npm run release

EXPOSE 8919

CMD ["clojure", "-X:run-prod"]
