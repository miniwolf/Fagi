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
%java% -cp .;../test/lib/*;../external/javafx/win/lib/*;../fagiClient/src;../fagiClient/resources org.junit.runner.JUnitCore com.fagi.MasterTest
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