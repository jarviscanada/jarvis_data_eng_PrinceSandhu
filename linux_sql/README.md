# Linux Cluster Monitoring Agent

# Introduction
The Linux Cluster Monitoring Agent enables users to monitor the individual Linux hosts/nodes of a system which have been connected internally via a switch.
The individual nodes initially provide hardware specifications (architecture, model, speed, memory, etc.) and, with the use of `crontab`, periodically provide usage data (available memory, kernel usage, disk I/O, etc.), including whether a node has failed (if it provides less than three updates in a five-minute interval). This aforementioned information can aid the LCA team make informed decisions about the system in question.

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
./scripts/host_info.sh localhost 5432 host_agent postgres password

#Insert hardware usage data into the database using host_usage.sh.
./scripts/host_usage.sh localhost 5432 host_agent postgres password

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
### psql_docker.sh
The `psql_docker.sh` script was tested as follows:
````
#Command Line Arguments verification (pass):
./scripts/psql_docker.sh create postgres
Error: Create requires username and password.

#Create Container (pass):
./scripts/psql_docker.sh create postgres password
Error: No such container: jrvs-psql
creating container

#Verify jrvs-psql container exists (pass):
docker container ls -a
CONTAINER ID   IMAGE                 COMMAND                  CREATED          STATUS          PORTS                                       NAMES
f4c36ec57546   postgres:9.6-alpine   "docker-entrypoint.s…"   36 seconds ago   Up 35 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp   jrvs-psql

#Stop running container (pass):
./scripts/psql_docker.sh stop
Stopping container.
jrvs-psql

#Start stopped container (pass):
./scripts/psql_docker.sh start
Starting container.
jrvs-psql
````
### sql.ddl
The `sql.ddl` script was tested as follows:
````
#Create tables in the host_agent database.
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql

#Connect to the host_agent database and verify the tables exist (pass).
host_agent=# \dt


| SCHEMA  | NAME       | TYPE                       | OWNER   |
|---------|------------|----------------------------|----------
| public  | host_info  | not null                   | postgres
| public  | host_usage | Foreign Key (host_info.id) | postgres


#Verify fields in host_info table (pass).
host_agent=# \d host_info

                      Table public.host_info
|      Column     |            Type             |            Modifiers
------------------|-----------------------------|-------------------------------
 id               | integer                     | not null default nextval('host_info_id_seq'::regclass)
 hostname         | character varying           | not null
 cpu_number       | integer                     | not null
 cpu_architecture | character varying           | not null
 cpu_model        | character varying           | not null
 cpu_mhz          | double precision            | not null
 l2_cache         | integer                     | not null
 total_mem        | integer                     | not null
 timestamp        | timestamp without time zone | not null


#Verify fields in host_usage table (pass).
host_agent=# \d host_usage

                    Table public.host_usage
     Column     |            Type             | Modifiers 
----------------|-----------------------------|-----------
 timestamp      | timestamp without time zone | not null
 host_id        | integer                     | not null
 memory_free    | double precision            | not null
 cpu_idle       | integer                     | not null
 cpu_kernel     | integer                     | not null
 disk_io        | integer                     | not null
 disk_available | double precision            | not null
````

### host_info.sh
The `host_info.sh` script was tested by verifying that the corresponding fields from the `lscpu` and `cat /proc/meminfo` commands correctly populate the `host_info.sh` PSQL table:

````
#Print CPU architecture information.
$lscpu

Architecture:          x86_64
CPU op-mode(s):        32-bit, 64-bit
Byte Order:            Little Endian
CPU(s):                2
On-line CPU(s) list:   0,1
Thread(s) per core:    2
Core(s) per socket:    1
Socket(s):             1
NUMA node(s):          1
Vendor ID:             GenuineIntel
CPU family:            6
Model:                 79
Model name:            Intel(R) Xeon(R) CPU @ 2.20GHz
Stepping:              0
CPU MHz:               2200.158
BogoMIPS:              4400.31
Hypervisor vendor:     KVM
Virtualization type:   full
L1d cache:             32K
L1i cache:             32K
L2 cache:              256K
L3 cache:              56320K
NUMA node0 CPU(s):     0,1

#Print memory information to console:
$cat /proc/meminfo

MemTotal:        8005732 kB
MemFree:         7222760 kB
MemAvailable:    7340884 kB

#Create the host_info and host_usage tables:


psql -h localhost -U postgres -d host_agent -f sql/ddl.sql
#Insert into the host_data table of host_agent:


#Verification (pass):
SELECT * FROM host_info;

 id |                 hostname                | cpu_number | cpu_architecture | cpu_model | cpu_mhz | l2_cache | total_mem | timestamp      
----------------------------------------------------------------------------------------------------------------------------------------
 10 | jrvs-remote-desktop-centos7.us-east1-c. |      2     |      x86_64      |     79    | 2200.21 |    266   |  8005732  | 2021-12-26
    |  c.polynomial-land-334415.internal      |            |                  |           |         |          |           |  18:22:38
````

### host_usage.sh
The `host_usage.sh` script was tested by verifying that the corresponding fields from the `vmstat` and `df` commands correctly populate the `host_usage.sh` PSQL table:



# Deployment
...

# Improvements
...
