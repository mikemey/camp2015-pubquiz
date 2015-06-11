if [ -d "target" ]; then
  sleep 7
else
  sleep 15
fi
open "http://localhost:8080"