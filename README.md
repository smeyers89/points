# Points

## Running application locally

If you have Docker setup locally, you can pull and run the latest application image with:

```
docker pull smeyers/points:latest

docker run -p 8080:8080 smeyers/points:latest
```  
OR

You can manually run the application (default port is 8080) with Kotlin/Gradle using:
```
./gradlew bootRun
```

Application Endpoints:
```
GET localhost:8080/transaction/{userId} (View point balance for a user)
```

---

```
POST localhost:8080/transaction/{userId} (Adding/Removing points for a user)
```
Example POST payloads:
```
{ "payer": "DANNON", "points": 1000, "timestamp": "2020-11-02T14:00:00Z" }
{ "payer": "UNILEVER", "points": 200, "timestamp": "2020-10-31T11:00:00Z" }
{ "payer": "DANNON", "points": -200, "timestamp": "2020-10-31T15:00:00Z" }
{ "payer": "MILLER COORS", "points": 10000, "timestamp": "2020-11-01T14:00:00Z" }
{ "payer": "DANNON", "points": 300, "timestamp": "2020-10-31T10:00:00Z" }
```

---

```
PATCH localhost:8080/transaction/{userId} (Spending points for a user)
```
Example PATCH payloads:
```
{ "points": 5000 }
```
