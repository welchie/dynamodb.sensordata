# DynamoDB SensorData (Spring-boot) App

A project to store data from a temperature sensor in DynamoDB.

## Architecture
Spring Boot application

- config - DynamoDBConfig
- controller - RestController classes
- model - DynamoDB Beans
- repositories - Spring CRUD Repositories
- Application.java - main Spring Boot class.

### Latest Updates
- 0.0.3-SNAPSHOT
  - Added Swagger UI integration -http://localhost:8080/swager-ui/index.html
  - Tidied up pom.xml properties.
- 0.0.4-SNAPSHOT
  - Added Service layer
  - Added Rest Integration tests
  - Added delete record functionality
- 0.0.5-SNAPSHOT
  - Added Docker  
### TO DO
- ~Rest Integration tests~
- ~Delete record~
- Delete table
- Secure admin functions
- ~Version reporting~
- Postman - test scripts.
- Dockerize application
- ~Use DynamoDB Local form Docker container for integration tests.~
- JWT Tokens for Security
- Swagger Open API Document methods
