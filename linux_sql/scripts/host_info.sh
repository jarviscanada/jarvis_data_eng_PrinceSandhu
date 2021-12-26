#!/bin/bash

#Set up arguments.
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#Validate number of arguments.
if [ $# -ne 5 ]; then
  echo 'Error: Illegal number of parameters.'
  exit 1
fi

#Save machine statistics to variables.
lscpu_out='lscpu'
mem_info=$(cat /proc/meminfo)

#Retrieve hardware specification variables.
hostname=$(hostname -f)
cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out" | egrep "^Model:" | awk '{print $2}' | xargs)
cpu_mhz=$(echo "$lscpu_out" | egrep "^CPU MHz:" | awk '{print $3}' | xargs)
l2_cache=$(echo "$lscpu_out" | egrep "^L2 cache:" | awk '{print $3}' | xargs)
total_mem=$(echo "$mem_info" | egrep "^MemTotal:" | awk '{print $2}' | xargs)
timestamp=$(date)

#Insert into PSQL host_info.
statement="INSERT INTO host_info(
    hostname,
    cpu_number,
    cpu_architecture,
    cpu_model,
    cpu_mhz,
    l2_cache,
    total_mem,
    timestamp
    )VALUES(
      '$hostname',
	    '$cpu_number',
	    '$cpu_architecture',
	    '$cpu_model',
	    '$cpu_mhz',
	    '$l2_cache',
	    '$total_mem',
	    '$timestamp'
	  )"

#Connect to PSQL and execute insert statement.
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?