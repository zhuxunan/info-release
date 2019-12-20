#!stop.sh
#!/bin/bash
echo "Stopping SpringBoot Application for CMP"
moduleName="inforelease-0.0.1-SNAPSHOT"
pid=`ps -ef | grep $moduleName.jar | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
#!kill -9 强制终止
   echo "kill -9 的pid:" $pid
   kill -9 $pid
fi
