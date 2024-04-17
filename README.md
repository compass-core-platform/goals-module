# Goals Module

# Setup Instructions
## Prerequisites
- Docker installed on your system
- Java 11

## Setup Steps

1. Clone the repository:
    ```bash
    git clone https://github.com/compass-core-platform/goals-module.git
    cd goals-module
    ```

2. Create a `docker-compose.yml` file in the root directory of the project with the following content:

   ```yaml
   version: '3.8'
   
   services:
     postgres:
       image: postgres
       restart: always
       environment:
         POSTGRES_DB: your_database_name
         POSTGRES_USER: your_username
         POSTGRES_PASSWORD: your_password
       ports:
         - "5432:5432"
   ```
   ```bash
    docker-compose up -d
   ```

## Database Schema

### Data Node Table

```sql
CREATE TABLE datanode (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR,
    description VARCHAR,
    nodeType VARCHAR,
    code VARCHAR,
    createdOn VARCHAR,
    createdBy VARCHAR,
    updatedOn VARCHAR,
    updatedBy VARCHAR
);
```
### Entity Data Node Table
```sql
CREATE TABLE entitynode (
    id BIGSERIAL PRIMARY KEY,
    dataNodeId BIGINT,
    entityId BIGINT,
    nodeType VARCHAR,
    entityType VARCHAR,
    createdOn VARCHAR,
    createdBy VARCHAR,
    updatedOn VARCHAR,
    updatedBy VARCHAR
);
```
### Entity Property Table
```sql
CREATE TABLE property (
    id BIGSERIAL PRIMARY KEY,
    entityNodeId BIGINT,
    propertyName VARCHAR,
    propertyValue VARCHAR
);
```
### Data Node Relations Table
```sql
CREATE TABLE relations (
    id BIGSERIAL PRIMARY KEY,
    parentId BIGINT,
    childId BIGINT
);
```
# application.properties
```properties
spring.application.name=goals

# DB properties
spring.datasource.url=jdbc:postgresql://{host}/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```
