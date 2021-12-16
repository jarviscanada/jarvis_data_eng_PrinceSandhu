#! /bin/sh

#Capture CLI arguments.
cmd=$1
db_username=$2
db_password=$3

#Start Docker.
sudo systemctl status docker || sudo systemctl start docker

#Check container status.
container_status=$(docker container inspect -f '{{.State.Status}}' jrvs-psql)

#User switch case to handle create|stop|start options.
case $cmd in
  create)
    #Check if container is already created.
    if [ -n "$container_status" ]; then
      echo 'Container already exists.'
      exit 1
    fi

    #Check # of CLI args.
    if [ $# -ne 3 ]; then
      echo 'Create requires username and password.'
      exit 1
    fi

    #Create container.
    echo 'creating container'
    #Create new volume if it does not exist.
    docker volume create pgdata
    #Create a container using psql image with name jrvs-psql.
    docker run --name jrvs-psql -e POSTGRES_USER="$db_username" -e POSTGRES_PASSWORD="$db_password" -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:9.6-alpine
    exit $?
    ;;

  start|stop)
    #Check instance status; exit 1 if container has not been created.
    if [ -z "$container_status" ]; then
      echo "Container already exists."
      exit 1
    fi

    #If the container is stopped, start it.
    if [ "$container_status" = "exited" ]; then
      echo "Starting container."
      docker container start jrvs-psql
      exit 0;
    fi

    #If the container is running, stops it.
    if [ "$container_status" = "running" ]; then
      echo "Stopping container."
      docker container stop jrvs-psql
      exit 0;
    fi
	  ;;
  *)
	  echo 'Illegal command'
	  echo 'Commands: start|stop|create'
	  exit 1
	  ;;
esac