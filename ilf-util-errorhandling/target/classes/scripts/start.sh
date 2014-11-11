clear
echo "logfile present in: /tmp/kvk-dp-errorhandling.out " 
nohup java -jar errorhandling-1.0.jar >/dev/null 2>&1 &
echo $!>$0.pid
