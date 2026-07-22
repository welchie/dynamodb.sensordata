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
- 0.0.6-SNAPSHOT
  - Added Name field
- 0.0.7-SNAPSHOT
  - Added GSI on Name/Date
### TO DO
- ~Rest Integration tests~
- ~Delete record~
- Delete table
- Secure admin functions
- ~Version reporting~
- Postman - test scripts.
- ~Dockerize application~
- ~Use DynamoDB Local form Docker container for integration tests.~
- JWT Tokens for Security
- Swagger Open API Document methods
- React App for Chart of data.

## Configuration & Running

The application externalizes credentials (API basic auth, Docker Hub registry, and AWS connection details) via environment variables.

### Local Development Setup

1. **Copy the environment template**:
   ```bash
   cp .env.example .env
   ```
2. **Configure your variables**:
   Open the `.env` file and populate it with your local development credentials.

3. **Export environment variables and run**:
   * **On macOS/Linux**:
     ```bash
     export $(cat .env | grep -v '#' | xargs)
     ./mvnw spring-boot:run
     ```
   * **On Windows (PowerShell)**:
     ```powershell
     Get-Content .env | ForEach-Object {
         if ($_ -and -not $_.StartsWith("#")) {
             $name, $value = $_ -split '=', 2
             [Environment]::SetEnvironmentVariable($name, $value, "Process")
         }
     }
     ./mvnw.cmd spring-boot:run
     ```

