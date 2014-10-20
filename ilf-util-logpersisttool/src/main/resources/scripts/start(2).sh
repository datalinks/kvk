clear
if [ $# -ne 1 ]
  then
    echo "Syntax is: ./start.sh [environment SBX/DEV/TST/ACP/FUN/PRD]"
else
   echo "logfile present in: /kvk/logs/kvk-dp-logpersist.out " 
   nohup java -jar logpersisttool-1.0.jar $1 >/dev/null 2>&1 &
   echo $!>$0.pid
   tail -f /kvk/logs/kvk-dp-logpersist.out
fi
