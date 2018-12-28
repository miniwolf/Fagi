#!/bin/bash

cd out
$1 -cp .:../test/lib/*:../external/javafx/linux/lib/*:../fagiClient/src:../fagiClient/resources org.junit.runner.JUnitCore com.fagi.MasterTest
cd ..