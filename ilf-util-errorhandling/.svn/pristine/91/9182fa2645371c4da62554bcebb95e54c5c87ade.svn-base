f [ $# -ne 1 ]
  then
    echo "Syntax is: ./stop.sh [environment SBX/DEV/TST/ACP/EDU/PRD]"
else
        fileName=start.sh-$1.pid
        kill `cat $fileName`
        rm -rf $fileName
fi
