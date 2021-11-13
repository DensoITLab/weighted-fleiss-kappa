@echo off

set CLASSPATH="../jar/iaa-1.0.0.jar;../jar/picocli/picocli-4.6.2.jar;../jar/poi/poi-5.1.0.jar;../jar/poi/poi-ooxml-5.1.0.jar;../jar/poi/poi-ooxml-lite-5.1.0.jar;../jar/poi/lib/commons-collections4-4.4.jar;../jar/poi/lib/commons-io-2.11.0.jar;../jar/poi/lib/log4j-api-2.14.1.jar;../jar/poi/ooxml-lib/commons-compress-1.21.jar;../jar/poi/ooxml-lib/commons-logging-1.2.jar;../jar/poi/ooxml-lib/curvesapi-1.06.jar;../jar/poi/ooxml-lib/xmlbeans-5.0.2.jar;../jar/log4j/log4j-core-2.14.1.jar"
set DIC_DIR="../res/dic"
set COMMAND=iaa
set CATEGORY=INT
set A_1="W1"
set A_2="W2"
set INPUT_DIR="data"
set LOCALE=ja

java -Dfile.encoding=UTF-8 -cp %CLASSPATH% jp.co.d_itlab.dbdc.tool.IAATool -s %COMMAND% -dic %DIC_DIR% -c %CATEGORY% -a1 %A_1% -a2 %A_2% -i %INPUT_DIR% -l %LOCALE%