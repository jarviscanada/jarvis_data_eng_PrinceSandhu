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
* * * * * bash /pwd/scripts/host_usage.sh localhost 5432 host_usage postgres password > /tmp/host_usage.log
````
# Implementation
The Linux Cluster Monitoring Agent project has been provisioned using Docker through the `psql_docker.sh` script. Each host/node contains `host_info.sh` and `host_usage.sh`. The former script records the hardware specification and the latter records the resource usage data of their respective hosts. Resource usage data is collected every minute, which is configured with `crontab`. The scripts record the aforementioned data into the `host_info` and `host_usage` tables of the `host_agent` PostgreSQL database. SQL queries are constructed to group hosts by hardware info, average memory usage, and to detect host failure.

## Architecture
<img src = './assets/Linux Architectural Diagram.jpg'>


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


  SCHEMA  | NAME       | TYPE                       | OWNER   
|---------|------------|----------------------------|----------
| public  | host_info  | not null                   | postgres
| public  | host_usage | Foreign Key (host_info.id) | postgres


#Verify fields in host_info table (pass).
host_agent=# \d host_info

                      Table public.host_info
       Column     |            Type             |                       Modifiers
------------------|-----------------------------|-------------------------------------------------------
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

#Print memory information:
$cat /proc/meminfo

MemTotal:        8005732 kB
MemFree:         7222760 kB
MemAvailable:    7340884 kB

#Insert into the host_info table of host_agent:
./scripts/host_info.sh localhost 5432 host_agent postgres password

#Verification (pass):
host_agent=#SELECT * FROM host_info;

 id |                 hostname                | cpu_number | cpu_architecture | cpu_model          | cpu_mhz | l2_cache | total_mem | timestamp      
----|-----------------------------------------|------------|------------------|--------------------|---------|----------|-----------|-----------
 10 | jrvs-remote-desktop-centos7.us-east1-c. |      2     |      x86_64      |  Intel(R)Xeon(R)   | 2200.21 |    266   |  8005732  | 2021-12-26
    |  c.polynomial-land-334415.internal      |            |                  |  CPU@2.20GHz       |         |          |           | 18:22:38
````

### host_usage.sh
The `host_usage.sh` script was tested by verifying that the corresponding fields from the `vmstat` and `df` commands correctly populate the `host_usage.sh` PSQL table:

````
#Print virtual memory statistics.
procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu----- -----timestamp-----
 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs  us sy id wa st        UTC
 1  0      0 4694324  2200 1719876   0    0    107    2   105  168  2  0 98  0  0 2021-12-27 01:16:43

#Insert into the host_usage table of host_agent:
./scripts/host_usage.sh localhost 5432 host_agent postgres password

#Verification (pass):
host_agent=# SELECT * FROM host_usage;

 timestamp  | host_id | memory_free | cpu_idle | cpu_kernel | disk_io | disk_available 
------------|---------|-------------|----------|------------|---------|---------------
 2021-12-27 |   13    |    4688     |    98    |     2      |    0    |     782
 2:46:45    |         |             |          |            |         |

````

### queries.sql
The three queries from `queries.sql` were tested as follows: sample values were inserted into the `host_info` and `host_usage` tables, and the output was manually inspected upon running each of the queries.

````
Insert sample values into host_info for Query 1:
INSERT INTO host_info VALUES (1, 'host1', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.20GHz', 2200.00, 256, 4096, ‘2021-12-24 10:45:19’),
                             (5, 'host2', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.20GHz', 2300.00, 256, 2048, '2021-12-25 11:46:20’),
                             (4, 'host3', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.20GHz', 2100.00, 256, 1024, '2021-12-26 12:47:21'),
                             (9, 'host4', 2, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.20GHz', 2000.00, 256, 1024, '2021-12-27 01:48:22'),
                             (6, 'host5', 2, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.20GHz', 1600.00, 256, 512, '2021-12-28 02:49:23');

Query 1: Group hosts by CPU number and sort by their memory size in descending order (within each cpu_number group).
SELECT cpu_number,
       id AS host_id,
       total_mem
FROM host_info
GROUP BY cpu_number, id
ORDER BY total_mem DESC;

Query 1 verification (pass):
cpu_number | host_id | total_mem 
-----------|---------|-----------
         1 |       1 |      4096
         1 |       5 |      2048
         1 |       4 |      1024
         2 |       9 |      1024
         2 |       6 |       512


Insert sample values into host_usage for Query 2:
INSERT INTO host_usage VALUES ('2021-12-28 11:25:00', 1, 200, 92, 3, 0, 24000),
                              ('2021-12-28 11:25:15', 1, 300, 93, 3, 0, 24000),
                              ('2021-12-28 11:25:30', 1, 400, 94, 4, 0, 24000),
                              ('2021-12-28 11:25:45', 1, 500, 95, 4, 0, 24000),
                              ('2021-12-28 11:26:00', 1, 600, 96, 4, 0, 24000),
                              ('2021-12-28 11:26:15', 5, 200, 82, 4, 0, 23000),
                              ('2021-12-28 11:26:30', 5, 300, 83, 4, 0, 23000),
                              ('2021-12-28 11:26:45', 5, 400, 84, 4, 0, 23000),
                              ('2021-12-28 11:27:00', 5, 500, 95, 5, 0, 23000),
                              ('2021-12-28 11:27:15', 5, 600, 86, 5, 0, 23000),
                              ('2021-12-28 11:27:30', 6, 200, 87, 5, 0, 23500),
                              ('2021-12-28 11:27:45', 6, 300, 87, 6, 0, 23500),
                              ('2021-12-28 11:28:00', 6, 400, 88, 6, 0, 23500),
                              ('2021-12-28 11:28:15', 6, 500, 88, 6, 0, 23500),
                              ('2021-12-28 11:28:30', 6, 500, 89, 6, 0, 23500),
                              ('2021-12-28 11:28:45', 9, 200, 96, 4, 0, 23000),
                              ('2021-12-28 11:29:00', 9, 300, 97, 4, 0, 23000);
                              
Query 2: Average used memory in percentage over a 5-minute interval for each host.
SELECT host_usage.host_id,
       host_info.hostname,
       round5(host_usage.timestamp),
       AVG(((host_info.total_mem - host_usage.memory_free)/(host_info.total_mem))*100) as avg_used_mem_percentage
FROM host_usage,
     host_info
WHERE host_usage.host_id = host_info.id
GROUP BY round5(host_usage.timestamp),
         host_usage.host_id,
         host_info.hostname,
         host_info.total_mem
ORDER BY host_usage.host_id,
         round5(host_usage.timestamp);

Query 2 verification (pass):
host_id | hostname |       round5        | avg_used_mem_percentage 
--------|----------|---------------------|------------------------
      1 | host1    | 2021-12-28 11:25:00 |               90.234375
      5 | host2    | 2021-12-28 11:25:00 |                80.46875
      6 | host5    | 2021-12-28 11:25:00 |                25.78125
      9 | host4    | 2021-12-28 11:25:00 |              75.5859375


Query 3: Detect host failure (less than 3 data points in a 5-min interval).
SELECT host_id,
       round5(timestamp) as timestamp,
       COUNT(*) AS num_data_points
FROM host_usage
GROUP BY host_id, round5(timestamp)
HAVING COUNT(*)<3
ORDER BY host_id;

Query 3 verification (pass):
host_id |      timestamp      | num_data_points 
--------|---------------------|----------------
      9 | 2021-12-28 11:25:00 |               2

````

# Deployment
- The PostgreSQL database has been provisioned using Docker.
- The `host_info.sh` script is initially run to records the hardware specification into the database.
- With the help of `crontab`, the `host_usage.sh` script runs periodically (every minute) and records the usage data into the database.
- All corresponding scripts have been stored on GitHub.

# Improvements
1. Create a Bash script to detect any hardware changes and update the `host_info` table accordingly. Would be significantly more convenient for long-term application use.
2. Include more columna (power usage, temperature, etc.) in the `host_usage` table. Would greatly benefit the LCA team in making informed decisions about the system.
3. Include a script to run all commands at once.
