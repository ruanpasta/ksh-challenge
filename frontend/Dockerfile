FROM clojure:tools-deps

RUN apt-get update && apt-get install -y curl gnupg --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y nodejs --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 8000

CMD ["npx", "shadow-cljs", "watch", ":web"]
