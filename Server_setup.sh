#!/bin/bash

# Change this to your netid
netid=bxw170030

# Root directory of your project
PROJDIR=$HOME/AOS_Project1

# Directory where the config file is located on your local system
CONFIGLOCAL=$HOME/AOS_Project1/server_list.txt

# Directory your java classes are in
BINDIR=$PROJDIR/src

# Your main project class
PROG = main

n=0
f=1
cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    echo $i
    echo $n	
    while [[ $n -lt $i ]]
    do
    	read line
	echo $line
    	p=$( echo $line | awk '{ print $1 }' )
	echo $p
        host="$( echo $line | awk '{ print $2 }' ).utdallas.edu"
	echo $host
	
		
	tp="$PROJDIR/s$f"
	echo $tp
        alias path="cd $tp"
	
	if [ $p == "1" ] 
	then
	   status="a"
	else 
	   status="p"
	fi

	echo $status

      konsole -noclose -e  ssh $netid@$host &

      n=$(( n + 1 ))
      f=$(( f + 1 ))
    done
)

