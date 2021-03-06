
## Prepare Index

```bash
docker-compose up
curl -XPUT http://localhost:9200/jobs -H "Content-Type: application/json" --data "@index/mapping.json"
./mvnw install -DskipTests && ./mvnw spring-boot:run -pl index
curl -XPOST http://localhost:8083/connectors -H "Content-Type: application/json" --data "@index/es-sink-connector.json"
```

## Generate test data

```bash
docker-compose up
curl -XPOST http://localhost:8083/connectors -H "Content-Type: application/json" --data "@index/datagen-connector.json"
curl -XPUT http://localhost:9200/jobs -H "Content-Type: application/json" --data "@index/mapping.json"
curl -XPOST http://localhost:8083/connectors -H "Content-Type: application/json" --data "@index/es-sink-connector.json"
```

## Run search-api

```bash
docker-compose up
./mvnw install -DskipTests && ./mvnw spring-boot:run -pl web
```

Graphiql is accesible under root path [click](http://localhost:8080/)

## TODO

* improve test data
* test kotlin-jpa plugin
* implement paging