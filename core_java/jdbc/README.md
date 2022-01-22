# Introduction
The JDBC App of the Core Java project has been implemented to utilize the JDBC API to execute an assortment of queries on a PostgreSQL
database that has been provisioned by Docker. The application performs various CRUD (create, read, update, and delete) operations on the database.

Technologies utilized:
- Java
- PostgreSQL
- Docker
- Maven
- DBeaver

# Implementation
## ER Diagram
ER diagram

## Design Patterns
Two accepted design patterns typically used for accessing databases in Java include the Data Access Object (DAO) and the Respository Design patterns. 
The JDBC App implements the Data Access Object pattern.\
\
**Data Access Object**: Through the means of an abstract Application Programming Interface, the Data Access Object is a structural design pattern that
enables one to isolate the application/business layer from the persistence layer. Thus, the API effectively hides all complexities involved in performing
CRUD operations in the underlying storage mechanism, which permits both layers to evolve independently of one another.\
\
**Repository**: Similar to the DAO pattern, the repository pattern effectively isolates the application/business layer from the persistence layer.
...

# Test
How you test your app against the database? (e.g. database setup, test data set up, query result)
