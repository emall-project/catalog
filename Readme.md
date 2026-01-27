**# Catalog Service (E-mall Microservice)

The **Catalog Service** is a microservice in the **E-mall System** responsible for managing catalog-related data (e.g., products/items metadata). It runs as a standalone Spring Boot service backed by PostgreSQL, and it can also be started as part of the full E-mall microservices stack.

---

## Tech Stack

* **Java / Spring Boot**
  * **PostgreSQL**
  * **Docker & Docker Compose**

---

## Running the Service


### 1) Run Campaigns Service in development

- from pgadmin
  - create user with user name `e-mall` and password `root`
  - create database with name `campaigns` and add `e-mall` as owner of this database
- then from inteliji
  - go to `Edit Configuration` window
  - set `dev` as the `Active Profile`


#### Ports


| Component   | Port (Host) | Port (Container) |
| ----------- |-------------| ---------------- |
| campaigns App | 8082        | 8080             |

---

### 2) Run the Full E-mall System

If you want to run the whole E-mall system (gateway + all microservices), follow the instructions in the **Gateway repository**:

* [https://github.com/TargetOne-System/gateway](https://github.com/TargetOne-System/gateway)

---

## Environment Variables

The service uses the following environment variables (as configured in Docker Compose):

### PostgreSQL

* `POSTGRES_DB=catalog-db`
  * `POSTGRES_USER=e-mall`
  * `POSTGRES_PASSWORD=root`

### Spring Boot (Catalog App)

* `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/catalog-db`
  * `SPRING_DATASOURCE_USERNAME=e-mall`
  * `SPRING_DATASOURCE_PASSWORD=root`
  * `SPRING_JPA_HIBERNATE_DDL_AUTO=update`


---

## Notes

* The database data is persisted using the `catalog_data` Docker volume.
  * The service connects to Postgres using the hostname `postgres` (the Docker Compose service name).
  * `SPRING_JPA_HIBERNATE_DDL_AUTO=update` will automatically update the schema based on entities. For production, you may want to switch to migrations (Flyway/Liquibase).

---**
