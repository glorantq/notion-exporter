#!/usr/bin/bash

#
#      Copyright (C) 2021 Gerber Lóránt Viktor
#
#      This program is free software: you can redistribute it and/or modify
#      it under the terms of the GNU General Public License as published by
#      the Free Software Foundation, either version 3 of the License, or
#      (at your option) any later version.
#
#      This program is distributed in the hope that it will be useful,
#      but WITHOUT ANY WARRANTY; without even the implied warranty of
#      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#      GNU General Public License for more details.
#
#      You should have received a copy of the GNU General Public License
#      along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

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