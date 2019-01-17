#!/bin/bash

function build () {
	cd $1
	find $PWD -name "*.java" > ../$1.txt
	cd ..
	javac -d ./out @$1.txt -cp $2
}

mkdir out
javac=$1

build shared external/gson-2.7.jar
build fagiClient ./out:external/javafx/osx/lib/*
build fagiServer ./out:external/gson-2.7.jar
build test ./out:external/javafx/osx/lib/*:test/lib/*

function clean () {
	rm -rf out
	rm fagiClient.txt
	rm fagiServer.txt
	rm shared.txt
	rm test.txt
}