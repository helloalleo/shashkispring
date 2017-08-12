#!/bin/bash

cd sharedmodule
./gradlew clean build bintrayUpload

cd ../boardservice
./gradlew clean build copyRuntimeLibs batchZip
./sls deploy

cd ../wikiservice
./gradlew clean build copyRuntimeLibs batchZip
./sls deploy
