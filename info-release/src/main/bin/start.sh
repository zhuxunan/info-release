###启动  

#!/bin/sh  

moduleName="inforelease-0.0.1-SNAPSHOT"

nohup java -jar ./$moduleName.jar -server -Xms512m -Xmx1024m  > ./run.log 2>&1 &

