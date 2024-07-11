# asteroids-backend

### Compile and Run
* Clone the repository and execute in the root folder:

```
sbt compile
sbt run
```

### Test
* To test, execute the following command:
```
sbt test
```

### Endpoints
* http://localhost:8080/api/asteroids (with optional query parameters: startDate, endDate and sortByName)
* http://localhost:8080/api/asteroid/{id}

The API adheres to HATEOAS, so you can simply go to the first link above and navigate through the links key of each record

#### Improvement points not implemented due to lack of time:
* More tests, including integration tests
* Nasa data case classes fields could be camelCased. That would require some adjustments in their json marshalling