PROG=java

SEED=

if [ -z $1 ]; then
  read -p " Please enter a username: " USER
else
  USER=$1
fi

ADDRESS=$(ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
#$PROG  -Dakka.remote.netty.tcp.hostname=$ADDRESS -Dakka.participant-name=$USER -jar pubquiz-assembly-1.0.jar

if [ $2 ]; then
    $PROG  -Dakka.remote.netty.tcp.hostname=$ADDRESS -Dakka.participant-name=$USER -Dakka.cluster.seed-nodes.0="akka.tcp://ClusterSystem@$2:2552" -jar pubquiz-assembly-1.0.jar
else
    $PROG  -Dakka.remote.netty.tcp.hostname=$ADDRESS -Dakka.participant-name=$USER -jar pubquiz-assembly-1.0.jar
fi
