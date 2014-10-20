clear

if [ $# -ne 1 ]
  then
    echo "Syntax is: ./start.sh [environment SBX/DEV/TST/ACP/EDU/PRD]"
else
   env=`echo $1 | sed 's/.*/\L&/g'`
   dir=`pwd`
   echo "logfile present in: /kvk/logs/kvk-dp-errorhandling-$env.out "
   nohup java -Dlog4j.configuration=file:$dir/properties/$env-log4j.properties -jar errorhandling-1.0.jar $1 >/dev/null 2>&1 &
   echo $!>$0-$1.pid
   tail -f /kvk/logs/kvk-dp-errorhandling-$env.out
fi