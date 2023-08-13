#!/bin/bash

function printWithTopSpace() {
  printf "*** %-*s ***\n" 50 "$1"
}

function dockerHealth() {
  docker inspect "$1" '--format="{{.State.Health.Status}}"'
}


function waitTillStarts() {
  COUNT=0
  echo "Waiting for mysql to start...\n"
  sleep 10 # give it a minute before we start polling jBPM.
  while [[ $(dockerHealth "mysql") != "\"healthy\"" ]]; do
    if [[ ${COUNT} -ge 15 ]]; then
      printf "mysql failed to start. Current status [%s].\n" "$(dockerHealth "mysql")" && exit 1
    fi
    ((COUNT++))
    sleep 2
  done
  printf "mysql started with status [%s].\n" "$(dockerHealth "mysql")"
}



printWithTopSpace "Pulling mysql docker image..."

docker-compose up &

waitTillStarts

exit 0