#!/bin/bash
docker ru n --rm -v /var/run/docker.sock:/var/run/docker.sock -e HOST_IP=$1 -e ZK=$2 -i -t wurstmeister/kafka /bin/bash