clear
echo "logfile present in: /tmp/kvk-dp-logpersist.out " 
nohup java -jar logpersisttool-1.0.jar >/dev/null 2>&1 &
echo $!>$0.pid