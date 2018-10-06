#!/bin/sh
#shell call java program
echo %JAVA_HOME
java -Xms50m -Xmx250m -jar  cjnet-1.0.jar %1 %2 %3 %4 %5 %6 %7 %8 %9  
 -Dfile.encoding=utf-8
