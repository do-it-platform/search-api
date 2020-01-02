
## Prepare Index

```bash
docker-compose up
curl -XPUT http://localhost:9200/jobs -H "Content-Type: application/json" --data "@index/mapping.json"
./mvnw install -DskipTests && ./mvnw spring-boot:run -pl index
curl -XPOST http://localhost:8083/connectors -H "Content-Type: application/json" --data "@index/es-sink-connector.json"
```

## TODO

