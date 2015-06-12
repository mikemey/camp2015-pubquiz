PROG=sbt

SBT_AV=`which sbt`
if [ -z $SBT_AV ]; then
  echo sbt not available
  ACT_AV=`which activator`
  if [ -z $ACT_AV ]; then
    echo activator also not available. exiting.
    exit
  else
    PROG=activator
  fi
fi

if [ -z $1 ]; then
  read -p " Please enter a username: " USER
else
  USER=$1
fi

ADDRESS=$(ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1')
$PROG "run-main Rest" -Dakka.remote.netty.tcp.hostname=$ADDRESS -Dakka.participant-name=$USER
