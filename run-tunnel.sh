#!/usr/bin/bash

directory=$1

if [[ -z $directory ]]
then
    directory="test-out"
fi

cd $directory
path=`pwd`

ngrok http "file://$path" &

sleep 1
url=`curl -s "http://localhost:4040/api/tunnels" | sed 's/^.*command_line",//' | sed 's/,"proto":"https",.*$//' | sed 's/"//g' | sed 's/^public_url://'`
wslview $url &> /dev/null

wait $!
