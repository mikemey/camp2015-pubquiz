ADDRESS=$(ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
sbt "run-main Rest" -Dakka.remote.netty.tcp.hostname=$ADDRESS -Dakka.participant-name=$1
