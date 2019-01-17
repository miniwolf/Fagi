if "%1" == "build" ( goto :build )
if "%1" == "clean" ( goto :clean )
if "%1" == "test" ( goto :test )
echo "First argument should be the method name (build/clean)"
EXIT /B 0

:build

set javac=%2
mkdir out

cd shared
dir /s /B *.java > ../shared.txt
cd ..
%javac% -d ./out @shared.txt -cp external/gson-2.7.jar

cd fagiClient
dir /s /B *.java > ../fagiClient.txt
cd ..
%javac% -d ./out -classpath ./out;external/javafx/win/lib/* @fagiClient.txt

cd fagiServer
dir /s /B *.java > ../fagiServer.txt
cd ..
%javac% -d ./out -classpath ./out;external/gson-2.7.jar @fagiServer.txt

cd test
dir /s /B *.java > ../test.txt
cd ..

%javac% -d ./out -classpath ./out;external/javafx/win/lib/*;test/lib/* @test.txt

EXIT /B 0

:test

set java=%2
cd out
%java% -jar ../test/lib/junit-platform-console-standalone-1.4.0-M1.jar --class-path .:../external/javafx/win/lib/javafx.base.jar:../external/javafx/win/lib/javafx.controls.jar:../external/javafx/win/lib/javafx.fxml.jar:../external/javafx/win/lib/javafx.graphics.jar:../external/javafx/win/lib/javafx.media.jar:../external/javafx/win/lib/javafx.swing.jar:../external/javafx/win/lib/javafx.web.jar:../external/javafx/win/lib/javafx-swt.jar:../test/lib/mockito-core-2.20.0.jar:../test/lib/testfx-core-4.0.15-alpha.jar:../test/lib/testfx-junit5-4.0.15-alpha.jar:../test/lib/api-guardian-api-1.0.0.jar:../test/lib/byte-buddy-1.8.13.jar:../test/lib/byte-buddy-agent-1.8.13.jar:../test/lib/hamcrest-core-1.3.jar:../test/lib/java-hamcrest-2.0.0.0.jar:../test/lib/junit-jupiter-api-5.4.0-M1.jar:../test/lib/junit-platform-commons-1.4.0-M1.jar:../test/lib/objenesis-2.6.jar:../test/lib/opentest4j-1.1.1.jar:../test/lib/testfx-junit-4.0.15-alpha.jar:../fagiClient/src:../fagiServer/src:../shared/src:../test/src:../fagiClient/resources --scan-classpath
cd ..

EXIT /B 0

:clean
FOR /D %%p IN ("out\*.*") DO rmdir "%%p" /s /q
rmdir "out"
del fagiClient.txt
del fagiServer.txt
del shared.txt
del test.txt

EXIT /B 0