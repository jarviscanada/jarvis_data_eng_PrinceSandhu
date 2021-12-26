# Linux Cluster Monitoring Agent

# Introduction
The Linux Cluster Monitoring Agent enables users to monitor the individual Linux hosts/nodes of a system which have been connected internally via a switch.
The individual nodes initially provide hardware specifications and, with the use of `crontab`, periodically provide usage data, including whether a node has failed (if it provides less than three updates in a five-minute interval). This aforementioned information can help the LCA team make informed decisions about the system in question.

Technologies utilized:
- Docker
- Bash
- Postgres SQL

# Quick Start
````
#Create and start the PSQL instance (provisioned using Docker).
./scripts/psql_docker.sh create postgres password
./scripts/psql_docker.sh start

#Create appropriate tables using ddl.sql.
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql

#Insert hardware specifications data into the database using host_info.sh.
./scripts/host_info.sh localhost 5432 host_info postgres password

#Insert hardware usage data into the database using host_usage.sh.
./scripts/host_usage.sh localhost 5432 host_usage postgres paassword

#Crontab setup (host_usage executes every minute).
* * * * * bash /pwd/scripts/host_useage.sh localhost 5432 host_usage postgres password
````
# Implementation
The Linux Cluster Monitoring Agent project has been provisioned using Docker through the `psql_docker.sh` script. Each host/node contains `host_info.sh` and `host_usage.sh`. The former script records the hardware specification and the latter records the resource usage data of their respective hosts. Resource usage data is collected every minute, which is configured with `crontab`. The scripts record the aforementioned data into the `host_info` and `host_usage` tables of the `host_agent` PostgreSQL database. SQL queries are constructed to group hosts by hardware info, average memory usage, and to detect host failure.

## Architecture
...

## Scripts
The following contains a high-level description of each script and its usage.

- `psql_docker.sh`
  - Provisions PSQL instance using Docker.
  - Starts/stops the Docker container containing the PSQL instance.
````
./scripts/psql_docker.sh create db_username db_password
./scripts/psql_docker.sh start | stop
````

- `host_info.sh`
  - Collects host hardware specification data and inserts it into the `host_info` table of the `host_agent` database.
````
./scripts/host_info.sh HOST_NAME PSQL_PORT db_name db_username db_password
````

- `host_usage.sh`
  - Collects host usage data and inserts it into the `host_usage` table of the `host_agent` database.
````
./scripts/host_usage.sh HOST_NAME PSQL_PORT db_name db_username db_password
````

- `crontab`
  - Edit the `crontab` info to execute the `host_usage.sh` script every minute.
````
* * * * * bash [path to host_usage.sh] HOST_NAME PSQL_PORT db_name db_username db_password > /tmp/host_usage.log
````

- `queries.sql`
  - Query 1: Groups host by CPU number and sort by their memory size in descending order. 
  - Query 2: Calculates the percentage of the average memory used over a five minute interval for each host.
  - Query 3: Detects host failure. If updated less than three times in a period of five minutes, the host has failed.

## Database Modeling
The following contains the schema for the `host_info` and `host_usage` tables.

### Host Info 
| NAME             | TYPE       | CONSTRAINT  |
|------------------|------------|-------------|
| id               | SERIAL     | Primary key |
| hostname         | VARCHAR    | not null    |
| cpu_number       | INT        | not null    |
| cpu_architecture | VARCHAR    | not null    |
| cpu_model        | VARCHAR    | not null    | 
| cpu_mhz          | FLOAT      | not null    | 
| l2_cache         | INT        | not null    |
| total_mem        | INT        | not null    |
| timestamp        | TIMESTAMP  | not null    |

### Host Usage 
| NAME           | TYPE      | CONSTRAINT                   |
|----------------|-----------|------------------------------|
| timestamp      | TIMESTAMP | not null                     |
| host_id        | INT       | Foreign Key (host_info.id)   |
| memory_free    | FLOAT     | not null                     |
| cpu_idle       | INT       | not null                     |
| cpu_kernel     | INT       | not null                     | 
| disk_io        | INT       | not null                     | 
| disk_available | FLOAT     | not null                     |

# Test
...

# Deployment
...

# Improvements
...
