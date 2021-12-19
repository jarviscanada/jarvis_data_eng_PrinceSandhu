CREATE TABLE IF NOT EXISTS PUBLIC.host_info(
    id SERIAL NOT NULL PRIMARY KEY,
    hostname VARCHAR NOT NULL UNIQUE,
    cpu_number INT NOT NULL,
    cpu_architecture VARCHAR NOT NULL,
    cpu_model VARCHAR NOT NULL,
    cpu_mhz FLOAT NOT NULL,
    L2_cache INT NOT NULL,
    total_mem INT NOT NULL,
    "timestamp" TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.host_usage(
"timestamp" TIMESTAMP NOT NULL,
host_id INT REFERENCES host_info(id),
memory_free FLOAT NOT NULL,
cpu_idle INT NOT NULL,
cpu_kernel INT NOT NULL,
disk_io INT NOT NULL,
disk_available FLOAT NOT NULL
);